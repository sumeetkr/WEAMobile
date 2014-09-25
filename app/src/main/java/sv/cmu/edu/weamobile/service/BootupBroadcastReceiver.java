package sv.cmu.edu.weamobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.setClass(context, WEABackgroundService.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}
