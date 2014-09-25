package sv.cmu.edu.weamobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

            }
        };

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("WEA", "scheduling wake up");
        //WEAAlarmManager.setupAlarmToWakeUpApplicationAtScheduledTime(this.getApplicationContext(), 4000);

        BroadcastReceiver newAlertBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("WEA", "Received new broadcast alert" + intent.getAction());
                Toast toast = Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(newAlertBroadcastReceiver,
                new IntentFilter(WEANewAlertIntent.WEA_NEW_ALERT));

        WEAAlarmManager.setupAlarmToWakeUpApplicationAtScheduledTime(this.getApplicationContext(), 5000);
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
