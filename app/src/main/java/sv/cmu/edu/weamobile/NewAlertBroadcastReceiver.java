package sv.cmu.edu.weamobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class NewAlertBroadcastReceiver extends BroadcastReceiver {
    private final Handler handler;

    public NewAlertBroadcastReceiver(){
        handler = null;
    }

    public NewAlertBroadcastReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("WEA", "Got new alert broadcast2 ");
        // Extract data included in the Intent
        final String message = intent.getStringExtra("MESSAGE");
        Toast.makeText(context, "Found Beacon: " + message, Toast.LENGTH_SHORT).show();


        // Post the UI updating code to our Handler
        if(handler!= null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("WEA", "Got new alert broadcast " );
                    Toast.makeText(context, "Found Beacon: " + message, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
