package sv.cmu.edu.weamobile.Utility;

/**
 * Created by sumeet on 9/24/14.
 */
public class GPSTracker
//        extends Service implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
//        GooglePlayServicesClient.OnConnectionFailedListener
{

//    //make it a singleton
    private GPSTracker(){

    }

    public static GPSTracker getGPSTrackerInstance(){
        return new GPSTracker();
    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        last_known_location_is_last_changed = false;
//        this.location_from_updates=location;
//        Log.w("smsreceiver", "received a location update with lat: " + Double.valueOf(location.getLatitude()).toString() + "; lon: " + Double.valueOf(location.getLongitude()).toString());
//    }
//
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return null;
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onConnected(Bundle arg0) {
//        Log.w("WEA", "GPS Listener connected");
//        mLocationClient.requestLocationUpdates(mLocationRequest, this);
//        canGetLocation = true;
//
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onDisconnected() {
//        // TODO Auto-generated method stub
//
//    }

}
