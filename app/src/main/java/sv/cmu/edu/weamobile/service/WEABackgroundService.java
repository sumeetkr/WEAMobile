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

import sv.cmu.edu.weamobile.AlertDialogActivity;
import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.Data.GeoLocation;
import sv.cmu.edu.weamobile.Utility.AppConfigurationFactory;
import sv.cmu.edu.weamobile.Utility.GPSTracker;
import sv.cmu.edu.weamobile.Utility.InOrOutTargetDecider;
import sv.cmu.edu.weamobile.Utility.Logger;
import sv.cmu.edu.weamobile.Utility.WEAPointInPoly;

public class WEABackgroundService extends Service {
    public static final String FETCH_CONFIGURATION = "sv.cmu.edu.weamobile.service.action.FETCH_CONFIGURATION";
    public static final String FETCH_ALERT = "sv.cmu.edu.weamobile.service.action.FETCH_ALERT";

    private static final String EXTRA_PARAM1 = "sv.cmu.edu.weamobile.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "sv.cmu.edu.weamobile.service.extra.PARAM2";
    private final IBinder mBinder = new LocalBinder();
    private BroadcastReceiver newConfigurationHandler;

    public static void checkServerForConfiguration(Context context, String param1, String param2) {
        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(FETCH_CONFIGURATION);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void checkServerForNewAlerts(Context context, String param){
        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(FETCH_ALERT);
        intent.putExtra(EXTRA_PARAM1, param);
        context.startService(intent);
    }


    public class LocalBinder extends Binder {
        WEABackgroundService getService() {
            return WEABackgroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("WEA", "WEABackgroundService started" );
        if(intent == null){
            intent = new Intent(getApplicationContext(), WEABackgroundService.class);
            intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        }

        if(newConfigurationHandler == null){
            newConfigurationHandler= new NewConfigurationReceiver(new Handler());
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newConfigurationHandler,
                    new IntentFilter("new-config-event"));

        }
        Log.d("WEA", "Service onStart called with "+ intent.getAction());
        onHandleIntent(intent);

        return Service.START_NOT_STICKY;
    }

    protected void onHandleIntent(Intent intent) {

        Log.d("WEA", "called with "+ intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_CONFIGURATION.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                fetchConfiguration(param1, param2);
            }

        }

        AlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void fetchConfiguration(String param1, String param2) {
        Log.d("WEA", "Got request to fetch new configuration");
        //read configuration and setup up new alarm
        //if problem in getting/receiving configuration, set default alarm

        AppConfigurationFactory.getConfigurationAsync(getApplicationContext());

    }

    private void broadcastNewAlert(String message, String polygonEncoded){
//        WEANewAlertIntent newAlertIntent = new WEANewAlertIntent(message, polygonEncoded);
//        Log.d("WEA", "Broadcast intent: About to broadcast new Alert");
//        getApplicationContext().sendBroadcast(newAlertIntent);

        //Ask InOuttargetDecider to decide
        //If in target send intent to show dialog
        //Do not send it as a broadcast, we need to keep service alive till
        //we know the is in target
        if(InOrOutTargetDecider.isInTarget(getApplicationContext(), polygonEncoded)){
            Intent dialogIntent = new Intent(getBaseContext(), AlertDialogActivity.class);
            dialogIntent.putExtra("Message", message);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(dialogIntent);
        }
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
            long currentTime = System.currentTimeMillis()/1000;
            for(Alert alert: alerts){
                //only show if not shown before
                if(currentTime < Long.parseLong(alert.getEndingAt()) && currentTime > Long.parseLong(alert.getScheduledFor())){
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
                        broadcastNewAlert(alert.getText(), "1222222233123113");
                    }else{
                        Logger.log("Not present in polygon: ", String.valueOf(inPoly));
                    }
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
            Logger.log("NewConfigurationReceiver", intent.getStringExtra("message"));
            String json = intent.getStringExtra("message");
            if(json.isEmpty()){
                json = AppConfigurationFactory.getStringProperty(context, "message");
            }else{
                AppConfigurationFactory.setStringProperty(context, "message", json);
            }
            AppConfiguration configuration = AppConfiguration.fromJson(json);
            newConfigurationReceived(configuration);
        }

    };



}
