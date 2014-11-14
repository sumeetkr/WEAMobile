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
    private int id;


    public GeoLocation(String latitude, String longitude){
        this.lat = latitude;
        this.lng = longitude;
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

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static GeoLocation fromJson(String s) {
        return new Gson().fromJson(s, GeoLocation.class);
    }

    public static GeoLocation getGeoLocationFromAndroidLocation(Location location){
        return new GeoLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
    }
}
