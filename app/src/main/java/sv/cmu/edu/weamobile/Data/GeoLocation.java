package sv.cmu.edu.weamobile.data;

import android.location.Location;

import com.google.gson.Gson;

import java.sql.Timestamp;

/**
 * Created by sumeet on 10/15/14.
 */
public class GeoLocation {
//            {
//                "lng": "-94.71",
//                    "lat": "29.34",
//                    "id": 1
//            },
    private String lat;
    private String lng;
    private float accuracy = 0.00f;
    private int id;
    private float batteryLevel =0.00f;
    private String packageVersion= "";
    private String additionalInfo="NA";
    private Timestamp timestamp;
    private String Activity="NA";
    private double ActivityConfidence = 0.0;

//    public GeoLocation(String latitude, String longitude){
//        this.lat = latitude;
//        this.lng = longitude;
//    }

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

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }

    public double getActivityConfidence() {
        return ActivityConfidence;
    }

    public void setActivityConfidence(double activityConfidence) {
        ActivityConfidence = activityConfidence;
    }
}
