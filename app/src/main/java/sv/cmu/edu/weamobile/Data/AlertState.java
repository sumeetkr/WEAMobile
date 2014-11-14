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
    private long timeWhenShownToUserInEpoch;
    private long timeWhenFeedbackGivenInEpoch;
    private GeoLocation locationWhenShown;
    private boolean isInPolygon = false;
    private State state = null;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        scheduled, shown, clicked
    }

    public long getTimeWhenFeedbackGivenInEpoch() {
        return timeWhenFeedbackGivenInEpoch;
    }

    public void setTimeWhenFeedbackGivenInEpoch(long timeWhenFeedbackGivenInEpoch) {
        this.timeWhenFeedbackGivenInEpoch = timeWhenFeedbackGivenInEpoch;
    }

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

    public long getTimeWhenShownToUserInEpoch() {
        return timeWhenShownToUserInEpoch;
    }

    public void setTimeWhenShownToUserInEpoch(long timeWhenShownToUserInEpoch) {
        this.timeWhenShownToUserInEpoch = timeWhenShownToUserInEpoch;
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
