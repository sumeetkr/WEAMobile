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
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.data.MessageState;
import sv.cmu.edu.weamobile.utility.db.LocationDataSource;
import sv.cmu.edu.weamobile.utility.db.MessageDataSource;
import sv.cmu.edu.weamobile.utility.db.MessageStateDataSource;
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
                                   Message message,
                                   GeoLocation location,
                                    String messageWhyShowingAlert) {
        Logger.log("Its the message time");
        WEAUtil.showMessageIfInDebugMode(context, "Checking if message is active");

        if(message.isActive()){
            MessageStateDataSource messageStateDataSource = new MessageStateDataSource(context);

            MessageState state = messageStateDataSource.getData(message.getId());
            if(location != null){
                location.setBatteryLevel(WEAUtil.getBatteryLevel(context));
                state.setLocationWhenShown(location);
                location.setAdditionalInfo(location.getAdditionalInfo()+ messageWhyShowingAlert);
            }

            state.setInPolygonOrAlertNotGeoTargeted(true);

            MessageStateDataSource dataSource = new MessageStateDataSource(context);
            dataSource.updateData(state);

            Intent dialogIntent = new Intent(context, MainActivity.class);
            dialogIntent.putExtra(Constants.ALERT_ID, String.valueOf(message.getId()));
//            dialogIntent.putExtra(Constants.CONFIG_JSON, configuration.getJson());
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            WEAUtil.showMessageIfInDebugMode(context, "Alert is active, asking main view to show message");
            context.startActivity(dialogIntent);
        }else{
            WEAUtil.showMessageIfInDebugMode(context, "Alert not active, will not be shown");
        }
    }

    public static void showAlertIfInTargetOrIsNotGeotargeted(Context context, int alertId) {
        Logger.log("Show message if in target for ", String.valueOf(alertId));

        MessageDataSource messageDataSource = new MessageDataSource(context);
        Message message = messageDataSource.getData(alertId);

        GPSTracker tracker = new GPSTracker(context);
        if(tracker.canGetLocation()){
            if(message.getParameter().isGeoFiltering()){
                Logger.log("The phone can get location, will check if in target");
                //first check location history
                //The code here needs to be refactored for future extension
                LocationDataSource dataSource = new LocationDataSource(context);
                List<GeoLocation> locations = dataSource.getAllData();
                //check history of user locations
                //Also predict his future locations
                if(message.getParameter().isHistoryBasedFiltering() && WEALocationHelper.areAnyPointsInPolygon(locations, message.getPolygon())){
                    String messageToShow = "User's location history was found in the message region, showing message";
                    Logger.log(messageToShow);
                    WEAUtil.showMessageIfInDebugMode(context, messageToShow);
                    showAlert(context, message, tracker.getNetworkGeoLocation(), messageToShow);

                }else if(message.getParameter().isMotionPredictionBasedFiltering() && WEALocationHelper.areAnyPointsInPolygon2(
                        WEALocationHelper.getFuturePredictionsOfLatLngs(locations)
                        , message.getPolygon())){
                    String messageToShow = "User's predicted location was found in the message region, showing message";
                    Logger.log(messageToShow);
                    WEAUtil.showMessageIfInDebugMode(context, messageToShow);
                    showAlert(context, message, tracker.getNetworkGeoLocation(), messageToShow);
                }else{
                    tracker.keepLookingForPresenceInPolygonAndShowAlertIfNecessary(context, message);
                }
            }else{
                String messageToShow = "Geo-filtering is off, showing message";
                Logger.log(messageToShow);
                WEAUtil.showMessageIfInDebugMode(context, messageToShow);
                showAlert(context, message, tracker.getNetworkGeoLocation(), messageToShow);
            }
        }else{
            Logger.log("Location not known");
            String messageToShow ="GPS location not know, please enable GPS for Geo-filtering, showing message";
            Logger.log(messageToShow);
            WEAUtil.showMessageIfInDebugMode(context, messageToShow);
            showAlert(context, message, null, messageToShow);
        }
    }

    public static Message getMessageFromId(Context context, String id) {

        int intId = Integer.valueOf(id);

        MessageDataSource dataSource = new MessageDataSource(context);
        return dataSource.getData(intId);
    }

    public static MessageState getAlertState(Context context, Message message) {
        MessageStateDataSource messageStateDataSource = new MessageStateDataSource(context);
        MessageState state = messageStateDataSource.getData(message.getId());

        return state;
    }

    public static List<MessageState> getAlertStates(Context context, List<Message> messages){
        List<MessageState> messageStates = new ArrayList<MessageState>();

        MessageStateDataSource messageStateDataSource = new MessageStateDataSource(context);
        for(int i=0; i<messages.size();i++){
            MessageState state = messageStateDataSource.getData(messages.get(i).getId());
            if(state != null){
                messageStates.add(state);
            }
        }

        return messageStates;
    }

    public static String getFeedbackURL(Context context, Message message){
        return Constants.FEEDBACK_URL_ROOT + message.getId()+
                "/" +WEAUtil.getIMEI(context);
    }

    public static String getContextTextToShow(Message message, GeoLocation myLocation) {
        double distance = WEALocationHelper.getDistance(message.getPolygon(), myLocation);
        return "You are at a distance " + String.valueOf(distance).substring(0,3) + " miles";
    }

    public static void updateMessageState(MessageState messageState, Context context) {
        MessageStateDataSource dataSource = new MessageStateDataSource(context);
        dataSource.updateData(messageState);
    }

    public static List<Message> getAllMessage(Context applicationContext) {
        MessageDataSource dataSource = new MessageDataSource(applicationContext);
        return  dataSource.getAllData();
    }

    public static List<MessageState> getAllAlertStates(Context applicationContext) {
        MessageStateDataSource dataSource = new MessageStateDataSource(applicationContext);
        return dataSource.getAllData();
    }
}
