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

import sv.cmu.edu.weamobile.Utility.AppConfigurationFactory;
import sv.cmu.edu.weamobile.Utility.WEAUtil;
import sv.cmu.edu.weamobile.service.WEAAlarmManager;
import sv.cmu.edu.weamobile.service.WEABackgroundService;

public class MainActivity extends FragmentActivity
        implements AlertListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Handler handler;
    private NewAlertBroadcastReceiver newAlertReciver;
    private WEABackgroundService mBoundService;
    private boolean mIsBound;
    private boolean isAlarmScheduled= false;
    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);

        if (findViewById(R.id.alert_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((AlertListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.alert_list))
                    .setActivateOnItemClick(true);
        }

        mySwitch = (Switch) findViewById(R.id.switch2);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    mySwitch.setText("Syncing..");
                    fetchConfig();
                }else{
//                    fetchConfig();
                    mySwitch.setText("Alerts disabled");
                }

            }
        });

        handler = new Handler();

    }

    @Override
    protected void onStart(){
        super.onStart();
        //WEAAlarmManager.setupAlarmToWakeUpApplicationAtScheduledTime(this.getApplicationContext(), 60*000);
//        if(!isAlarmScheduled){
            Log.d("WEA", "scheduling alarm");
            WEAAlarmManager.setupRepeatingAlarm(this.getApplicationContext(), 1000*60*1);
            isAlarmScheduled =  true;
//        }
    }


    @Override
    protected void onResume(){
        super.onResume();

        if(newAlertReciver ==null) newAlertReciver = new NewAlertBroadcastReceiver(handler);
        Log.d("WEA", "Alert receiver created in main activity");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_ALERT");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getApplicationContext().registerReceiver(newAlertReciver, filter);

        updateStatus();

    }

    private void updateStatus() {
        String time = AppConfigurationFactory.getStringProperty(getApplicationContext(), "lastTimeChecked");
        if(time != null && !time.isEmpty() && (Long.parseLong(time)-System.currentTimeMillis()<30*60*1000)){
            mySwitch.setChecked(true);
            mySwitch.setText("Synced at: " + WEAUtil.getTimeString(Long.parseLong(time) / 1000));
        }else{
            mySwitch.setChecked(false);
            fetchConfig();
        }
    }

    private void fetchConfig() {
        WEAAlarmManager.setupAlarmToWakeUpApplicationAtScheduledTime(this.getApplicationContext(), 60*000);
//
//        Intent intent = new Intent(getApplicationContext(), WEABackgroundService.class);
//        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
//        sendBroadcast(intent);
    }

    @Override
    protected void onPause(){
        // Register mMessageReceiver to receive messages.
        if(newAlertReciver!= null){
            getApplication().unregisterReceiver(newAlertReciver);
            newAlertReciver = null;
        }
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(newAlertReciver);

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
            startActivity(detailIntent);
        }
    }

    public class NewAlertBroadcastReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewAlertBroadcastReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d("WEA", "Got new alert broadcast2 ");
            // Extract data included in the Intent
            final String message = intent.getStringExtra("MESSAGE");

            // Post the UI updating code to our Handler
            if(handler!= null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus();
                        Log.d("WEA", "Got new alert broadcast " );
//                        Toast.makeText(context, "Alert !!: " + message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

}
