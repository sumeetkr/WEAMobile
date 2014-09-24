package sv.cmu.edu.weamobile.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEAAlarmManager {
    public static final int ALARM_REQUEST_CODE = 1111;

    public static void setupAlarmToWakeUpApplicationAtScheduledTime(Context context, long triggerAfterMiliSeconds){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.SYNCHRONIZE_ALERTS);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                WEAAlarmManager.ALARM_REQUEST_CODE, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAfterMiliSeconds, alarmIntent);
    }
}
