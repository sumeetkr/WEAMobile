package sv.cmu.edu.weamobile.data;

import android.location.Location;

import com.google.gson.Gson;

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


//    public GeoLocation(String latitude, String longitude){
//        this.lat = latitude;
//        this.lng = longitude;
//    }

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
}
