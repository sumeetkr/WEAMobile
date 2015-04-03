package sv.cmu.edu.weamobile.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;

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

//    public static void clearSavedConfiguration(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
//        if (sharedPreferences != null) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.clear();
//            editor.commit();
//            Logger.log("cleard preferences");
//        }
//    }

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

    public static AlertState getAlertState(Context context, Alert alert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        AlertState alertState = null;
        if (sharedPreferences != null) {
            String res = sharedPreferences.getString(Constants.ALERT_STATE+alert.getId()+alert.getScheduledEpochInSeconds(), null);
            if(res!=null){
                alertState = AlertState.fromJson(res);
            }else{
                Logger.log("Alert state not found");
            }
        }

        Logger.log("got state from shared preferences "+ Constants.ALERT_STATE+alert.getId()+alert.getScheduledEpochInSeconds());
        return alertState;
    }

    public static void saveAlertState(Context context, AlertState alertState) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.ALERT_STATE+alertState.getId()+alertState.getScheduledEpochInSeconds(), alertState.getJson());
            editor.commit();
            Logger.log("Saved new alert to shared preferences "+ Constants.ALERT_STATE+alertState.getId()+alertState.getScheduledEpochInSeconds());
        }
    }

    public static void addAlertStateToPreferences(Context context, Alert alert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String res = sharedPreferences.getString(Constants.ALERT_STATE+alert.getId()+alert.getScheduledEpochInSeconds(), null);
            if(res==null){
                saveAlertState(context, new AlertState(alert.getId(), alert.getScheduledFor()));
                Logger.log("Added to shared preferences, alert id " + alert.getId());
            }
        }
    }

    public static void deleteAlertState(Context context, Alert alert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String res = sharedPreferences.getString(Constants.ALERT_STATE+alert.getId()+alert.getScheduledEpochInSeconds(), null);
            if(res!=null){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Constants.ALERT_STATE+alert.getId()+alert.getScheduledEpochInSeconds());
                editor.commit();
                Logger.log("Removed from shared preferences, alert id " + alert.getId());
            }
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
            editor.putBoolean(Constants.IS_DEBUG_MODE,isDebugMode);
            editor.commit();
            Logger.log("Saved debug mode to shared preferences "+ isDebugMode);
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
            editor.putBoolean(Constants.IS_LOCATION_HISTORY_ENABLED,enable);
            editor.commit();
            Logger.log("Saved location history mode to shared preferences "+ enable);
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
        }
    }
}
