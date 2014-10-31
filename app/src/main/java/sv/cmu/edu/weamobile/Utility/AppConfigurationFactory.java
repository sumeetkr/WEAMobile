package sv.cmu.edu.weamobile.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import sv.cmu.edu.weamobile.Data.GeoLocation;

/**
 * Created by sumeet on 10/7/14.
 */
public class AppConfigurationFactory {
    public static void getConfigurationAsync(Context context){

        GPSTracker tracker = new GPSTracker(context);
        GeoLocation location = tracker.getNetworkGeoLocation();

        Logger.log("Sending lat " + location.getLatitude());
        Logger.log("Sending lng " + location.getLongitude());

        //fetch application configuration from server
        WEAHttpClient.sendHeartbeat(location.getJson(), context, Constants.URL_TO_GET_CONFIGURATION + WEAUtil.getIMSI(context));
    }

    public static String getStringProperty(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        String res = null;
        if (sharedPreferences != null) {
            res = sharedPreferences.getString(key, null);
        }
        return res;
    }

    public static void setStringProperty(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
            Logger.log("Set " + key + " property = " + value);
        }
    }
}
