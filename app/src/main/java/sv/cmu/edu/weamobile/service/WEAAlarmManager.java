package sv.cmu.edu.weamobile.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEAAlarmManager {
    public static final int ALARM_REQUEST_CODE_FOR_REPEATING = Integer.MAX_VALUE;

    public static void setupAlarmForAlertAtScheduledTime(Context context,
                                                                    int alertId,
                                                                    long triggerAtEpochInMillis){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up alarm to trigger at " +
                String.valueOf(triggerAtEpochInMillis));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.putExtra("alertId", alertId);
        intent.setAction(WEABackgroundService.SHOW_ALERT);
        intent.addCategory(WEABackgroundService.SHOW_ALERT);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                alertId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//        alarmMgr.setWindow(AlarmManager.RTC_WAKEUP, triggerAfterMilliSeconds, 1000, alarmIntent);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtEpochInMillis, alarmIntent);
    }

    public static void setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
            Context context,
            long timeBetweenRepeatsInMilliSeconds){

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up repeating alarm to trigger after milliseconds "
                + String.valueOf(timeBetweenRepeatsInMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(WEABackgroundService.SEND_HEARTBEAT);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE_FOR_REPEATING,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Logger.log("Alarm is already active");


        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                1000,
                timeBetweenRepeatsInMilliSeconds,
                alarmIntent);
    }
}
