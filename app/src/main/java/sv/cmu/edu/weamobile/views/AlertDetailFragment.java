package sv.cmu.edu.weamobile.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.GPSTracker;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEAPointInPoly;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEATextToSpeech;
import sv.cmu.edu.weamobile.utility.WEAUtil;
import sv.cmu.edu.weamobile.utility.WEAVibrator;
import sv.cmu.edu.weamobile.utility.db.LocationDataSource;


/**
 * A fragment representing a single Alert detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link AlertDetailActivity}
 * on handsets.
 */
public class AlertDetailFragment extends Fragment {
    private GoogleMap mMap;
    private Alert alert;
    private AlertState alertState;
    private String startTime;
    private String endTime;
    private View rootView;
    private GeoLocation myLocation;
    private WEATextToSpeech textToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                "Creating alert with a map");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alert_detail, container, false);

        return rootView;
    }

    private void setupView(){

        if (alert != null) {
            Logger.log("Item is there"+ alert.getText());
            TextView view = ((TextView) rootView.findViewById(R.id.alertText));

            String text = alert.toString();
//            if(!alertState.isAlreadyShown() && alert.isActive() && alertState.isInPolygonOrAlertNotGeoTargeted()){
//                text = alert.getAlertType() + " Alert : "+ text;
//            }
            view.setText(
                    AlertHelper.getTextWithStyle(text
                    , 1.7f, false));

            view.setMovementMethod(LinkMovementMethod.getInstance());

            startTime = alert.getScheduledForString();
            endTime = alert.getEndingAtString();

            ((TextView) rootView.findViewById(R.id.txtLabel)).setText(
                    AlertHelper.getTextWithStyle(startTime +  " to " +endTime,
                                    //+ "\n" + textToShow,
                            1f, false));

            getActivity().setTitle(AlertHelper.getTextWithStyle(alert.getAlertType() + " Alert", 1.3f, false));
            getActivity().getActionBar().setIcon(R.drawable.ic_launcher);

        }else{
            Logger.log("Item is null");
            WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                    "Alert was null, this should not happen");

        }


        if(!alertState.isFeedbackGiven()){
            LinearLayout buttonLayout = (LinearLayout) rootView.findViewById(R.id.alertDialogButtons);
            buttonLayout.setVisibility(View.VISIBLE);

            addEventListenersToButtons();
        }else{
            LinearLayout buttonLayout = (LinearLayout) rootView.findViewById(R.id.alertDialogButtons);
            buttonLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void updateMyLocation() {

        if(alertState.getLocationWhenShown()!= null) {
            myLocation = alertState.getLocationWhenShown();

        }else{
            GPSTracker tracker = new GPSTracker(this.getActivity().getApplicationContext());
            if(tracker.canGetLocation()){
                myLocation = tracker.getNetworkGeoLocation();
            }

            alertState.setLocationWhenShown(myLocation);
            WEASharedPreferences.saveAlertState(getActivity().getApplicationContext(), alertState);
        }

        if(myLocation != null) Logger.log("my location: " + myLocation.toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().containsKey(Constants.ARG_ITEM_ID)) {
            alert = AlertHelper.getAlertFromId(
                    getActivity().getApplicationContext(),
                    getArguments().getString(Constants.ARG_ITEM_ID));

            alertState = WEASharedPreferences.getAlertState(getActivity().getApplicationContext(),
                    alert);
        }

        updateMyLocation();
        setupView();
        setUpMapIfNeeded();
        alertUserWithVibrationAndSpeech();
    }

    @Override
    public void onDestroy() {
        shutdownSpeech();
        super.onDestroy();
    }

    private void addEventListenersToButtons() {
//        Button close_button = (Button) rootView.findViewById(R.id.buttonOk);
//        if(close_button != null) close_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(textToSpeech!=null) textToSpeech.shutdown();
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//            }
//        });

        Button btnFeedback = (Button) rootView.findViewById(R.id.buttonFeedback);
        if(btnFeedback != null){
            final Activity activity = this.getActivity();
            btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shutdownSpeech();

                    Toast.makeText(getActivity(), Constants.SHOWING_FEEDBACK_FORM, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(activity, FeedbackWebViewActivity.class);
                    intent.putExtra(Constants.ALERT_ID, alert.getId());
                    startActivity(intent);
                }
            });
        }
    }

    private void alertUserWithVibrationAndSpeech() {

        if(alertState!= null && !alertState.isAlreadyShown()){

            WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                    "Alert is shown for first time, may vibrate and speak");

            alertState.setAlreadyShown(true);
            alertState.setTimeWhenShownToUserInEpoch(System.currentTimeMillis());
            alertState.setState(AlertState.State.shown);
            WEASharedPreferences.saveAlertState(getActivity().getApplicationContext(), alertState);

            WEAHttpClient.sendAlertState(getActivity().getApplicationContext(),
                    alertState.getJson(),
                    String.valueOf(alertState.getId()));


            if (alert != null && alert.isPhoneExpectedToVibrate()) {
                WEAVibrator.vibrate(getActivity().getApplicationContext());
                WEAUtil.lightUpScreen(getActivity().getApplicationContext());
            }

            if(alert != null && alert.isTextToSpeechExpected()){
                String messageToSay = AlertHelper.getTextWithStyle(alert.getText(), 1f, false).toString();
//                        + AlertHelper.getContextTextToShow(alert, myLocation);
                textToSpeech = new WEATextToSpeech(getActivity());
                textToSpeech.say(messageToSay, 2);
            }

        }else{
            WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                    "Alert was shown before, will not vibrate and speak");

        }
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (alert != null && alert.isMapToBeShown()) {
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                try{
                    final Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
                    mMap = ((SupportMapFragment) fragment).getMap();
                    // Check if we were successful in obtaining the map.
                    if (mMap != null) {
                        setUpMap();

                        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                            @Override
                            public void onCameraChange(CameraPosition arg0) {
                                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (GeoLocation location : alert.getPolygon()) {
                                    builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                                }

                                if(alertState != null && alertState.getLocationWhenShown() != null){
                                    builder.include(new LatLng(alertState.getLocationWhenShown().getLatitude(),
                                            alertState.getLocationWhenShown().getLongitude()));
                                }

                                LatLngBounds bounds = builder.build();
                                // Move camera.
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100), 1000, new GoogleMap.CancelableCallback() {
                                    @Override
                                    public void onFinish() {
                                        Context ctxt = fragment.getActivity().getApplicationContext();

                                        if(WEASharedPreferences.isLocationHistoryEnabled(ctxt)){
                                            LocationDataSource dataSource = new LocationDataSource(ctxt);

                                            double totalAccuracy = 0.0;
                                            double avgAccuracy =100.0;
                                            List<GeoLocation> rawLocations = dataSource.getAllData();
                                            for(GeoLocation location :rawLocations){
                                                totalAccuracy += location.getAccuracy();
                                            }
                                            if(rawLocations.size()>1){
                                                avgAccuracy = totalAccuracy/rawLocations.size();
                                            }

                                            List<LatLng> historyPoints = new ArrayList<LatLng>();
                                            for(GeoLocation location :rawLocations){
                                                // Remove out liars
                                                if(location.getAccuracy()<2*avgAccuracy){
                                                    historyPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                                                }
                                            }

                                            //old points should be in a different color
                                            if(historyPoints.size()>3){
                                                List<LatLng> oldPoints = new ArrayList<LatLng>();
                                                for(int i =0; i< historyPoints.size()-3; i++){
                                                    oldPoints.add(historyPoints.get(i));
                                                }

                                                mMap.addPolygon(new PolygonOptions()
                                                        .addAll(oldPoints)
                                                        .strokeColor(Color.BLUE)
                                                        .strokeWidth(2));

                                                //newer points should be in a different color
                                                List<LatLng> newPoints = new ArrayList<LatLng>();
                                                for(int i = historyPoints.size()-3; i<historyPoints.size(); i++){
                                                    newPoints.add(historyPoints.get(i));
                                                }

                                                mMap.addPolygon(new PolygonOptions()
                                                        .addAll(newPoints)
                                                        .strokeColor(Color.MAGENTA));

                                                WEAUtil.showMessageIfInDebugMode(ctxt, "No of history points in database : "+ historyPoints.size());
                                                Logger.log("Adding history points on the map,  count of points: "+ historyPoints.size());

                                                // Try to get direction and speed based on previous points

                                            }else{
                                                WEAUtil.showMessageIfInDebugMode(ctxt, "No of history points in database : "+ historyPoints.size());
                                                Logger.log("Adding 000 i.e. zero history points on the map, no points in database");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                                // Remove listener to prevent position reset on camera move.
                                mMap.setOnCameraChangeListener(null);
                            }
                        });
                    }
                }catch(Exception ex){
                    Logger.log(ex.getMessage());
                }
            }
        }
    }

    private void setUpMap() {

        WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                "Setting up map for this alert.");

        mMap.clear();
//        mMap.setMyLocationEnabled(true);

        addUserLocationWhenAlertWasFirstShown();
        drawPolygon();
    }

    private void addUserLocationWhenAlertWasFirstShown() {
        if(myLocation != null){

            if(alertState != null && alertState.getLocationWhenShown() != null){
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(alertState.getLocationWhenShown().getLatitude(),
                                alertState.getLocationWhenShown().getLongitude()))
                        .title("Your location"));
            }else{
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .title("Your location"));
            }
        }
    }

    private void drawPolygon(){
        try{
            if(mMap!=null){
                PolygonOptions polyOptions = new PolygonOptions()
                        .strokeColor(Color.RED);

                GeoLocation[] locations = alert.getPolygon();

                if(locations != null & locations.length>2){
                    for(GeoLocation location:locations){
                        polyOptions.add(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng())));
                    }

                    mMap.addPolygon(polyOptions);
                    setCenter();
                }
            }
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    private void setCenter(){

        try{
            if(alert.getPolygon() != null){

                double [] centerLocation = WEAPointInPoly.calculatePolyCenter(alert.getPolygon());

                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(centerLocation[0], centerLocation[1]));

                mMap.moveCamera(center);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }else{
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(17);
                mMap.animateCamera(zoom);
            }

        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    public void shutdownSpeech() {
        try{
            if(textToSpeech != null) textToSpeech.shutdown();
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }
}
