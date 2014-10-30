package sv.cmu.edu.weamobile.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import sv.cmu.edu.weamobile.AlertDetailActivity;
import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.Data.GeoLocation;
import sv.cmu.edu.weamobile.PlainAlertDialogActivity;
import sv.cmu.edu.weamobile.Utility.AppConfigurationFactory;
import sv.cmu.edu.weamobile.Utility.Constants;
import sv.cmu.edu.weamobile.Utility.GPSTracker;
import sv.cmu.edu.weamobile.Utility.Logger;
import sv.cmu.edu.weamobile.Utility.WEAPointInPoly;
import sv.cmu.edu.weamobile.Utility.WEAUtil;

public class WEABackgroundService extends Service {
    public static final String FETCH_CONFIGURATION = "sv.cmu.edu.weamobile.service.action.FETCH_CONFIGURATION";
    public static final String SHOW_ALERT = "sv.cmu.edu.weamobile.service.action.SHOW_ALERT";

    private final IBinder mBinder = new LocalBinder();
    private BroadcastReceiver newConfigurationHandler;

    public class LocalBinder extends Binder {
        WEABackgroundService getService() {
            return WEABackgroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("WEA", "WEABackgroundService started at " + WEAUtil.getTimeStringFromEpoch(System.currentTimeMillis() / 1000) );
        Log.d("WEA", "Service onStart called with "+ intent);
        if(intent == null){
            Log.d("WEA", "Intent was null so setting it to FETCH_CONFIGURATION");
            intent = new Intent(getApplicationContext(), WEABackgroundService.class);
            intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        }

        if(newConfigurationHandler == null){
            newConfigurationHandler= new NewConfigurationReceiver(new Handler());
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newConfigurationHandler,
                    new IntentFilter("new-config-event"));

        }

        onHandleIntent(intent);

        return Service.START_NOT_STICKY;
    }

    protected void onHandleIntent(Intent intent) {
        Log.d("WEA", "called with "+ intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_CONFIGURATION.equals(action)) {
                fetchConfiguration();
            }else if(SHOW_ALERT.equals(action)){
                int alertId= intent.getIntExtra(("alertId"),-1);
                alertUsers(alertId);
            }
        }
        AlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void fetchConfiguration() {
        Log.d("WEA", "Got request to fetch new configuration");
        //read configuration and setup up new alarm
        //if problem in getting/receiving configuration, set default alarm

        AppConfigurationFactory.getConfigurationAsync(getApplicationContext());

    }

    private void alertUsers(int alertid){
        showAlertIfInTarget(alertid);
    }

    private void broadcastNewAlert(String message, String polygonEncoded, int alertId){

        showAlert(alertId, 0);
    }

    private void showAlert(int alertId, int alertTYpe) {
        Logger.log("Its the alert time");
        Intent dialogIntent;
        if(alertTYpe == 0){
            dialogIntent = new Intent(getBaseContext(), AlertDetailActivity.class);
        }else
        {
            dialogIntent = new Intent(getBaseContext(), PlainAlertDialogActivity.class);
        }

        dialogIntent.putExtra("item_id", String.valueOf(alertId));
        dialogIntent.putExtra("isDialog", true);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(dialogIntent);
    }

    private void broadcastOutOfTargetAlert(){

        WEANewAlertIntent newConfigurationIntent = new WEANewAlertIntent("New Alert, but out of target", "");
        Log.d("WEA", "Broadcast intent: About to broadcast new Alert");
        getApplicationContext().sendBroadcast(newConfigurationIntent);
    }

    private void newConfigurationReceived(AppConfiguration configuration){

        //two things to be done, shown now or schedule for later if in half an hour
        Alert alert = getAlertRelevantForNow(configuration);
        if(alert != null){
            long currentTime = System.currentTimeMillis()/1000;
            Logger.log("Alarm expected after: "+ (alert.getScheduledForLong() - currentTime)*1000);
            WEAAlarmManager.setupAlarmForAlertAtScheduledTime(getApplicationContext(), alert.getId(), alert.getScheduledForLong()*1000);
        }
    }

    private Alert getAlertRelevantForNow(AppConfiguration config){
        Alert relevantAlert= null;

        Alert [] alerts = config.getAlerts();
        if(alerts.length >0){
            for(Alert alert: alerts){
                try{
                    long currentTime = System.currentTimeMillis()/1000;
                    //only show if not shown before in +60 -1 seconds
                    if((alert.getScheduledForLong()- currentTime) < Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES*60
                            && (alert.getScheduledForLong() - currentTime) > 30){ //just 30 seconds
                        relevantAlert = alert;
                    }

                }catch(Exception ex){
                    Logger.log(ex.getMessage());

                }
            }
        }

        return relevantAlert;
    }

    private void showAlertIfInTarget(int alertId) {
        Logger.log("Show alert if in target for ", String.valueOf(alertId));
        String json = AppConfigurationFactory.getStringProperty(getApplicationContext(), "message");
        AppConfiguration configuration = AppConfiguration.fromJson(json);
        Alert [] alerts = configuration.getAlerts();

        for(Alert alert: alerts){
            if(alert.getId() == alertId){
                GPSTracker tracker = new GPSTracker(this.getApplicationContext());
                GeoLocation location = tracker.getGPSGeoLocation();
                if( WEAPointInPoly.isInPolygon(location, alert.getPolygon())){
                    Logger.log("Present in polygon");
                    broadcastNewAlert(alert.getText(), "1222222233123113", alert.getId());
                }else{
                    broadcastOutOfTargetAlert();
                    Logger.log("Not present in polygon");
                }

                tracker.stopUsingGPS();
            }
        }
    }

    private class NewConfigurationReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewConfigurationReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Extract data included in the Intent
            Logger.log("NewConfigurationReceiver");
            AppConfigurationFactory.setStringProperty(context,
                    "lastTimeChecked",
                    String.valueOf(System.currentTimeMillis()));
            String json = intent.getStringExtra("message");

            if(json.isEmpty()){
                json = AppConfigurationFactory.getStringProperty(context, "message");
            }else{
                AppConfigurationFactory.setStringProperty(context, "message", json);
                WEANewAlertIntent newConfigurationIntent = new WEANewAlertIntent("", "");
                Log.d("WEA", "Broadcast intent: About to broadcast new Alert");
                getApplicationContext().sendBroadcast(newConfigurationIntent);
            }
            AppConfiguration configuration = AppConfiguration.fromJson(json);
            newConfigurationReceived(configuration);
        }

    };
}
