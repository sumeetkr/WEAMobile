package sv.cmu.edu.weamobile.views;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.AppConfiguration;
import sv.cmu.edu.weamobile.service.WEAAlarmManager;
import sv.cmu.edu.weamobile.service.WEABackgroundService;
import sv.cmu.edu.weamobile.service.WEANewConfigurationIntent;
import sv.cmu.edu.weamobile.utility.AWSHelperUtility;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.MessageReceivingService;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEATextToSpeech;
import sv.cmu.edu.weamobile.utility.WEAUtil;
import sv.cmu.edu.weamobile.utility.WEAVibrator;
import sv.cmu.edu.weamobile.utility.db.AlertDataSource;
import sv.cmu.edu.weamobile.utility.db.MySQLiteHelper;

public class MainActivity extends FragmentActivity
        implements AlertListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Handler handler;
    private NewConfigurationReceivedBroadcastReceiver newAlertReciver;
    private Switch mySwitch;
    private AppConfiguration configuration;
    private AlertListFragment listFragment;
    private boolean programTryingToChangeSwitch = false;
    private AlertDialog dialog;
    private final int defaultId = -2;
    private int idOfShownAlert = defaultId;


    private AlertDataSource alertDataSource;


    //Amazon AWS
    // Since this activity is SingleTop, there can only ever be one instance. This variable corresponds to this instance.
    public static Boolean inBackground = true;
    private SharedPreferences savedValues;
    private String numOfMissedMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);
        //opening datasource
        alertDataSource = new AlertDataSource(this);
        alertDataSource.open();


        Logger.log("Main on create called");
        listFragment = ((AlertListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alert_list));

        if (findViewById(R.id.alert_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

            listFragment.setActivateOnItemClick(true);
        }




        setSwitchEvents();

        handler = new Handler();

        registerNewConfigurationReceiver();
        if(getIntent().getAction()!= null && getIntent().getAction() == "android.intent.action.MAIN"){

            Log.d("WEA", "scheduling alarm");
            WEAAlarmManager.setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
                    this.getApplicationContext(),
                    Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES * 60 * 1000);
        }

        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Reached onCreate of main view");

        //register the amazon aws client
        if (isMyServiceRunning(MessageReceivingService.class) == false) {
            startService(new Intent(this, MessageReceivingService.class));
        }


    }

    //To  check if your service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void onStop(){
        super.onStop();
        inBackground = true;
    }


    // If messages have been missed, check the backlog. Otherwise check the current intent for a new message.
    private String getMessage(int numOfMissedMessages) {
        String message = "";
        String linesOfMessageCount = getString(R.string.lines_of_message_count);
        if(numOfMissedMessages > 0){
            String plural = numOfMissedMessages > 1 ? "s" : "";
            Log.i("onResume","missed " + numOfMissedMessages + " message" + plural);
            //Log.i("TEXTVIEW","You missed " + numOfMissedMessages +" message" + plural + ". Your most recent was:\n");
            for(int i = 0; i < savedValues.getInt(linesOfMessageCount, 0); i++){
                String line = savedValues.getString("MessageLine"+i, "");
                message+= (line + "\n");
            }
            NotificationManager mNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotification.cancel(R.string.notification_number);
            SharedPreferences.Editor editor=savedValues.edit();
            editor.putInt(this.numOfMissedMessages, 0);
            editor.putInt(linesOfMessageCount, 0);
            editor.commit();
        }
        else{
            Log.i("onResume","no missed messages");
            Intent intent = getIntent();
            if(intent!=null){
                Bundle extras = intent.getExtras();
                if(extras!=null){
                    for(String key: extras.keySet()){
                        message+= key + "=" + extras.getString(key) + "\n";
                    }
                }
            }
        }
        message+="\n";
        return message;
    }


    private void setSwitchEvents() {
        mySwitch = (Switch) findViewById(R.id.switch2);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!programTryingToChangeSwitch) {
                    if (isChecked) {
                        mySwitch.setText("Syncing..");
                        //ToDO: only for debugging
//                        if (Constants.IS_IN_DEBUG_MODE && configuration != null) {
//                            AlertHelper.clearAlertStates(getApplicationContext(), configuration.getAlerts(getApplicationContext()));
//                        }
                        fetchConfig();
                    } else {
                        mySwitch.setText("Alerts disabled");
                    }
                }else{
                    programTryingToChangeSwitch = false;
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Reached onNewIntent, updating intent");
    }

    @Override
    protected void onResume(){
        super.onResume();
        getActionBar().setIcon(R.drawable.ic_emergency);

        registerNewConfigurationReceiver();

        String json = WEASharedPreferences.readApplicationConfiguration(getApplicationContext());
        configuration = AppConfiguration.fromJson(json);

        if(getIntent().hasExtra(Constants.ALERT_ID)){
            refreshListAndSelectItem();
        }else{
            refreshListAndShowUnSeenAlert();
        }

//        if(!getIntent().hasExtra(Constants.ALERT_ID)){
//            Alert[] alerts = configuration.getAlertsWhichAreNotGeoTargetedOrGeotargetedAndUserWasInTarget(getApplicationContext());
//            AlertState [] alertStates = AlertHelper.getAlertStates(getApplicationContext(), alerts);
//            List<Alert> alertNotShown = listFragment.updateListAndReturnAnyActiveAlertNotShown(alerts, alertStates);
//            if(alertNotShown != null && alertNotShown.size()>0){
//                AlertHelper.showAlertIfInTargetOrIsNotGeotargeted(getApplicationContext(), alertNotShown.get(0).getId());
//            }
//        }
        updateLastCheckTimeStatus();

        //amazon
        inBackground = false;
        savedValues = MessageReceivingService.savedValues;
        int numOfMissedMessages = 0;
        if(savedValues != null){
            numOfMissedMessages = savedValues.getInt(this.numOfMissedMessages, 0);
        }
        String newMessage = getMessage(numOfMissedMessages);
        if(newMessage!=""){
            Log.i("displaying message", newMessage);
            Log.i("TEXTVIEW",newMessage);
            if (newMessage.contains("default")) {
                AWSHelperUtility.showNotification(this, "New Alert", newMessage);
            }
        }
    }

    private void refreshListAndShowUnSeenAlert() {
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Reached main view OnResume, but no alert to show, will refresh the list");
        if(configuration!= null){
            List<Alert> alerts = configuration.getAlertsWhichAreNotGeoTargetedOrGeotargetedAndUserWasInTarget(getApplicationContext());
            List<AlertState> alertStates = AlertHelper.getAlertStates(getApplicationContext(), alerts);
            List<Alert> alertNotShown = listFragment.updateListAndReturnAnyActiveAlertNotShown(alerts, alertStates);
            if(alertNotShown != null && alertNotShown.size()>0){
                AlertHelper.showAlertIfInTargetOrIsNotGeotargeted(getApplicationContext(), alertNotShown.get(0).getId());
            }
        }
    }

    private void refreshListAndSelectItem() {
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Reached main view OnResume with an alert to show, updating the list of alerts first");
        if(configuration!= null){
            String alertId = getIntent().getStringExtra(Constants.ALERT_ID);
            List<Alert> alerts = configuration.getAlertsWhichAreNotGeoTargetedOrGeotargetedAndUserWasInTarget(getApplicationContext());
            List<AlertState> alertStates = AlertHelper.getAlertStates(getApplicationContext(), alerts);
            listFragment.updateListAndReturnAnyActiveAlertNotShown(alerts, alertStates);

            onItemSelected(alertId);
        }
    }

    private void registerNewConfigurationReceiver() {
        if(newAlertReciver ==null) {
            newAlertReciver = new NewConfigurationReceivedBroadcastReceiver(handler);

            Log.d("WEA", "New configuration receiver created in main activity");
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.NEW_ALERT");
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getApplicationContext().registerReceiver(newAlertReciver, filter);
        }
    }

    private void updateLastCheckTimeStatus() {
        String time = WEASharedPreferences.getStringProperty(getApplicationContext(), "lastTimeChecked");
        if(time != null && !time.isEmpty() && ((System.currentTimeMillis()- Long.parseLong(time))< Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES*60*1000)){
            setUpToDate();
        }else{
            if(mySwitch.isChecked()) mySwitch.setChecked(false);
            //fetchConfig();
        }
    }

    private void setUpToDate() {
        if(!mySwitch.isChecked()) mySwitch.setChecked(true);
        long time = Long.parseLong(WEASharedPreferences.getStringProperty(getApplicationContext(),
                "lastTimeChecked"));
        mySwitch.setText("Synced at: " +
                WEAUtil.getTimeStringFromEpoch(time / 1000));
    }

    private void setUpCouldNotConnectToNetwork() {
        if(mySwitch.isChecked()) mySwitch.setChecked(false);
        mySwitch.setText("Synced failed !!");
    }

    private void fetchConfig() {
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Main view, fetch configuration called");
        registerNewConfigurationReceiver();

        Intent intent = new Intent(getApplicationContext(), WEABackgroundService.class);
        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        startService(intent);
    }

    @Override
    protected void onPause(){

        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Reached onPause of main view");
        // Register mMessageReceiver to receive messages.
        if(newAlertReciver!= null) {
            getApplication().unregisterReceiver(newAlertReciver);
            newAlertReciver = null;
        }
        super.onPause();

    }

    @Override
    protected void onDestroy(){
        inBackground = true;
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Called onDestroy of main view");
        if(newAlertReciver!= null){
            getApplication().unregisterReceiver(newAlertReciver);
            newAlertReciver = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), DebugSettings.class);
                startActivity(intent);
                return true;
            case R.id.action_login:
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Callback method from {@link AlertListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Constants.ARG_ITEM_ID, id);
            AlertDetailFragment fragment = new AlertDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.alert_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            //if without map, show dialog
            Alert alert = AlertHelper.getAlertFromId(
                    getApplicationContext(),
                    id);

            if(alert.isMapToBeShown()){
                Intent detailIntent = new Intent(this, AlertDetailActivity.class);
                detailIntent.putExtra(Constants.ARG_ITEM_ID, id);
                detailIntent.putExtra(Constants.CONFIG_JSON, configuration.getJson());

                WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                        "Asking to show alert with a map");
                startActivity(detailIntent);
            }else{
                //if(!isDialogShown){
                showDialog(alert);
                //}
            }
        }
    }

    public void onBackPressed() {

        if(dialog != null){
            dialog.cancel();
            dialog = null;
        }else{
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    private void showDialog(Alert alert) {

        if(dialog != null && alert.getId() == idOfShownAlert){
            //do not do anything, the alert has already been shown
            WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                    "The alert is already shown, so not showing again.");
        }else{
            if(dialog != null) {
                dialog.cancel();
                dialog.dismiss();
                WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                        "Existing alert dialog was there, dismissing it");
            }
            try {
                WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                        "Creating alert as a dialog");
                dialog = createDialog(getApplicationContext(), alert);
                dialog.show();
            }catch (Exception ex){
                Logger.log(ex.getMessage());
            }
        }
    }

    private AlertDialog createDialog(final Context context, final Alert alert1){

        final Alert alert = alert1;
        final Activity activity = this;
        final AlertState alertState = WEASharedPreferences.getAlertState(context, alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(this,  AlertDialog.THEME_TRADITIONAL);
        final WEATextToSpeech textToSpeech = new WEATextToSpeech(activity);

        AlertDialog alertDialog;

        if(!alertState.isFeedbackGiven()){

            WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                    "Feedback button added to alert dialog");
            //set the cancel button
            AlertDialog.Builder feedbackBtn = builder.setNegativeButton("Feedback",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            WEAUtil.showMessageIfInDebugMode(getApplicationContext(), "Canceling dialog for feedback");
                            if(textToSpeech!= null) textToSpeech.shutdown();
                            idOfShownAlert = defaultId;

                            Toast.makeText(getApplicationContext(), Constants.SHOWING_FEEDBACK_FORM, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(activity, FeedbackWebViewActivity.class);
                            intent.putExtra(Constants.ALERT_ID, alert.getId());
                            startActivity(intent);
                        }
            });
        }

        //set the title and message of the alert
        builder.setTitle(AlertHelper.getTextWithStyle(alert.getAlertType() + " Alert", 1.3f, false));
        builder.setIcon(R.drawable.ic_launcher);

        final TextView message = new TextView(this);
        SpannableString string = AlertHelper.getTextWithStyle(alert.getText(), 1.7f, false);
        message.setText(string);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(message);

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                idOfShownAlert = alert1.getId();

                WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                        "Showing alert dialog");

                if(alertState!= null && !alertState.isAlreadyShown()){

                    WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                            "Showing alert for first time, may vibrate and speak");
                    alertState.setAlreadyShown(true);
                    alertState.setTimeWhenShownToUserInEpoch(System.currentTimeMillis());
                    alertState.setState(AlertState.State.shown);

                    WEASharedPreferences.saveAlertState(getApplicationContext(), alertState);
                    WEAHttpClient.sendAlertState(getApplicationContext(),
                            alertState.getJson(),
                            String.valueOf(alertState.getId()));

                    if(alert != null && alert.isPhoneExpectedToVibrate()){
                        WEAVibrator.vibrate(getApplicationContext());
                        WEAUtil.lightUpScreen(getApplicationContext());
                    }

                    if(alert != null && alert.isTextToSpeechExpected()){
                        textToSpeech.say(AlertHelper.getTextWithStyle(alert.getText(), 1f, false).toString(), 2);
                    }
                }
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                idOfShownAlert = defaultId;
                WEAUtil.showMessageIfInDebugMode(getApplicationContext(), "Dismissing dialog");
                if(textToSpeech!=null){
                    textToSpeech.shutdown();
                }
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                WEAUtil.showMessageIfInDebugMode(getApplicationContext(), "Canceling dialog");
                idOfShownAlert = defaultId;
                if(textToSpeech!=null) {
                    textToSpeech.shutdown();
                }
            }
        });

        return alertDialog;
    }

    public class NewConfigurationReceivedBroadcastReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewConfigurationReceivedBroadcastReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            try{
                final String message = intent.getStringExtra("MESSAGE");
                final boolean isOld = intent.getBooleanExtra(WEANewConfigurationIntent.STATUS, false);

                if(!isOld){
                    String json = WEASharedPreferences.readApplicationConfiguration(context);
                    configuration = AppConfiguration.fromJson(json);
                    if(listFragment != null) {
                        List<Alert> activeButNotShown = listFragment.updateListAndReturnAnyActiveAlertNotShown(
                                configuration.getAlertsList(context),
                                AlertHelper.getAlertStates(context, configuration.getAlertsList(context)));

                        if(activeButNotShown!=null && activeButNotShown.size()>0){
                            WEAUtil.showMessageIfInDebugMode(context, "Found an alert which is active but not sown.");
                            AlertHelper.showAlertIfInTargetOrIsNotGeotargeted(context, activeButNotShown.get(0).getId());
                        }
                    }
                }

                // Post the UI updating code to our Handler
                if(handler!= null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            programTryingToChangeSwitch = true;
                            Log.d("WEA", "Got new configuration broadcast " );
                            if(message!=null && !message.isEmpty()){
                                updateLastCheckTimeStatus();
                                WEAUtil.showMessageIfInDebugMode(context, message);
//                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }else{
                                setUpCouldNotConnectToNetwork();
                                Toast.makeText(context, "Could not connect to server, please check you network connection.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch(Exception ex){
                Logger.log(ex.getMessage());
            }
        }
    }

}
