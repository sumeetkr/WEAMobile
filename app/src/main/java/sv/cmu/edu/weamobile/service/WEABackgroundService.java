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

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.AppConfiguration;
import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;
import sv.cmu.edu.weamobile.utility.db.AlertDataSource;

public class WEABackgroundService extends Service {
    public static final String FETCH_CONFIGURATION = "sv.cmu.edu.weamobile.service.action.FETCH_CONFIGURATION";
    public static final String SHOW_ALERT = "sv.cmu.edu.weamobile.service.action.SHOW_ALERT";

    private final IBinder mBinder = new LocalBinder();
    private BroadcastReceiver newConfigurationHandler;
    private NewActivityReceiver activityBroadcastReceiver;
    private AlertDataSource alertDataSource = new AlertDataSource(this);
    private int lastKnownActivityType = -1;
    private int lastKnownActivityConfidence = 0;
    private Handler handler;


    public class LocalBinder extends Binder {
        WEABackgroundService getService() {
            return WEABackgroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        handler = new Handler();
        Log.d("WEA", "WEABackgroundService started at " + WEAUtil.getTimeStringFromEpoch(System.currentTimeMillis() / 1000) );
        Log.d("WEA", "Service onStart called with "+ intent);
        if(intent == null){
            Log.d("WEA", "Intent was null so setting it to FETCH_CONFIGURATION");
            intent = new Intent(getApplicationContext(), WEABackgroundService.class);
            intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        }

        registerNewConfigurationReceiver();
        registerNewActivityReceiver();

        onHandleIntent(intent);


        return Service.START_NOT_STICKY;
    }

    private void registerNewConfigurationReceiver() {
        if(newConfigurationHandler == null){
            newConfigurationHandler= new NewConfigurationReceiver(handler);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newConfigurationHandler,
                    new IntentFilter("new-config-event"));

        }
    }

    private void registerNewActivityReceiver() {
        if (activityBroadcastReceiver == null){
            activityBroadcastReceiver = new NewActivityReceiver(handler);

            Logger.log("WEA", "New configuration receiver created in main activity");
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.NEW_ACTIVITY");
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getApplicationContext().registerReceiver(activityBroadcastReceiver, filter);
        }
    }

    protected void onHandleIntent(Intent intent) {
        Log.d("WEA", "called with "+ intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_CONFIGURATION.equals(action)) {
                fetchConfiguration();
            }else if(SHOW_ALERT.equals(action)){
                int alertId= intent.getIntExtra(("alertId"),-1);
                showAlertAfterCheckingOtherConditions(alertId);
            }
        }
        AlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.log("WEABackgroundService onDestroy called");

        if(activityBroadcastReceiver!= null){
            getApplication().unregisterReceiver(activityBroadcastReceiver);
            activityBroadcastReceiver = null;
        }

        if(newConfigurationHandler != null){
            getApplication().unregisterReceiver(newConfigurationHandler);
            newConfigurationHandler = null;
        }
    }

    private void showAlertAfterCheckingOtherConditions(int alertId) {

        //Get info on UserActivity

//        WEAUtil.getUserActivityInfo(getApplicationContext());

        // Get info on location history

        // Get info on motion i.e. speed and direction

        //Combine the above three info to find if alert is to be shown or not

        AlertHelper.showAlertIfInTargetOrIsNotGeotargeted(getApplicationContext(), alertId);
    }

    private void fetchConfiguration() {
        Log.d("WEA", "Got request to fetch new configuration");
        //read configuration and setup up new alarm
        //if problem in getting/receiving configuration, set default alarm

        // Get info on motion i.e. speed and direction
        //Get info on UserActivity
        WEAUtil.getUserActivityInfo(getApplicationContext());
        //wait some time to get user activity, which is added to to the heartbeat
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WEAUtil.sendHeartBeatAndGetConfigurationAsync(getApplicationContext(),
                        lastKnownActivityType,
                        lastKnownActivityConfidence);
            }
        }, 1000);
    }

    private void addOrUpdatedAlertsStateToSharedPreferences(AppConfiguration configuration) {
        if(configuration!= null){
            Alert [] alerts = configuration.getAlertsFromJSON();
            if(alerts.length >0){
                for(Alert alert: alerts) {
                    if(alert.isActive() || alert.isOfFuture()){
                        WEASharedPreferences.addAlertStateToPreferences(getApplicationContext(), alert);
                    }
                }
            }
        }
    }

    /*
        This function adds alerts to the database and if they already exist, updates them.
        Author: Harsh Alkutkar, Feb 25, 2015
     */
    private void addOrUpdatedAlertsStateToDatabase(AppConfiguration configuration) {
        if(configuration!= null){
            Alert [] alerts = configuration.getAlertsFromJSON();
            if(alerts.length >0){
                for(Alert alert: alerts) {
                    if(alert.isActive() || alert.isOfFuture()){
                        //Level of indirection (ADS->MYSQLITEHELPER->addAlertStateToDatabase())
                        alertDataSource.addAlertStateToDatabase(alert);
                    }
                }
            }
        }
    }



    private void setupAlarmToShowAlertAtRightTime(AppConfiguration configuration){

        //two things to be done, shown now or schedule for later if in half an hour
        List<Alert> alerts = getAlertRelevantBetweenNowAndNextScheduledCheck(configuration);
        for(Alert alert : alerts){
            long currentTime = System.currentTimeMillis()/1000;
            String message = "Alert expected after: "+ (alert.getScheduledEpochInSeconds() - currentTime) + " secs";
            Logger.log(message);
            WEAUtil.showMessageIfInDebugMode(getApplicationContext(), message);
            WEAAlarmManager.setupAlarmForAlertAtScheduledTime(getApplicationContext(), alert.getId(), alert.getScheduleEpochInMillis());

            sendAlertScheduledInfoToServer(alert);
        }
    }

    private void sendAlertScheduledInfoToServer(Alert alert) {
        try{
            AlertState alertState = AlertHelper.getAlertState(getApplicationContext(), alert);
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

    private List<Alert> getAlertRelevantBetweenNowAndNextScheduledCheck(AppConfiguration config){
        List<Alert> relevantAlerts= new ArrayList<Alert>();

        if(config != null){
            Alert [] alerts = config.getAlertsFromJSON();
            if(alerts.length >0){
                for(Alert alert: alerts){
                    try{
                        if(alert.isInRangeToSchedule()){
                            relevantAlerts.add(alert);
                        }
                    }catch(Exception ex){
                        Logger.log(ex.getMessage());
                    }
                }
            }
        }
        return relevantAlerts;
    }

    private class NewConfigurationReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewConfigurationReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
//            ToDo: Move "open, add data and close" to a single function, with exception handling
            // [--- database start -- * has to be here *]
            //create / open the db (important - has to be before anything)
            Logger.log("Opening a connection to the database");
            alertDataSource.open();

            Logger.log("NewConfigurationReceiver");
            String json = intent.getStringExtra("message");
            WEANewConfigurationIntent newConfigurationIntent;

            if(json.isEmpty()){
                Logger.log("Received empty json");
                json = WEASharedPreferences.readApplicationConfiguration(context);
                newConfigurationIntent = new WEANewConfigurationIntent("", json, true);

            }else{
                WEASharedPreferences.saveApplicationConfiguration(context, json);
                newConfigurationIntent = new WEANewConfigurationIntent("Received new configuration. ", json, false);

            }

            AppConfiguration configuration = AppConfiguration.fromJson(json);

            addOrUpdatedAlertsStateToSharedPreferences(configuration); //Save the individual alerts

            //---- Database Insertion Trial [db]
            addOrUpdatedAlertsStateToDatabase(configuration);

            setupAlarmToShowAlertAtRightTime(configuration);

            //update if new alerts
            Logger.log("Broadcast intent: About to broadcast new configuration");
            getApplicationContext().sendBroadcast(newConfigurationIntent);

            alertDataSource.close();
        }

    }

    private class NewActivityReceiver extends BroadcastReceiver{

        private final Handler handler;

        public NewActivityReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UserActivity activity = (UserActivity) intent.getSerializableExtra(Constants.ACTIVITY);

                        lastKnownActivityType = activity.getPrimaryActivityType();
                        lastKnownActivityConfidence = activity.getActivityConfidence();
                        Logger.log("BackgroundService received new activity notification " + intent.getStringExtra(Constants.ACTIVITY_TYPE));
                    }
                });
            }
        }
    };
}
