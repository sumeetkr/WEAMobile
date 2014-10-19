package sv.cmu.edu.weamobile.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEAAlarmManager {
    public static final int ALARM_REQUEST_CODE = 1111;

    public static void setupAlarmToWakeUpApplicationAtScheduledTime(Context context,
                                                                    long triggerAfterMilliSeconds){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up alarm to trigger after milliseconds " +
                String.valueOf(triggerAfterMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        intent.addCategory(WEABackgroundService.FETCH_CONFIGURATION);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAfterMilliSeconds, alarmIntent);
    }

    public static void setupAlarmForAlertAtScheduledTime(Context context,
                                                                    long triggerAfterMilliSeconds){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up alarm to trigger after milliseconds " +
                String.valueOf(triggerAfterMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(WEABackgroundService.SHOW_ALERT);
        intent.addCategory(WEABackgroundService.SHOW_ALERT);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAfterMilliSeconds, alarmIntent);
    }

    public static void setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
            Context context,
            long timeBetweenRepeatsInMilliSeconds){

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up repeating alarm to trigger after milliseconds "
                + String.valueOf(timeBetweenRepeatsInMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);

        long firstTime = SystemClock.elapsedRealtime();

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0,
                timeBetweenRepeatsInMilliSeconds,
                alarmIntent);
    }
}
