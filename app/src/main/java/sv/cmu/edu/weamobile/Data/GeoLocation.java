package sv.cmu.edu.weamobile.data;

import android.location.Location;

import com.google.gson.Gson;

import java.sql.Timestamp;

/**
 * Created by sumeet on 10/15/14.
 */
public class GeoLocation {
    private String lat;
    private String lng;
    private float accuracy = 0.00f;
    private int id;
    private float batteryLevel =0.00f;
    private String packageVersion= "";
    private String additionalInfo="NA";
    private Timestamp timestamp;
    private int primaryActivityType =-1;
    private int activityConfidence = 0;
    private int lastReceivedMessageId =0;
    private int secondaryActivityType= -1;
    private int secondaryActivityConfidence = 0;

//    public GeoLocation(String latitude, String longitude){
//        this.lat = latitude;
//        this.lng = longitude;
//    }

    public GeoLocation(String latitude, String longitude, float accuracy,
                       int primaryActivityType, int primaryActivityConfidence,
                       int secondaryActivityType, int secondaryActivityConfidence,
                       Timestamp timestamp){

        this.lat = latitude;
        this.lng = longitude;
        this.accuracy = accuracy;
        setActivityType(primaryActivityType);
        setActivityConfidence(primaryActivityConfidence);
        setSecondaryActivity(secondaryActivityType);
        setActivityConfidence(secondaryActivityConfidence);
        setTimestamp(timestamp);

    }

    public GeoLocation(String latitude, String longitude, float accuracy, Timestamp timestamp){
        this.lat = latitude;
        this.lng = longitude;
        this.accuracy = accuracy;
        setTimestamp(timestamp);
    }

    public GeoLocation(String latitude, String longitude, float accuracy){
        this.lat = latitude;
        this.lng = longitude;
        this.accuracy = accuracy;
    }

    public GeoLocation(String latitude, String longitude, int id){
        this.lat = latitude;
        this.lng = longitude;
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public double getLatitude() {
        return Double.parseDouble(lat);
    }

    public double getLongitude() {
        return Double.parseDouble(lng);
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }


    public int getId() { return id; }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static GeoLocation fromJson(String s) {
        return new Gson().fromJson(s, GeoLocation.class);
    }

    public static GeoLocation getGeoLocationFromAndroidLocation(Location location){
        return new GeoLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),location.getAccuracy());
    }

    public float getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getActivityType() {
        return primaryActivityType;
    }

    public void setActivityType(int activityType) {
        this.primaryActivityType = activityType;
    }

    public int getActivityConfidence() {
        return activityConfidence;
    }

    public void setActivityConfidence(int activityConfidence) {
        this.activityConfidence = activityConfidence;
    }

    public int getSecondaryActivityType() {
        return secondaryActivityType;
    }

    public void setSecondaryActivity(int secondaryActivityType) {
        this.secondaryActivityType = secondaryActivityType;
    }

    public int getSecondaryActivityConfidence() {
        return secondaryActivityConfidence;
    }

    public void setSecondaryActivityConfidence(int secondaryActivityConfidence) {
        this.secondaryActivityConfidence = secondaryActivityConfidence;
    }

    public int getLastReceivedMessageId() {
        return lastReceivedMessageId;
    }

    public void setLastReceivedMessageId(int lastReceivedMessageId) {
        this.lastReceivedMessageId = lastReceivedMessageId;
    }
}
