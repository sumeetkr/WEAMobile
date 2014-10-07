package sv.cmu.edu.weamobile.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import sv.cmu.edu.weamobile.AlertDialogActivity;
import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.Utility.AppConfigurationFactory;
import sv.cmu.edu.weamobile.Utility.InOrOutTargetDecider;

public class WEABackgroundService extends Service {
    public static final String FETCH_CONFIGURATION = "sv.cmu.edu.weamobile.service.action.FETCH_CONFIGURATION";
    public static final String FETCH_ALERT = "sv.cmu.edu.weamobile.service.action.FETCH_ALERT";

    private static final String EXTRA_PARAM1 = "sv.cmu.edu.weamobile.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "sv.cmu.edu.weamobile.service.extra.PARAM2";
    private final IBinder mBinder = new LocalBinder();

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

//    public WEABackgroundService() {
//        super("WEABackgroundService");
//    }

    public class LocalBinder extends Binder {
        WEABackgroundService getService() {
            return WEABackgroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent == null){
            intent = new Intent(getApplicationContext(), WEABackgroundService.class);
            intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        }
        Log.d("WEA", "Service onStart called with "+ intent.getAction());
        onHandleIntent(intent);

        return Service.START_NOT_STICKY;
    }

//    @Override
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

        AppConfiguration configuration = AppConfigurationFactory.getConfiguration();

        long currentTime = System.currentTimeMillis();
        if(currentTime < configuration.getEndingAt() && currentTime > configuration.getScheduledFor()){
            //if time to show new alert
            broadcastNewAlert("Free food alert", "1222222233123113");
        }
    }

    private void broadcastNewAlert(String message, String polygonEncoded){
        WEANewAlertIntent newAlertIntent = new WEANewAlertIntent(message, polygonEncoded);
        Log.d("WEA", "Broadcast intent: About to broadcast new Alert");
        getApplicationContext().sendBroadcast(newAlertIntent);

        //Ask InOuttargetDecider to decide
        //If in target send intent to show dialog
        //Do not send it as a broadcast, we need to keep service alive till
        //we know the is in target
        if(InOrOutTargetDecider.isInTarget(polygonEncoded)){
            Intent dialogIntent = new Intent(getBaseContext(), AlertDialogActivity.class);
            dialogIntent.putExtra("Message", message);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(dialogIntent);
        }
    }
}
