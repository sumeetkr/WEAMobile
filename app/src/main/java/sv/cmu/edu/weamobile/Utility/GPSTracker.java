package sv.cmu.edu.weamobile.Utility;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import sv.cmu.edu.weamobile.Data.GeoLocation;

/**
 * Created by sumeet on 9/24/14.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;

        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // Getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // No network provider is enabled
            Logger.log("GPS or NETWORK provider location manager is not available");
        } else {
            this.canGetLocation = true;
        }

        updateLocation();
    }

    private Location updateLocation() {
        try {
            if (isNetworkEnabled) {
                location = getNetworkLocation();
            }
            // If GPS enabled, get latitude/longitude using GPS Services
            if (location == null && isGPSEnabled) {
                location = getGPSLocation();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getMessage());
        }

        return location;
    }

    public Location getGPSLocation() {
        Location loc = null;
        if(isGPSEnabled)
        {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Logger.log("Asking for GPS location");
            if (locationManager != null) {
                loc = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    location = loc;
                }
            }

            stopUsingGPS();
        }
        return loc;
    }

    public Location getNetworkLocation() {
        Location loc = null;
        if(isNetworkEnabled){
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Logger.log("Asking for network location");
            if (locationManager != null) {
                loc = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    location = loc;
                }
            }
        }
        return loc;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public GeoLocation getNetworkGeoLocation(){
        updateLocation();
        GeoLocation loc;
        if(location != null){
            loc = new GeoLocation(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));
        }else{
            loc = getGPSGeoLocation();
        }
        return loc;
    }

    public GeoLocation getGPSGeoLocation(){
        location = getGPSLocation();
        GeoLocation loc = null;
        if(location == null){
            updateLocation();
        }
        if(location != null){
            loc = new GeoLocation(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));
        }

        return loc;
    }

    @Override
    public void onLocationChanged(Location location) {
    }


    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
