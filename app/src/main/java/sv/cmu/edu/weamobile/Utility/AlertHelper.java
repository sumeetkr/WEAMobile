package sv.cmu.edu.weamobile.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.widget.Toast;

import sv.cmu.edu.weamobile.Activities.MainActivity;
import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AlertState;
import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.Data.GeoLocation;

/**
 * Created by sumeet on 10/30/14.
 */
public class AlertHelper {

    public static SpannableString getTextWithStyle(String text, int fontSize){
        Spanned spannedText = Html.fromHtml(text);
        SpannableString spanString = new SpannableString(spannedText);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan (new AbsoluteSizeSpan(fontSize), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static void showAlert( Context context,
                                   Alert alert,
                                   GeoLocation location,
                                   AppConfiguration configuration) {
        Logger.log("Its the alert time");

        if(alert.isActive()){
            AlertState state = WEASharedPreferences.getAlertState(context, String.valueOf(alert.getId()));
            state.setInPolygon(true);
            state.setLocationWhenShown(location);
            WEASharedPreferences.saveAlertState(context, state);

            Intent dialogIntent = new Intent(context, MainActivity.class);
            dialogIntent.putExtra("item_id", String.valueOf(alert.getId()));
            dialogIntent.putExtra(Constants.CONFIG_JSON, configuration.getJson());
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(dialogIntent);
        }
    }

    public static void showAlertIfInTarget(Context context, int alertId) {
        Logger.log("Show alert if in target for ", String.valueOf(alertId));
        String json = WEASharedPreferences.readApplicationConfiguration(context);
        AppConfiguration configuration = AppConfiguration.fromJson(json);
        Alert [] alerts = configuration.getAlerts(context);

        for(Alert alert: alerts){
            if(alert.getId() == alertId && alert.isActive()){
                GPSTracker tracker = new GPSTracker(context);
                if(tracker.canGetLocation()){
                    Logger.log("The phone can get location, will check if in target");
                    tracker.keepLookingForPresenceInPolygonAndShowAlertIfNecessary(context, alert, configuration);
                }else{
                    Logger.log("Location not known");
                    String message ="Location not know, Geo-filtering failed.";
                    Logger.log(message);
                    Toast.makeText(context,
                            "Alert Time!!: " + message, Toast.LENGTH_SHORT).show();
                    showAlert(context, alert, null, configuration);
                }
            }
        }
    }

    public static Alert getAlertFromId(Context context, String id) {

//        return WEASharedPreferences.getAlertState(context,id);


        AppConfiguration configuration = AppConfiguration.fromJson(WEASharedPreferences.readApplicationConfiguration(context));
        Logger.log("AlertDetailFragment key: " + id);

        Alert selectedAlert = null;
        for(Alert alert:configuration.getAlerts(context)){
            if(alert.getId() == Integer.parseInt(id)){
                selectedAlert = alert;
            }
        }
        return selectedAlert;
    }

    public static AlertState [] getAlertStates(Context context, Alert [] alerts){
        AlertState [] alertStates = new AlertState[alerts.length];

        for(int i=0; i<alerts.length;i++){
            alertStates[i] = WEASharedPreferences.getAlertState(context, String.valueOf(alerts[i].getId()));
        }

        return  alertStates;
    }

    public static String getFedbackURL( Context context, Alert alert){
        return Constants.FEEDBACK_URL_ROOT + alert.getId()+
                "/" +WEAUtil.getIMSI(context);
    }

    public static String getContextTextToShow(Alert alert, GeoLocation myLocation) {
        double distance = WEAPointInPoly.getDistance(alert.getPolygon(), myLocation);
        return "You are at a distance " + String.valueOf(distance).substring(0,3) + " miles";
    }

    public static void clearAlertStates(Context context, Alert [] alerts) {

        if(alerts!= null){
            for(int i=0; i<alerts.length;i++){
                WEASharedPreferences.deleteAlertState(context, String.valueOf(alerts[i].getId()));
            }
        }
    }
}
