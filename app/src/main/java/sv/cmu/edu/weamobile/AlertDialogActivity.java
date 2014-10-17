package sv.cmu.edu.weamobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import sv.cmu.edu.weamobile.Utility.Logger;


public class AlertDialogActivity extends Activity {

    private Vibrator vibrator;
    private TextToSpeech tts;
    private Intent intent;
    private String message;
    private boolean isDialogShown;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        addEventListenersToButtons();

        Logger.log("Create alert dialog");
        try {
            alertUserWithVibrationAndSpeech();
            //showDialog();
        }catch (Exception ex){
         Log.d("WEA", "Exception while showing dialog " + ex.getMessage());
        }
    }

    private void addEventListenersToButtons() {
        Button close_button = (Button) findViewById(R.id.buttonOk);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnFeedback = (Button) findViewById(R.id.buttonFeedback);
        final Activity activity = this;
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FeedbackWebViewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDialog() {
        if(isDialogShown){
            dialog.cancel();
            Logger.log("One dialog is already shown");
        }
        dialog = createDialog(this.getApplicationContext());
        dialog.show();
        isDialogShown = true;
    }

    private void alertUserWithVibrationAndSpeech() {
        intent = getIntent();
        message = intent.getStringExtra("Message");

        vibrator = (Vibrator) this.getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {200, 200, 200, 200, 200, 200, 200, 400,200,
                400,200, 400,200, 200, 200, 200, 200, 200, 400,
                200, 200, 200, 200, 200, 200,400,200, 400,
                200, 400,200, 200, 200, 200, 200, 200};

        vibrator.vibrate(pattern, -1);

        tts = new TextToSpeech(this, new TTSListener(message, 2));
    }

    @Override
    protected void onDestroy() {
        vibrator = null;
        tts.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alert_dialog, menu);
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
    protected void onNewIntent(Intent intent) {
        Logger.log("New intent for alert dialog");
        alertUserWithVibrationAndSpeech();
        //showDialog();
    }

    private AlertDialog createDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        final Activity activity = this;

        //set the cancel button
        AlertDialog.Builder ok = builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                isDialogShown = false;
                Intent intent = new Intent(activity, FeedbackWebViewActivity.class);
                startActivity(intent);
            }
        });

        //set the title and message of the alert
        builder.setTitle("WEA Alert");
        builder.setMessage(message);

        alert = builder.create();
        return alert;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected class TTSListener implements TextToSpeech.OnInitListener {

        String what_to_speak = null;
        Integer how_many_times = null;

        public TTSListener(String to_speak, Integer times) {
            what_to_speak = to_speak;
            how_many_times = times;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.speak(what_to_speak, TextToSpeech.QUEUE_FLUSH, null);
                    for (Integer n = 1; n < how_many_times; n++) {
                        tts.speak(what_to_speak, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        }
    }
}
