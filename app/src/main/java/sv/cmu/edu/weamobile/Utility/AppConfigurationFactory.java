package sv.cmu.edu.weamobile.Utility;

import android.content.Context;
import android.telephony.TelephonyManager;

import sv.cmu.edu.weamobile.Data.GeoLocation;

/**
 * Created by sumeet on 10/7/14.
 */
public class AppConfigurationFactory {
    public static void getConfigurationAsync(Context context){

        GPSTracker tracker = new GPSTracker(context);
        GeoLocation location = tracker.getGeoLocation();

        TelephonyManager telephoneMananger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephoneMananger.getSimSerialNumber();

        //fetch application configuration from server
        WEAHttpClient.sendHeartbeat(location.getJson(), context, Constants.URL_TO_GET_CONFIGURATION + imsi.substring(0,6));


        //if received, validate application configuration

        //Save application configuration

        //Load application configuration using shared preferences

        //create configuration object and return
    }
}
