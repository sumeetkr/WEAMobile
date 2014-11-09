package sv.cmu.edu.weamobile.data;

import com.google.gson.Gson;

/**
 * Created by sumeet on 11/7/14.
 */
public class AlertState {

    private int id;
    //these properties are set by phone
    private boolean isAlreadyShown = false;
    private boolean isFeedbackGiven = false;
    private long timeWhenShownInEpoch;
    private GeoLocation locationWhenShown;
    private boolean isInPolygon = false;

    public AlertState(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public boolean isAlreadyShown() {
        return isAlreadyShown;
    }

    public void setAlreadyShown(boolean isAlreadyShown) {
        this.isAlreadyShown = isAlreadyShown;
    }

    public long getTimeWhenShownInEpoch() {
        return timeWhenShownInEpoch;
    }

    public void setTimeWhenShownInEpoch(long timeWhenShownInEpoch) {
        this.timeWhenShownInEpoch = timeWhenShownInEpoch;
    }

    public GeoLocation getLocationWhenShown() {
        return locationWhenShown;
    }

    public void setLocationWhenShown(GeoLocation locationWhenShown) {
        this.locationWhenShown = locationWhenShown;
    }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static AlertState fromJson(String s) {
        return new Gson().fromJson(s, AlertState.class);
    }

    public boolean isFeedbackGiven() {
        return isFeedbackGiven;
    }

    public void setFeedbackGiven(boolean isFeedbackGiven) {
        this.isFeedbackGiven = isFeedbackGiven;
    }

    public boolean isInPolygon() {
        return isInPolygon;
    }

    public void setInPolygon(boolean isInPolygon) {
        this.isInPolygon = isInPolygon;
    }
}
