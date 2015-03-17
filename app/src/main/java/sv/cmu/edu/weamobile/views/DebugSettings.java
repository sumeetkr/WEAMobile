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
import sv.cmu.edu.weamobile.utility.ActivityRecognition.UserActivityRecognizer;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;

public class DebugSettings extends ActionBarActivity {

    private CheckBox chkViewDebugMessages;
    private CheckBox chkStartActivityRecognition;
    private CheckBox chkShowLocationHistory;
    private CheckBox chkMotion;
    private TextView txtMessages;
    private UserActivityRecognizer activityRecognizer;
    private NewActivityReceiver activityBroadcastReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_settings);
        final Context ctxt = this;

        chkViewDebugMessages = (CheckBox)findViewById(R.id.checkBoxDebugMessages);
        chkStartActivityRecognition = (CheckBox) findViewById(R.id.checkBoxActivityRecognition);
        chkShowLocationHistory = (CheckBox) findViewById(R.id.chkLocationHistory);
        txtMessages = (TextView) findViewById(R.id.txtDebugMessages);
        chkMotion = (CheckBox) findViewById(R.id.chkMotion);

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

        chkStartActivityRecognition.setChecked(WEASharedPreferences.isActivityRecognitionEnabled(getApplicationContext()));
        chkStartActivityRecognition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WEASharedPreferences.setActivityRecognitionEnabled(getApplicationContext(), true);
                    if(activityRecognizer== null) activityRecognizer = new UserActivityRecognizer(ctxt);
                    txtMessages.setText("Looking for a new Activity.");
                    activityRecognizer.startActivityRecognitionScan();
                }else{
                    WEASharedPreferences.setActivityRecognitionEnabled(getApplicationContext(), false);
                    if(activityRecognizer != null){
                        activityRecognizer.stopActivityRecognitionScan();
                    }
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

        chkMotion.setChecked(WEASharedPreferences.isMotionEnabled(getApplicationContext()));
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

        registerNewActivityReceiver();
    }

    private void registerNewActivityReceiver() {
        activityBroadcastReceiver = new NewActivityReceiver(new Handler());

        Logger.log("WEA", "New configuration receiver created in main activity");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_ACTIVITY");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getApplicationContext().registerReceiver(activityBroadcastReceiver, filter);
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

    @Override
    protected void onDestroy(){
        WEAUtil.showMessageIfInDebugMode(getApplicationContext(),
                "Called onDestroy of debug settings view");
        if(activityBroadcastReceiver!= null){
            getApplication().unregisterReceiver(activityBroadcastReceiver);
            activityBroadcastReceiver = null;
        }

        super.onDestroy();
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
                        txtMessages.setText(intent.getStringExtra("message"));
                    }
                });
            }
        }
    };
}
