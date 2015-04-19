package sv.cmu.edu.weamobile.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.ActivityRecognition.UserActivityRecognizer;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;

public class DebugSettings extends ActionBarActivity {

    private CheckBox chkViewDebugMessages;
    private CheckBox chkStartActivityRecognition;
    private CheckBox chkShowLocationHistory;
    private CheckBox chkShowActivityHistory;
    private CheckBox chkMotion;
    private CheckBox chkShowAWSMessagesAsNotifications;
    private CheckBox chkShowAllAlerts;
    private CheckBox chkShowFetchAlerts;
    private TextView txtMessages;
    private TextView txtLastHeartBeatAt;
    private TextView txtLastAlertAt;
    private TextView txtVersionNo;
    private TextView txtPhoneId;
    private UserActivityRecognizer activityRecognizer;
    private NewActivityReceiver activityBroadcastReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_settings);

        WEAUtil.restoreDebugSettingToOriginalState(this);

        final Context ctxt = this;

        chkViewDebugMessages = (CheckBox)findViewById(R.id.checkBoxDebugMessages);
        chkStartActivityRecognition = (CheckBox) findViewById(R.id.checkBoxActivityRecognition);
        chkShowLocationHistory = (CheckBox) findViewById(R.id.chkLocationHistory);
        txtMessages = (TextView) findViewById(R.id.txtDebugMessages);
        chkMotion = (CheckBox) findViewById(R.id.chkMotion);
        chkShowActivityHistory = (CheckBox) findViewById(R.id.chkActivityHistory);
        chkShowAWSMessagesAsNotifications = (CheckBox) findViewById(R.id.chkShowNotifications);
        chkShowAllAlerts = (CheckBox) findViewById(R.id.chkShowAllAlerts);
        chkShowFetchAlerts = (CheckBox) findViewById(R.id.chkShowFetchAlertsPanel);

        txtLastAlertAt = (TextView) findViewById(R.id.txtFetchAlerts);
        txtLastHeartBeatAt = (TextView) findViewById(R.id.txtSendHeartbeat);
        txtVersionNo = (TextView) findViewById(R.id.txtVersionNo);
        txtPhoneId = (TextView) findViewById(R.id.txtPhoneId);

        chkViewDebugMessages.setChecked(WEASharedPreferences.isInDebugMode(getApplicationContext()));
        chkViewDebugMessages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setDebugMode(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setDebugMode(getApplicationContext(), false);
                }
           }
        }

        );

        boolean isActivityEnabled = WEASharedPreferences.isActivityRecognitionEnabled(getApplicationContext());
        chkStartActivityRecognition.setChecked(isActivityEnabled);
        if(isActivityEnabled) registerNewActivityReceiver();

        chkStartActivityRecognition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    WEASharedPreferences.setActivityRecognitionEnabled(getApplicationContext(), true);
                    txtMessages.setText("Asking GOOGLE for a new Activity.");

                    registerNewActivityReceiver();
                    WEAUtil.getUserActivityInfo(getApplicationContext());

                }else{
                    WEASharedPreferences.setActivityRecognitionEnabled(getApplicationContext(), false);
                    if(activityRecognizer != null){
                        activityRecognizer.stopActivityRecognitionScan();
                    }
                    unregisterActivityBroadcastReceiver();
                    txtMessages.setText("Activity check stopped.");
                }
            }
        });

        chkShowLocationHistory.setChecked(WEASharedPreferences.isLocationHistoryEnabled(getApplicationContext()));
        chkShowLocationHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setIsLocationHistoryEnabled(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setIsLocationHistoryEnabled(getApplicationContext(), false);
                }
            }
        });

        chkShowActivityHistory.setChecked(WEASharedPreferences.isActivityHistoryEnabled(getApplicationContext()));
        chkShowActivityHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setActivityHistoryEnabled(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setActivityHistoryEnabled(getApplicationContext(), false);
                }
            }
        });


        chkMotion.setChecked(WEASharedPreferences.isMotionPredictionEnabled(getApplicationContext()));
        chkMotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setMotionEnabled(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setMotionEnabled(getApplicationContext(), false);
                }
            }
        });

        chkShowAWSMessagesAsNotifications.setChecked(WEASharedPreferences.isShowNotificationsEnabled(getApplicationContext()));
        chkShowAWSMessagesAsNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setShowNotificationsEnabled(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setShowNotificationsEnabled(getApplicationContext(), false);
                }
            }
        });

        chkShowAllAlerts.setChecked(WEASharedPreferences.isShowAllAlertsEnabled(getApplicationContext()));
        chkShowAllAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setShowAllAlertsEnabled(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setShowAllAlertsEnabled(getApplicationContext(), false);
                }
            }
        });

        chkShowFetchAlerts.setChecked(WEASharedPreferences.isFetchAlertsEnabled(getApplicationContext()));
        chkShowFetchAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setFetchAlertsEnabled(getApplicationContext(), true);
                }else{
                    WEASharedPreferences.setFetchAlertsEnabled(getApplicationContext(), false);
                }
            }
        });

        updateTimeInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimeInfo();
    }

    private void updateTimeInfo() {
        try{
            String timeLastAlertReceived = WEASharedPreferences.getStringProperty(this, Constants.LAST_TIME_WHEN_ALERT_RECEIVED);
            if(timeLastAlertReceived!= null && !timeLastAlertReceived.isEmpty()){
                Long timeWhenLastAlertReceived = Long.valueOf(timeLastAlertReceived);
                txtLastAlertAt.setText("Last Alert at : "+ WEAUtil.getTimeStringFromEpoch(timeWhenLastAlertReceived / 1000));
            }

            String timeLastHeartBeat = WEASharedPreferences.getStringProperty(this, Constants.LAST_TIME_WHEN_HEARTBEAT_SENT);
            if(timeLastHeartBeat != null && !timeLastHeartBeat.isEmpty()){
                Long timeWhenLastHeartbeatSent = Long.valueOf(timeLastHeartBeat);
                txtLastHeartBeatAt.setText("Last Heartbeat at : " + WEAUtil.getTimeStringFromEpoch(timeWhenLastHeartbeatSent/1000));
            }

            txtVersionNo.setText("Version no: " + WEAUtil.getPackageVersion(getApplicationContext()));

            String phoneId = WEASharedPreferences.getStringProperty(getApplicationContext(), Constants.PHONE_ID);
            if(phoneId != null && !phoneId.isEmpty()){
                txtPhoneId.setText("PhoneId: "+ phoneId);
            }

        }catch (Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    private void registerNewActivityReceiver() {
        if(activityBroadcastReceiver == null){
            activityBroadcastReceiver = new NewActivityReceiver(new Handler());

            Logger.log("WEA", "New activityBroadcastReceiver created in debug activity");
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.NEW_ACTIVITY");
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getApplicationContext().registerReceiver(activityBroadcastReceiver, filter);
        }
    }

    private void unregisterActivityBroadcastReceiver() {
        Logger.log("unregisterActivityBroadcastReceiver called");
        if(activityBroadcastReceiver!= null){
            getApplication().unregisterReceiver(activityBroadcastReceiver);
            activityBroadcastReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onResume(){
//        super.onResume();
//
//    }

    @Override
    protected void onDestroy(){
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Called onDestroy of debug settings view");
        unregisterActivityBroadcastReceiver();
        super.onDestroy();
    }

    private class NewActivityReceiver extends BroadcastReceiver{

        private final Handler handler;

        public NewActivityReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, final Intent intent) {
            UserActivity activity = (UserActivity) intent.getSerializableExtra(Constants.ACTIVITY);

            final String lastKnownActivity = UserActivity.getFriendlyName(activity.getPrimaryActivityType());
            final int lastKnownActivityConfidence = activity.getActivityConfidence();

            final String lastKnownSecondaryActivity = UserActivity.getFriendlyName(activity.getSecondaryActivityTYpe());
            final int lastKnownSecondaryActivityConfidence = activity.getSecondaryActivityConfidence();

            Logger.log("DebugSettings received new activity notification " + intent.getStringExtra(Constants.ACTIVITY_TYPE));
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtMessages.setText(
                                "Primary Activity Type : " + lastKnownActivity +
                        "\nPrimary Activity Confidence : " + lastKnownActivityConfidence + "%\n"+
                        "Secondary Activity Type : " + lastKnownSecondaryActivity +
                        "\nSecondary Activity Confidence : " + lastKnownSecondaryActivityConfidence + "%");
                    }
                });
            }
        }
    };
}
