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

import sv.cmu.edu.weamobile.data.Configuration;
import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.data.MessageState;
import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;
import sv.cmu.edu.weamobile.utility.db.MessageDataSource;
import sv.cmu.edu.weamobile.utility.db.MessageStateDataSource;

public class WEABackgroundService extends Service {
    public static final String FETCH_CONFIGURATION = "sv.cmu.edu.weamobile.service.action.FETCH_CONFIGURATION";
    public static final String SHOW_ALERT = "sv.cmu.edu.weamobile.service.action.SHOW_ALERT";
    public static final String SEND_HEARTBEAT = "sv.cmu.edu.weamobile.service.action.SEND_HEARTBEAT";

    private final IBinder mBinder = new LocalBinder();
    private BroadcastReceiver newConfigurationHandler;
    private NewActivityReceiver activityBroadcastReceiver;
    private UserActivity lastActivity;
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
            }else if(SEND_HEARTBEAT.equals(action)){
                sendHeartbeat();
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

    private void sendHeartbeat(){
        Logger.log("Got request to send heartbeat");

//        if(WEAUtil.checkIfPhoneIsRegisteredIfNotRegister(getApplicationContext())){
            // Get info on motion i.e. speed and direction
            //Get info on UserActivity
            WEAUtil.getUserActivityInfo(getApplicationContext());
            //wait some time to get user activity, which is added to to the heartbeat
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WEAUtil.sendHeartBeat(getApplicationContext(),
                            lastActivity);
                }
            }, WEAUtil.randInt(2000, 3000)); //it takes a couple of seconds to get activities
//        }

    }
    private void fetchConfiguration() {
        Logger.log("Got request to fetch new configuration");
        //read configuration and setup up new alarm
        //if problem in getting/receiving configuration, set default alarm

        if(WEAUtil.checkIfPhoneIsRegisteredIfNotRegister(getApplicationContext())){

            //don't let all phones to ask at the same moment
            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        WEAHttpClient.fetchAlerts(getApplicationContext());
                                    }

              }, WEAUtil.randInt(5, 1000));


            // Get info on motion i.e. speed and direction
            //Get info on UserActivity
//            WEAUtil.getUserActivityInfo(getApplicationContext());
//            //wait some time to get user activity, which is added to to the heartbeat
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    WEAUtil.sendHeartBeat(getApplicationContext(),
//                            lastActivity);
//                }
//            }, WEAUtil.randInt(1000, 2000));
        }
    }

    private void addOrUpdatedMessageStatesToDatabase(Configuration configuration) {
        if(configuration!= null){
            List<Message> messages = configuration.getMessages();
            if(messages.size() >0){
                for(Message message: messages) {
                    //Only create messagestate for future/current alerts, no use of old alerts
                    if(message.isActive() || message.isOfFuture()){
//                        WEASharedPreferences.addAlertStateToPreferences(getApplicationContext(), message);

                        MessageState state = new MessageState(message.getId(), message.getScheduledFor());
                        MessageStateDataSource messageStateDataSource = new MessageStateDataSource(this);
                        messageStateDataSource.insertDataIfNotPresent(state);

                    }
                }
            }
        }
    }

    /*
        This function adds alerts to the database and if they already exist, updates them.
        Author: Harsh Alkutkar, Feb 25, 2015
     */
    private void addOrUpdatedMessagesToDatabase(Configuration configuration) {

        MessageDataSource messageDataSource = new MessageDataSource(this);
        //insert only new alerts, leave stale alerts
        List<Message> messages = getAlertRelevantBetweenNowAndFuture(configuration);
        messageDataSource.insertDataItemsIfNotPresent(messages);

        List<Message> messagesRecent = getRecentMessages(configuration);
        messageDataSource.insertDataItemsIfNotPresent(messagesRecent);

    }

    private void setupAlarmToShowFutureMessagesAtRightTime(Configuration configuration, long currentTime){

        //two things to be done, shown now or schedule for later if in half an hour
        List<Message> messages = getAlertRelevantBetweenNowAndFuture(configuration);
        for(Message message : messages){
            String messageToShow = "Alert expected after: "+ (message.getScheduledEpochInSeconds() - currentTime) + " secs";
            Logger.log(messageToShow);
            WEAUtil.showMessageIfInDebugMode(getApplicationContext(), messageToShow);
            WEAAlarmManager.setupAlarmForAlertAtScheduledTime(getApplicationContext(), message.getId(), message.getScheduleEpochInMillis());

            AlertHelper.sendAlertReceivedInfoToServer(getApplicationContext(), message);
        }
    }

    private void showRecentMessageToUser(Configuration configuration, long currentTime){
        List<Message> messages = getRecentMessages(configuration);
        for(Message message : messages){
            String messageToShow = "Alert expected after: "+ (message.getScheduledEpochInSeconds() - currentTime) + " secs";
            Logger.log(messageToShow);
            WEAUtil.showMessageIfInDebugMode(getApplicationContext(), messageToShow);
            WEAAlarmManager.setupAlarmForAlertAtScheduledTime(
                    getApplicationContext(),
                    message.getId(),
                    System.currentTimeMillis() + WEAUtil.randInt(5000, 20000)); //in 3 to 6 seconds

            AlertHelper.sendAlertReceivedInfoToServer(getApplicationContext(), message);
        }
    }

    private List<Message> getAlertRelevantBetweenNowAndFuture(Configuration config){
        List<Message> relevantAlerts= new ArrayList<Message>();

        if(config != null){
            List<Message> messages = config.getMessages();
            if(messages.size() >0){
                for(Message alert: messages){
                    try{
                        if(alert.isFutureMessage()){
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

    private List<Message> getRecentMessages(Configuration config){
        List<Message> relevantAlerts= new ArrayList<Message>();

        if(config != null){
            List<Message> messages = config.getMessages();
            if(messages.size() >0){
                for(Message alert: messages){
                    try{
                        if(alert.isRecent()){
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

            Logger.log("NewConfigurationReceiver");
            String json = intent.getStringExtra("message");
            Logger.log("New configuration received" + json);
            WEANewConfigurationIntent newConfigurationIntent;

            if(!json.isEmpty() && json.length()>3){
                WEASharedPreferences.saveApplicationConfiguration(context, json);
                newConfigurationIntent = new WEANewConfigurationIntent(
                        "Received new configuration. ",
                        json,
                        false);

                Configuration configuration = Configuration.fromJson(json);

                addOrUpdatedMessagesToDatabase(configuration);

                addOrUpdatedMessageStatesToDatabase(configuration); //Save the individual alerts

                long currentTime = System.currentTimeMillis()/1000;
                setupAlarmToShowFutureMessagesAtRightTime(configuration, currentTime);
                showRecentMessageToUser(configuration, currentTime);

                //update if new alerts
                Logger.log("Broadcast intent: About to broadcast new configuration");
                getApplicationContext().sendBroadcast(newConfigurationIntent);

            }else{
                Logger.log("empty configuration received");
            }
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
                        lastActivity = (UserActivity) intent.getSerializableExtra(Constants.ACTIVITY);
                        Logger.log("BackgroundService received new activity notification " + intent.getStringExtra(Constants.ACTIVITY_TYPE));
                    }
                });
            }
        }
    };
}
