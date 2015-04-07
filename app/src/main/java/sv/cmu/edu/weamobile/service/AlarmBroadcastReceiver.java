package sv.cmu.edu.weamobile.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String SYNCHRONIZE_ALERTS = "SynchronizeAlerts";

    public AlarmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("WEA AlarmReceiver", "Triggered onReceive");
        Intent service = new Intent(context, WEABackgroundService.class);
        service.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        context.startService(service);
    }
}
