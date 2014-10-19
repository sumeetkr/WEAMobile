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
import sv.cmu.edu.weamobile.Utility.AppConfigurationFactory;
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
        Log.d("WEA", "WEABackgroundService started at " + WEAUtil.getTimeString(System.currentTimeMillis()/1000) );
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
            }else if(intent.getAction()==SHOW_ALERT){
                alertUsers();
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

    private void alertUsers(){
        Intent dialogIntent = new Intent(getBaseContext(), AlertDetailActivity.class);
        dialogIntent.putExtra("item_id", String.valueOf(1));
        dialogIntent.putExtra("isDialog", true);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(dialogIntent);
    }

    private void broadcastNewAlert(String message, String polygonEncoded, int alertId){

        Intent dialogIntent = new Intent(getBaseContext(), AlertDetailActivity.class);
        dialogIntent.putExtra("item_id", String.valueOf(alertId));
        dialogIntent.putExtra("isDialog", true);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(dialogIntent);
    }

    private void newConfigurationReceived(AppConfiguration configuration){

        //two things to be done, shown now or schedule for later if in half an hour
        getAlertRelevantForNow(configuration);
    }

    private void getAlertRelevantForNow(AppConfiguration config){
        Alert [] alerts = config.getAlerts();
        if(alerts.length >0){
            GPSTracker tracker = new GPSTracker(this.getApplicationContext());
            GeoLocation location = tracker.getGeoLocation();
            for(Alert alert: alerts){
                long currentTime = System.currentTimeMillis()/1000;
                //only show if not shown before in +5 -5 seconds
                Logger.log("Alarm expected at: "+ WEAUtil.getTimeString(alert.getScheduledForLong()));
                if(currentTime < (Long.parseLong(alert.getScheduledFor())+1*60) && currentTime > (Long.parseLong(alert.getScheduledFor())-1*60)){
                    Logger.log("Its the alarm time");
                    //Now check the locaton range
                    GeoLocation [] locations = alert.getPolygon();
                    double [] longs = new double[locations.length];
                    double [] lats = new double[locations.length];

                    for(int i = 0; i<locations.length; i++){
                        lats[i]= Double.parseDouble(locations[i].getLat());
                        longs[i] = Double.parseDouble(locations[i].getLng());
                    }

                    Logger.log("Verifying presence in polygon.");
                    boolean inPoly = WEAPointInPoly.pointInPoly(locations.length,lats,longs,Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng()));
                    if(inPoly){
                        Logger.log("Presence in polygon: ", String.valueOf(inPoly));
                        broadcastNewAlert(alert.getText(), "1222222233123113", alert.getId());
//                        broadcastNewAlert(alert.getText(), "1222222233123113", alert.getId());
                    }else{
                        Logger.log("Not present in polygon: ", String.valueOf(inPoly));
                    }
//                }else if(currentTime < (Long.parseLong(alert.getScheduledFor())-1*60)) {
//                    Logger.log("Scheduling alarm for " + String.valueOf(1000*(Long.parseLong(alert.getScheduledFor())-currentTime-60)));
//                    WEAAlarmManager.setupAlarmToWakeUpApplicationAtScheduledTime(getApplicationContext(), (Long.parseLong(alert.getScheduledFor())-currentTime-59)*1000 );
//
//                }else{
//                    Logger.log("No immediate alert expected");
                }
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
                WEANewAlertIntent newAlertIntent = new WEANewAlertIntent(json, "");
                Log.d("WEA", "Broadcast intent: About to broadcast new Alert");
                getApplicationContext().sendBroadcast(newAlertIntent);
            }
            AppConfiguration configuration = AppConfiguration.fromJson(json);
            newConfigurationReceived(configuration);
        }

    };



}
