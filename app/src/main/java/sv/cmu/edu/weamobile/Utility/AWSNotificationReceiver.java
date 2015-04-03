package sv.cmu.edu.weamobile.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import sv.cmu.edu.weamobile.service.WEABackgroundService;
import sv.cmu.edu.weamobile.views.MainActivity;

public class AWSNotificationReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            Logger.log("===== EXTERNAL RECEIVER ======= Received AWS message");
//            Log.e("inBackground",Boolean.toString(MainActivity.inBackground));

            if(WEASharedPreferences.isShowNotificationsEnabled(context)){
                WEAUtil.postNotification(new Intent(context, MainActivity.class), context, extras.getString("default"));
            }

            Intent startServiceIntent = new Intent(context, WEABackgroundService.class);
            startServiceIntent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
            context.startService(startServiceIntent);

//            if(!MainActivity.inBackground){
//                Logger.log("EXTER_RECV", "===== EXTERNAL RECEIVER ======= : Sending to app");
//                MessageReceivingService.sendToApp(extras, context);
//            }
//            else{
//                Logger.log("EXTER_RECV", "===== EXTERNAL RECEIVER ======= : Sending to log");
//                MessageReceivingService.saveToLog(extras, context);
//            }
        }
    }
}

