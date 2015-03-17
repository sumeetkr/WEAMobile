package sv.cmu.edu.weamobile.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import sv.cmu.edu.weamobile.views.MainActivity;

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            Log.e("EXTER_RECV","===== EXTERNAL RECEIVER =======");
            Log.e("inBackground",Boolean.toString(MainActivity.inBackground));
            if(!MainActivity.inBackground){
                Log.e("EXTER_RECV","===== EXTERNAL RECEIVER ======= : Sending to app");
                MessageReceivingService.sendToApp(extras, context);
            }
            else{
                Log.e("EXTER_RECV","===== EXTERNAL RECEIVER ======= : Sending to log");
                MessageReceivingService.saveToLog(extras, context);
            }
        }
    }
}

