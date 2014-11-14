package sv.cmu.edu.weamobile.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.AppConfiguration;
import sv.cmu.edu.weamobile.service.WEAAlarmManager;
import sv.cmu.edu.weamobile.service.WEABackgroundService;
import sv.cmu.edu.weamobile.service.WEANewConfigurationIntent;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEATextToSpeech;
import sv.cmu.edu.weamobile.utility.WEAUtil;
import sv.cmu.edu.weamobile.utility.WEAVibrator;

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
    private boolean isDialogShown = false;
    private boolean programTryingToChangeSwitch = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);

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

        if(!getIntent().hasExtra(Constants.ALERT_ID) && getIntent().getAction() != (Constants.SHOW_MAIN_VIEW_ACTION)){
            Log.d("WEA", "scheduling alarm");
            WEAAlarmManager.setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
                    this.getApplicationContext(),
                    Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES * 60 * 1000);
        }
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
                        if (Constants.IS_IN_DEBUG_MODE && configuration != null) {
                            AlertHelper.clearAlertStates(getApplicationContext(), configuration.getAlerts(getApplicationContext()));
                        }
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
    protected void onResume(){
        super.onResume();

        registerNewConfigurationReceiver();

        String json = WEASharedPreferences.readApplicationConfiguration(getApplicationContext());
        configuration = AppConfiguration.fromJson(json);

        if(listFragment != null && configuration!= null) {
            Alert [] alerts = configuration.getAlerts(getApplicationContext());
            AlertState [] alertStates = AlertHelper.getAlertStates(getApplicationContext(), alerts);
            listFragment.updateListAndReturnAnyActiveAlertNotShown(alerts, alertStates);
        }

        updateStatus();

        if(getIntent().hasExtra(Constants.ALERT_ID)){
            String alertId = getIntent().getStringExtra(Constants.ALERT_ID);
            onItemSelected(alertId);
        }
    }

    private void registerNewConfigurationReceiver() {
        if(newAlertReciver ==null) {
            newAlertReciver = new NewConfigurationReceivedBroadcastReceiver(handler);
        }
        Log.d("WEA", "New configuration receiver created in main activity");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_ALERT");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getApplicationContext().registerReceiver(newAlertReciver, filter);
    }

    private void updateStatus() {
        String time = WEASharedPreferences.getStringProperty(getApplicationContext(), "lastTimeChecked");
        if(time != null && !time.isEmpty() && (Long.parseLong(time)-System.currentTimeMillis()< Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES*60*1000)){
            setUpToDate();
        }else{
            mySwitch.setChecked(false);
            //fetchConfig();
        }
    }

    private void setUpToDate() {
        mySwitch.setChecked(true);
        long time = Long.parseLong(WEASharedPreferences.getStringProperty(getApplicationContext(),
                "lastTimeChecked"));
        mySwitch.setText("Synced at: " +
                WEAUtil.getTimeStringFromEpoch(time/1000));
    }

    private void fetchConfig() {
        Intent intent = new Intent(getApplicationContext(), WEABackgroundService.class);
        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        startService(intent);
    }

    @Override
    protected void onPause(){
        // Register mMessageReceiver to receive messages.
        if(newAlertReciver!= null) {
            getApplication().unregisterReceiver(newAlertReciver);
            newAlertReciver = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy(){
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
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
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
                startActivity(detailIntent);
            }else{
                //if(!isDialogShown){
                showDialog(alert);
                //}
            }
        }
    }

    public void onBackPressed() {

        if(isDialogShown && dialog != null){
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
        if(!isDialogShown) {
            isDialogShown = true;
            dialog = createDialog(getApplicationContext(), alert);
            dialog.show();
        }
    }

    private AlertDialog createDialog(final Context context, Alert alert1){

        final Alert alert = alert1;
        final Activity activity = this;
        final AlertState alertState = WEASharedPreferences.getAlertState(context, String.valueOf(alert.getId()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this,  AlertDialog.THEME_TRADITIONAL);
        final WEATextToSpeech textToSpeech = new WEATextToSpeech(activity);

        AlertDialog alertDialog;

        if(!alertState.isFeedbackGiven()){
            //set the cancel button
            AlertDialog.Builder feedbackBtn = builder.setNegativeButton("Feedback",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if(textToSpeech!= null) textToSpeech.shutdown();
                            isDialogShown = false;

                            alertState.setFeedbackGiven(true);
                            WEASharedPreferences.saveAlertState(context, alertState);

                            Intent intent = new Intent(activity, FeedbackWebViewActivity.class);
                            intent.putExtra(Constants.ALERT_ID, alert.getId());
                            startActivity(intent);
                        }
            });
        }

        //set the title and message of the alert
        builder.setTitle(alert.getAlertType() + " Alert");
        builder.setIcon(R.drawable.ic_launcher);

        final TextView message = new TextView(this);
        SpannableString string = AlertHelper.getTextWithStyle(alert.getText(), 40, false);
        message.setText(string);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(message);

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(alertState!= null && !alertState.isAlreadyShown() && alert.isActive()){

                    alertState.setAlreadyShown(true);
                    alertState.setTimeWhenShownToUserInEpoch(System.currentTimeMillis());
                    alertState.setState(AlertState.State.shown);
                    WEASharedPreferences.saveAlertState(getApplicationContext(), alertState);
                    WEAHttpClient.sendAlertState(getApplicationContext(),
                            alertState.getJson(),
                            String.valueOf(alertState.getId()));

                    if(alert != null && alert.isPhoneExpectedToVibrate()){
                        WEAVibrator.vibrate(getApplicationContext());
                    }

                    if(alert != null && alert.isTextToSpeechExpected()){
                        textToSpeech.say(AlertHelper.getTextWithStyle(alert.getText(), 33, false).toString(), 2);
                    }
                }

                // Make the textview clickable. Must be called after show()
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDialogShown = false;
                if(textToSpeech!=null) textToSpeech.shutdown();
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isDialogShown = false;
                if(textToSpeech!=null) textToSpeech.shutdown();
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
                        Alert activeButNotShown = listFragment.updateListAndReturnAnyActiveAlertNotShown(
                                configuration.getAlerts(context),
                                AlertHelper.getAlertStates(context, configuration.getAlerts(context)));

                        if(activeButNotShown!=null){
                            AlertHelper.showAlertIfInTarget(context,activeButNotShown.getId());
                        }
                    }
                }

                // Post the UI updating code to our Handler
                if(handler!= null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            programTryingToChangeSwitch = true;
                            updateStatus();
                            Log.d("WEA", "Got new congiguration broadcast " );
                            if(message!=null && !message.isEmpty()){
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
