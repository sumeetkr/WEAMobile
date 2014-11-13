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

    public static AlertState getAlertState(Context context, String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        AlertState alertState = null;
        if (sharedPreferences != null) {
            String res = sharedPreferences.getString(Constants.ALERT_STATE+id, null);
            if(res!=null){
                alertState = AlertState.fromJson(res);
            }
        }
        return alertState;
    }

    public static void saveAlertState(Context context, AlertState alertState) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.ALERT_STATE + alertState.getId(), alertState.getJson());
            editor.commit();
            Logger.log("Saved new alert to shared preferences "+ alertState.getId());
        }
    }

    public static void addAlertStateToPreferences(Context context, Alert alert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String res = sharedPreferences.getString(Constants.ALERT_STATE + alert.getId(), null);
            if(res==null){
                saveAlertState(context, new AlertState(alert.getId()));
                Logger.log("Added to shared preferences, alert id " + alert.getId());
            }
        }
    }

    public static void deleteAlertState(Context context, String alertId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String res = sharedPreferences.getString(Constants.ALERT_STATE + alertId, null);
            if(res!=null){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Constants.ALERT_STATE + alertId);
                editor.commit();
                Logger.log("Removed from shared preferences, alert id " + alertId);
            }
        }

    }
}