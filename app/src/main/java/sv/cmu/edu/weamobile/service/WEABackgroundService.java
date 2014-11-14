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
import android.widget.Toast;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.AppConfiguration;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;

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
                AlertHelper.showAlertIfInTarget(getApplicationContext(), alertId);
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

        WEAHttpClient.getConfigurationAsync(getApplicationContext());

    }

    private void addOrUpdatedAlertsStateToSharedPreferences(AppConfiguration configuration) {
        Alert [] alerts = configuration.getAlertsFromJSON();
        if(alerts.length >0){
            for(Alert alert: alerts) {
                WEASharedPreferences.addAlertStateToPreferences(getApplicationContext(), alert);
            }
        }
    }

    private void setupAlarmToShowAlertAtRightTime(AppConfiguration configuration){

        //two things to be done, shown now or schedule for later if in half an hour
        Alert alert = getAlertRelevantBetweenNowAndNextScheduledCheck(configuration);
        if(alert != null){
            long currentTime = System.currentTimeMillis()/1000;
            String message = "Alarm expected after: "+ (alert.getScheduledEpochInSeconds() - currentTime) + " secs";
            Logger.log(message);
            if(message!=null && !message.isEmpty()){
                Toast.makeText(getApplicationContext(), "Alert Time!!: " + message, Toast.LENGTH_SHORT).show();
            }
            WEAAlarmManager.setupAlarmForAlertAtScheduledTime(getApplicationContext(), alert.getId(), alert.getScheduleEpochInMillis());

            try{
                AlertState alertState = AlertHelper.getAlertStateFromId(getApplicationContext(), String.valueOf(alert.getId()));
                alertState.setState(AlertState.State.scheduled);
                WEASharedPreferences.saveAlertState(getApplicationContext(), alertState);
                WEAHttpClient.sendAlertState(getApplicationContext(),
                        alertState.getJson(),
                        String.valueOf(alertState.getId()));

            }
            catch (Exception ex){
                Logger.log(ex.getMessage());
            }
        }
    }

    private Alert getAlertRelevantBetweenNowAndNextScheduledCheck(AppConfiguration config){
        Alert relevantAlert= null;

        Alert [] alerts = config.getAlertsFromJSON();
        if(alerts.length >0){
            for(Alert alert: alerts){
                try{
                    if(alert.isInRangeToSchedule()){
                        relevantAlert = alert;
                    }
                }catch(Exception ex){
                    Logger.log(ex.getMessage());
                }
            }
        }
        return relevantAlert;
    }

    private class NewConfigurationReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewConfigurationReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            Logger.log("NewConfigurationReceiver");
            String json = intent.getStringExtra("message");
            WEANewConfigurationIntent newConfigurationIntent;

            if(json.isEmpty()){
                Logger.log("Received empty json");
                json = WEASharedPreferences.readApplicationConfiguration(context);
                newConfigurationIntent = new WEANewConfigurationIntent("Could not connect to server, using old configuration !!", json, true);

            }else{
                WEASharedPreferences.saveApplicationConfiguration(context, json);
                newConfigurationIntent = new WEANewConfigurationIntent("", json, false);
            }

            AppConfiguration configuration = AppConfiguration.fromJson(json);
            addOrUpdatedAlertsStateToSharedPreferences(configuration);
            setupAlarmToShowAlertAtRightTime(configuration);

            //update if new alerts
            Log.d("WEA", "Broadcast intent: About to broadcast new configuration");
            getApplicationContext().sendBroadcast(newConfigurationIntent);
        }

    }
}
