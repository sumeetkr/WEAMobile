package sv.cmu.edu.weamobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;
import sv.cmu.edu.weamobile.views.MainActivity;

public class AWSNotificationReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            Logger.log("===== EXTERNAL RECEIVER ======= Received AWS message");
//            Log.e("inBackground",Boolean.toString(MainActivity.inBackground));

            if(intent.getAction() != null && intent.getAction()== "com.google.android.c2dm.intent.REGISTRATION"){
                Logger.log("Registering AWS Notification receiver");
                WEAHttpClient.registerPhoneAync(context);
            }else{
                Logger.log("Received notification " + extras.getString("default"));
                if(WEASharedPreferences.isShowNotificationsEnabled(context)){
                    WEAUtil.postNotification(new Intent(context, MainActivity.class), context, extras.getString("default"));
                }

                Intent startServiceIntent = new Intent(context, WEABackgroundService.class);
                startServiceIntent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
                context.startService(startServiceIntent);
            }


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

