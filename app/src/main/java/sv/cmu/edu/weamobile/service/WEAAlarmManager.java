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
    public static final int ALARM_REQUEST_CODE_FOR_REPEATING = 1111;
    public static final int ALARM_REQUEST_CODE_FOR_SINGLE = 1112;

    public static void setupAlarmToFetchConfigurationAtScheduledTime(Context context,
                                                                     long triggerAfterMilliSeconds){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up alarm to trigger after milliseconds " +
                String.valueOf(triggerAfterMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);
        intent.addCategory(WEABackgroundService.FETCH_CONFIGURATION);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE_FOR_SINGLE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAfterMilliSeconds, alarmIntent);
    }

    public static void setupAlarmForAlertAtScheduledTime(Context context,
                                                                    int alertId,
                                                                    long triggerAfterMilliSeconds){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up alarm to trigger after milliseconds " +
                String.valueOf(triggerAfterMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.putExtra("alertId", alertId);
        intent.setAction(WEABackgroundService.SHOW_ALERT);
        intent.addCategory(WEABackgroundService.SHOW_ALERT);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE_FOR_SINGLE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setWindow(AlarmManager.RTC_WAKEUP, triggerAfterMilliSeconds, 1000, alarmIntent);
    }

    public static void setupRepeatingAlarmToWakeUpApplicationToFetchConfiguration(
            Context context,
            long timeBetweenRepeatsInMilliSeconds){

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("WEA", "setting up repeating alarm to trigger after milliseconds "
                + String.valueOf(timeBetweenRepeatsInMilliSeconds));

        Intent intent = new Intent(context, WEABackgroundService.class);
        intent.setAction(WEABackgroundService.FETCH_CONFIGURATION);

        PendingIntent alarmIntent = PendingIntent.getService(
                context,
                WEAAlarmManager.ALARM_REQUEST_CODE_FOR_REPEATING,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Logger.log("Alarm is already active");


        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                timeBetweenRepeatsInMilliSeconds,
                timeBetweenRepeatsInMilliSeconds,
                alarmIntent);
    }
}
