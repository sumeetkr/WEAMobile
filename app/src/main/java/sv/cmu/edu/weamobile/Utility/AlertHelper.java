package sv.cmu.edu.weamobile.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Toast;

import sv.cmu.edu.weamobile.AlertDetailActivity;
import sv.cmu.edu.weamobile.AlertDetailFragment;
import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.service.WEANewAlertIntent;

/**
 * Created by sumeet on 10/30/14.
 */
public class AlertHelper {

    public static double getDistanceFromCentroid(Location myLocation, double[] polyCenter) {
        float center = 0000;
        try{
            float [] results= new float[2];
            Location.distanceBetween(polyCenter[0], polyCenter[1], myLocation.getLatitude(), myLocation.getLongitude(), results);
            center= results[0]/1000;
        }catch(Exception ex){

        }
        return center;
    }

    public static SpannableString getTextWithStyle(String text, int fontSize){
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan (new AbsoluteSizeSpan(fontSize), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static void broadcastNewAlert(Context context, Alert alert, AppConfiguration configuration){
        showAlert(context, alert, configuration);
    }

    private static void showAlert( Context context, Alert alert, AppConfiguration configuration) {
        Logger.log("Its the alert time");
        int alertFilter = alert.getOptions();
        Intent dialogIntent;
        dialogIntent = new Intent(context, AlertDetailActivity.class);
        // ToDo need to enable later
        //        if(alertFilter == 1){
        //            dialogIntent = new Intent(getBaseContext(), AlertDetailActivity.class);
        //            Logger.log("Showing alert with map");
        //        }else
        //        {
        //            dialogIntent = new Intent(getBaseContext(), PlainAlertDialogActivity.class);
        //            Logger.log("Showing alert without map");
        //            dialogIntent.putExtra("Message", alert.getText());
        //        }

        //to be used when feedback button is clicked
        AppConfigurationFactory.setStringProperty(
                context,
                "feedback_url",
                Constants.FEEDBACK_URL_ROOT + alert.getId()+
                        "/" +WEAUtil.getIMSI(context));

        dialogIntent.putExtra("item_id", String.valueOf(alert.getId()));
        dialogIntent.putExtra("isDialog", true);
        dialogIntent.putExtra(AlertDetailFragment.ALERTS_JSON, configuration.getJson());
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);
    }

    public static void broadcastOutOfTargetAlert(Context context){

        WEANewAlertIntent outOfTargetAlertIntent = new WEANewAlertIntent("New Alert, but out of target", "");
        Log.d("WEA", "Broadcast intent: About to broadcast new Alert");
        context.sendBroadcast(outOfTargetAlertIntent);
    }

    public static void showAlertIfInTarget(Context context, int alertId) {
        Logger.log("Show alert if in target for ", String.valueOf(alertId));
        String json = AppConfigurationFactory.getStringProperty(context, "message");
        AppConfiguration configuration = AppConfiguration.fromJson(json);
        Alert [] alerts = configuration.getAlerts();

        for(Alert alert: alerts){
            if(alert.getId() == alertId){
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
                    broadcastNewAlert(context, alert, configuration);
                }
            }
        }
    }
}
