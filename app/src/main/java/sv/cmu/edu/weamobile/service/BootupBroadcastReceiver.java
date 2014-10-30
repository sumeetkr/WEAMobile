package sv.cmu.edu.weamobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sv.cmu.edu.weamobile.Utility.Constants;
import sv.cmu.edu.weamobile.Utility.Logger;

/**
 * @author Maxim Kovalev
 * As in http://stackoverflow.com/questions/6391902/how-to-start-an-application-on-startup
 */
public class BootupBroadcastReceiver extends BroadcastReceiver {
    /* (non-Javadoc)
      * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
      */
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log("boot up intent received");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            WEAAlarmManager.setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
                    context,
                    Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES*60*1000);

//            Intent i = new Intent();
//            i.setClass(context, WEABackgroundService.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startService(i);
        }

    }
}
