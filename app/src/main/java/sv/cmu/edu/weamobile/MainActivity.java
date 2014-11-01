package sv.cmu.edu.weamobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.Utility.AppConfigurationFactory;
import sv.cmu.edu.weamobile.Utility.Constants;
import sv.cmu.edu.weamobile.Utility.Logger;
import sv.cmu.edu.weamobile.Utility.WEAUtil;
import sv.cmu.edu.weamobile.service.WEAAlarmManager;
import sv.cmu.edu.weamobile.service.WEANewAlertIntent;

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

        mySwitch = (Switch) findViewById(R.id.switch2);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mySwitch.setText("Syncing..");
                    fetchConfig();
                } else {
                    mySwitch.setText("Alerts disabled");
                }
            }
        });

        handler = new Handler();

        Log.d("WEA", "scheduling alarm");
        WEAAlarmManager.setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
                this.getApplicationContext(),
                Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES*60*1000);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(newAlertReciver ==null) newAlertReciver = new NewConfigurationReceivedBroadcastReceiver(handler);
        Log.d("WEA", "Alert receiver created in main activity");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_ALERT");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getApplicationContext().registerReceiver(newAlertReciver, filter);

        updateStatus();

    }

    private void updateStatus() {
        String time = AppConfigurationFactory.getStringProperty(getApplicationContext(), "lastTimeChecked");
        if(time != null && !time.isEmpty() && (Long.parseLong(time)-System.currentTimeMillis()< 60*1000)){
            mySwitch.setChecked(true);
            mySwitch.setText("Synced at: " + WEAUtil.getTimeStringFromEpoch(Long.parseLong(time) / 1000));
        }else{
            mySwitch.setChecked(false);
            fetchConfig();
        }
    }

    private void fetchConfig() {
        Logger.log("Scheduling call to fetch configuration");
        WEAAlarmManager.setupAlarmToFetchConfigurationAtScheduledTime(
                this.getApplicationContext(),
                Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES * 60 * 1000);
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
            arguments.putString(AlertDetailFragment.ARG_ITEM_ID, id);
            AlertDetailFragment fragment = new AlertDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.alert_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.

            Intent detailIntent = new Intent(this, AlertDetailActivity.class);
            detailIntent.putExtra(AlertDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(AlertDetailFragment.ALERTS_JSON, configuration.getJson());
            startActivity(detailIntent);
        }
    }

    public class NewConfigurationReceivedBroadcastReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewConfigurationReceivedBroadcastReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String message = intent.getStringExtra("MESSAGE");
            String json = intent.getStringExtra(WEANewAlertIntent.CONFIG_JSON);
            configuration = AppConfiguration.fromJson(json);
            listFragment.updateList(configuration.getAlerts());

            // Post the UI updating code to our Handler
            if(handler!= null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus();
                        Log.d("WEA", "Got new alert broadcast " );
                        if(message!=null && !message.isEmpty()){
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

}
