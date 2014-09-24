package sv.cmu.edu.weamobile.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String SYNCHRONIZE_ALERTS = "SynchronizeAlerts";
    public AlarmReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Triggered onReceive");
        Intent service = new Intent(context, WEABackgroundService.class);
        startWakefulService(context, service);
    }
}
