package sv.cmu.edu.weamobile.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.AppConfiguration;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.views.MainActivity;

/**
 * Created by sumeet on 10/30/14.
 */
public class AlertHelper {

    public static SpannableString getTextWithStyle(String text, float fontSize, boolean isStriked){
        Spanned spannedText = Html.fromHtml(text);
        SpannableString spanString = new SpannableString(spannedText);

        if(isStriked){
            spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        }

        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan (new RelativeSizeSpan(fontSize), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static void showAlert( Context context,
                                   Alert alert,
                                   GeoLocation location,
                                   AppConfiguration configuration) {
        Logger.log("Its the alert time");
        WEAUtil.showMessageIfInDebugMode(context, "Checking if alert is active");

        if(alert.isActive()){
            AlertState state = WEASharedPreferences.getAlertState(context, alert);
            if(location != null){
                location.setBatteryLevel(WEAUtil.getBatteryLevel(context));
                state.setLocationWhenShown(location);
            }

            state.setInPolygonOrAlertNotGeoTargeted(true);
            WEASharedPreferences.saveAlertState(context, state);

            Intent dialogIntent = new Intent(context, MainActivity.class);
            dialogIntent.putExtra("item_id", String.valueOf(alert.getId()));
            dialogIntent.putExtra(Constants.CONFIG_JSON, configuration.getJson());
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            WEAUtil.showMessageIfInDebugMode(context, "Alert is active, asking main view to show alert");
            context.startActivity(dialogIntent);
        }else{
            WEAUtil.showMessageIfInDebugMode(context, "Alert not active, will not be shown");
        }
    }

    public static void showAlertIfInTargetOrIsNotGeotargeted(Context context, int alertId) {
        Logger.log("Show alert if in target for ", String.valueOf(alertId));
        String json = WEASharedPreferences.readApplicationConfiguration(context);
        AppConfiguration configuration = AppConfiguration.fromJson(json);
        Alert [] alerts = configuration.getAlerts(context);

        for(Alert alert: alerts){
            if(alert.getId() == alertId && alert.isActive() ){

                GPSTracker tracker = new GPSTracker(context);
                if(tracker.canGetLocation()){
                    if(alert.isGeoFiltering() ){
                        Logger.log("The phone can get location, will check if in target");
                        tracker.keepLookingForPresenceInPolygonAndShowAlertIfNecessary(context, alert, configuration);
                    }else{
                        String message = "Geo-filtering off, showing alert";
                        Logger.log(message);
                        WEAUtil.showMessageIfInDebugMode(context, message);
                        showAlert(context, alert, tracker.getNetworkGeoLocation(), configuration);
                    }
                }else{
                    Logger.log("Location not known");
                    String message ="GPS location not know, please enable GPS for Geo-filtering.";
                    Logger.log(message);
                    WEAUtil.showMessageIfInDebugMode(context, message);
                    showAlert(context, alert, null, configuration);
                }
            }
        }
    }

    public static Alert getAlertFromId(Context context, String id) {

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

    public static AlertState getAlertState(Context context, Alert alert) {
        return WEASharedPreferences.getAlertState(context, alert);
    }

    public static List<AlertState> getAlertStates(Context context, List<Alert> alerts){
        List<AlertState> alertStates = new ArrayList<AlertState>();

        for(int i=0; i<alerts.size();i++){
            AlertState state = WEASharedPreferences.getAlertState(context, alerts.get(i));
            if(state != null){
                alertStates.add(state);
            }
        }

        return  alertStates;
    }

    public static String getFeedbackURL(Context context, Alert alert){
        return Constants.FEEDBACK_URL_ROOT + alert.getId()+
                "/" +WEAUtil.getIMEI(context);
    }

    public static String getContextTextToShow(Alert alert, GeoLocation myLocation) {
        double distance = WEAPointInPoly.getDistance(alert.getPolygon(), myLocation);
        return "You are at a distance " + String.valueOf(distance).substring(0,3) + " miles";
    }

    public static void clearAlertStates(Context context, Alert [] alerts) {

        if(alerts!= null){
            for(int i=0; i<alerts.length;i++){
                WEASharedPreferences.deleteAlertState(context, alerts[i]);
            }
        }
    }
}
