package sv.cmu.edu.weamobile.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sumeet on 10/7/14.
 */
public class WEASharedPreferences {

    public static String getStringProperty(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        String res = null;
        if (sharedPreferences != null) {
            res = sharedPreferences.getString(key, null);
        }
        return res;
    }

    public static void setStringProperty(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
            Logger.log("Set " + key + " property = " + value);
        }
    }

    public static String readApplicationConfiguration(Context context ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        String res = null;
        if (sharedPreferences != null) {
            res = sharedPreferences.getString(Constants.CONFIG_JSON, null);
        }
        return res;
    }

    public static void saveApplicationConfiguration(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.CONFIG_JSON, json);
            editor.commit();
            Logger.log("Saved new json to shared preferences");
        }

    }

    public static boolean isInDebugMode(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_DEBUG_MODE, false);
        }
        return result;
    }

    public static void setDebugMode(Context context, boolean isDebugMode){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_DEBUG_MODE, isDebugMode);
            editor.commit();
            Logger.log("Saved debug mode to shared preferences "+ isDebugMode);
            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isLocationHistoryEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_LOCATION_HISTORY_ENABLED, false);
        }
        return result;
    }

    public static void setIsLocationHistoryEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_LOCATION_HISTORY_ENABLED, enable);
            editor.commit();
            Logger.log("Saved location history mode to shared preferences "+ enable);
            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isActivityHistoryEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_ACTIVITY_HISTORY_ENABLED, false);
        }
        return result;
    }

    public static void setActivityHistoryEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_ACTIVITY_HISTORY_ENABLED, enable);
            editor.commit();
            Logger.log("Saved activity history mode to shared preferences "+ enable);
            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isMotionPredictionEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_MOTION_ENABLED, false);
        }
        return result;
    }

    public static void setMotionEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_MOTION_ENABLED,enable);
            editor.commit();
            Logger.log("Saved motion mode to shared preferences "+ enable);

            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isShowNotificationsEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_SHOW_NOTIFICATIONS_ENABLED, false);
        }
        return result;
    }

    public static void setShowNotificationsEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_SHOW_NOTIFICATIONS_ENABLED,enable);
            editor.commit();
            Logger.log("Saved show notifications enabled to shared preferences "+ enable);

            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isShowAllAlertsEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_SHOW_ALL_ALERTS_ENABLED, false);
        }
        return result;
    }

    public static void setShowAllAlertsEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_SHOW_ALL_ALERTS_ENABLED,enable);
            editor.commit();
            Logger.log("Saved show all alerts enabled to shared preferences "+ enable);

            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isActivityRecognitionEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_ACTIVITY_RECOGNITION_ENABLED, false);
        }
        return result;
    }

    public static void setActivityRecognitionEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_ACTIVITY_RECOGNITION_ENABLED,enable);
            editor.commit();
            Logger.log("Saved activity recognition mode to shared preferences "+ enable);

            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static boolean isFetchAlertsEnabled(Context context){
        boolean result = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(Constants.IS_FETCH_ALERTS_PANEL_ENABLED, false);
        }
        return result;
    }

    public static void setFetchAlertsEnabled(Context context, boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.IS_FETCH_ALERTS_PANEL_ENABLED,enable);
            editor.commit();
            Logger.log("Saved fetch alerts mode to shared preferences "+ enable);

            setLastTimeWhenSettingGotUpdated(context);
        }
    }

    public static void setLastTimeWhenSettingGotUpdated(Context context){
        try{
            setStringProperty(context, Constants.EPOCH_TIME_WHEN_LAST_UPDATED, String.valueOf(System.currentTimeMillis()));
        }catch (Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    public static void restoreDebugSetting(Context context, int timeGap){
        try{
            Logger.log("restoreDebugSetting called");
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
            if (sharedPreferences != null) {

                String time = getStringProperty(context, Constants.EPOCH_TIME_WHEN_LAST_UPDATED);
                if(time!= null && !time.isEmpty()){
                    Long lastUpdatedTImeInEpoch = Long.valueOf(time);


                    if(lastUpdatedTImeInEpoch != null && lastUpdatedTImeInEpoch >0){
                        Long currentTime = System.currentTimeMillis();

                        if(currentTime - lastUpdatedTImeInEpoch > timeGap){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            boolean enable = false;

                            editor.putBoolean(Constants.IS_DEBUG_MODE, enable);
                            editor.putBoolean(Constants.IS_LOCATION_HISTORY_ENABLED,enable);
                            editor.putBoolean(Constants.IS_ACTIVITY_HISTORY_ENABLED, enable);
                            editor.putBoolean(Constants.IS_MOTION_ENABLED,enable);
                            editor.putBoolean(Constants.IS_SHOW_NOTIFICATIONS_ENABLED,enable);
                            editor.putBoolean(Constants.IS_SHOW_ALL_ALERTS_ENABLED,enable);
                            editor.putBoolean(Constants.IS_ACTIVITY_RECOGNITION_ENABLED,enable);
                            editor.putBoolean(Constants.IS_FETCH_ALERTS_PANEL_ENABLED, enable);

                            editor.commit();
                            Logger.log("Restored debug setting to default ");
                        }
                    }
                }
            }

        }catch (Exception ex){
            Logger.log("restoreDebugSetting" + ex.getMessage());

        }
    }
}
