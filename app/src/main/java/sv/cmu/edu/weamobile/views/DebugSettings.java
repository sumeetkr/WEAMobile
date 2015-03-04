package sv.cmu.edu.weamobile.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.utility.ActivityRecognition.UserActivityRecognizer;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;

public class DebugSettings extends ActionBarActivity {

    private CheckBox chkViewDebugMessages;
    private CheckBox chkStartActivityRecognition;
    private CheckBox chkShowLocationHistory;
    private TextView txtMessages;
    private UserActivityRecognizer activityRecognizer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_settings);
        final Context ctxt = this;

        chkViewDebugMessages = (CheckBox)findViewById(R.id.checkBoxDebugMessages);
        chkStartActivityRecognition = (CheckBox) findViewById(R.id.checkBoxActivityRecognition);
        chkShowLocationHistory = (CheckBox) findViewById(R.id.chkLocationHistory);
        txtMessages = (TextView) findViewById(R.id.txtDebugMessages);

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

        chkStartActivityRecognition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(activityRecognizer== null) activityRecognizer = new UserActivityRecognizer(ctxt);
                    activityRecognizer.startActivityRecognitionScan();
                }else{
                    if(activityRecognizer != null){
                        activityRecognizer.stopActivityRecognitionScan();
                    }
                }
            }
        });

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
}
