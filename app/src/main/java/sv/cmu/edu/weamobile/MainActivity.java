package sv.cmu.edu.weamobile;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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

        handler = new Handler();

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("WEA", "scheduling one time wakeup");
        WEAAlarmManager.setupAlarmToWakeUpApplicationAtScheduledTime(this.getApplicationContext(), 20000);
        //WEAAlarmManager.setupRepeatingAlarm(this.getApplicationContext(), 1000*60*5);
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
        // Register mMessageReceiver to receive messages.
//        LocalBroadcastManager.getInstance(this).registerReceiver(newAlertReciver,new IntentFilter());
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
}
