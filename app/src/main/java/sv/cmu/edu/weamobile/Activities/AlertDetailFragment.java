package sv.cmu.edu.weamobile.Activities;

import android.app.Activity;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AlertState;
import sv.cmu.edu.weamobile.Data.GeoLocation;
import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.Utility.AlertHelper;
import sv.cmu.edu.weamobile.Utility.Constants;
import sv.cmu.edu.weamobile.Utility.GPSTracker;
import sv.cmu.edu.weamobile.Utility.Logger;
import sv.cmu.edu.weamobile.Utility.WEAPointInPoly;
import sv.cmu.edu.weamobile.Utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.Utility.WEATextToSpeech;
import sv.cmu.edu.weamobile.Utility.WEAVibrator;


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

        if (getArguments().containsKey(Constants.ARG_ITEM_ID)) {
            alert = AlertHelper.getAlertFromId(
                    getActivity().getApplicationContext(),
                    getArguments().getString(Constants.ARG_ITEM_ID));

            alertState = WEASharedPreferences.getAlertState(getActivity().getApplicationContext(),
                    getArguments().getString(Constants.ARG_ITEM_ID));
        }

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

            view.setText(
                    AlertHelper.getTextWithStyle(
                    alert.toString(), 40));

            view.setMovementMethod(LinkMovementMethod.getInstance());

            startTime = alert.getScheduledForString();
            endTime = alert.getEndingAtString();

            String textToShow = AlertHelper.getContextTextToShow(alert,myLocation);
            ((TextView) rootView.findViewById(R.id.txtLabel)).setText(
                    AlertHelper.getTextWithStyle("Schedule: " + startTime +" to " +endTime + "\n" + textToShow,
                            25));

            getActivity().setTitle("CMU WEA+ " + alert.getAlertType() + " Alert");
        }else{
            Logger.log("Item is null");
        }


        if(! alertState.isFeedbackGiven()){
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
        updateMyLocation();
        setupView();
        setUpMapIfNeeded();
        alertUserWithVibrationAndSpeech();
    }

    @Override
    public void onDestroy() {
        if(textToSpeech!=null) textToSpeech.shutdown();
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
                    if(textToSpeech!=null) textToSpeech.shutdown();

                    alertState.setFeedbackGiven(true);
                    WEASharedPreferences.saveAlertState(getActivity().getApplicationContext(), alertState);

                    Intent intent = new Intent(activity, FeedbackWebViewActivity.class);
                    intent.putExtra(Constants.ALERT_ID, alert.getId());
                    startActivity(intent);
                }
            });
        }
    }

    private void alertUserWithVibrationAndSpeech() {

        if(alertState!= null && !alertState.isAlreadyShown() && alert.isActive()){

            alertState.setAlreadyShown(true);
            alertState.setTimeWhenShownInEpoch(System.currentTimeMillis());
            WEASharedPreferences.saveAlertState(getActivity().getApplicationContext(), alertState);

            if (alert != null && alert.isPhoneExpectedToVibrate()) {
                WEAVibrator.vibrate(getActivity().getApplicationContext());
            }

            if(alert != null && alert.isTextToSpeechExpected()){
                String messageToSay = AlertHelper.getTextWithStyle(alert.getText(), 33).toString()
                        + AlertHelper.getContextTextToShow(alert, myLocation);;
                textToSpeech = new WEATextToSpeech(getActivity());
                textToSpeech.say(messageToSay, 2);
            }

        }
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (alert != null && alert.isMapToBeShown()) {
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                try{
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
                    mMap = ((SupportMapFragment) fragment).getMap();
                    // Check if we were successful in obtaining the map.
                    if (mMap != null) {
                        setUpMap();
                    }
                }catch(Exception ex){
                    Logger.log(ex.getMessage());
                }
            }
        }
    }

    private void setUpMap() {
        mMap.clear();
        mMap.setMyLocationEnabled(true);

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
        drawPolygon();
    }

    private void drawPolygon(){
        if(mMap!=null){
            PolygonOptions polyOptions = new PolygonOptions()
                    .strokeColor(Color.RED);

            if(alert!=null){
                GeoLocation[] locations = alert.getPolygon();
                for(GeoLocation location:locations){
                    polyOptions.add(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng())));
                }
            }


            mMap.addPolygon(polyOptions);
            setCenter();
        }
    }

    private void setCenter(){
        double [] location = WEAPointInPoly.calculatePolyCenter(alert.getPolygon());

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(location[0],location[1]));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(17);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

}
