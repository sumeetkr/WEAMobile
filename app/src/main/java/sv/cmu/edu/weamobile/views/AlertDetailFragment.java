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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.data.MessageState;
import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.GPSTracker;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEALocationHelper;
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
    private Message message;
    private MessageState messageState;
    private String startTime;
    private String endTime;
    private View rootView;
    private GeoLocation myLocation;
    private WEATextToSpeech textToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                "Creating message with a map");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alert_detail, container, false);

        return rootView;
    }

    private void setupView(){

        if (message != null) {
            Logger.log("Message to display : " + message.getText());
            TextView view = ((TextView) rootView.findViewById(R.id.alertText));

            String text = message.getText();
//            if(!alertState.isAlreadyShown() && message.isActive() && alertState.isInPolygonOrAlertNotGeoTargeted()){
//                text = message.getAlertType() + " Alert : "+ text;
//            }
            view.setText(
                    AlertHelper.getTextWithStyle(text
                    , 1.7f, false));

            view.setMovementMethod(LinkMovementMethod.getInstance());

            startTime = message.getEndingAtInLocalTime();
            endTime = message.getEndingAtInLocalTime();

            ((TextView) rootView.findViewById(R.id.txtLabel)).setText(
                    AlertHelper.getTextWithStyle("From : " + startTime +  " To : " +endTime,
                                    //+ "\n" + textToShow,
                            1f, false));

            getActivity().setTitle(AlertHelper.getTextWithStyle(
                    message.getAlertType() + " Alert", 1.3f, false));
            getActivity().getActionBar().setIcon(R.drawable.ic_launcher);

        }else{
            Logger.log("Item is null");
            WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                    "Alert was null, this should not happen");

        }


        if(!messageState.isFeedbackGiven()){
            LinearLayout buttonLayout = (LinearLayout) rootView.findViewById(R.id.alertDialogButtons);
            buttonLayout.setVisibility(View.VISIBLE);

            addEventListenersToButtons();
        }else{
            LinearLayout buttonLayout = (LinearLayout) rootView.findViewById(R.id.alertDialogButtons);
            buttonLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void updateMyLocation() {

        if(messageState.getLocationWhenShown()!= null) {
            myLocation = messageState.getLocationWhenShown();

        }else{
            GPSTracker tracker = new GPSTracker(this.getActivity().getApplicationContext());
            if(tracker.canGetLocation()){
                myLocation = tracker.getNetworkGeoLocation();
            }

            messageState.setLocationWhenShown(myLocation);
            AlertHelper.updateMessageState(messageState, this.getActivity());
        }

        if(myLocation != null) Logger.log("my location: " + myLocation.toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().containsKey(Constants.ARG_ITEM_ID)) {
            message = AlertHelper.getMessageFromId(
                    getActivity().getApplicationContext(),
                    getArguments().getString(Constants.ARG_ITEM_ID));

            messageState = AlertHelper.getAlertState(getActivity().getApplicationContext(),
                    message);
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
//                Intent intent = new Intent(getActivityType(), MainActivity.class);
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
                    intent.putExtra(Constants.ALERT_ID, message.getId());
                    startActivity(intent);
                }
            });
        }
    }

    private void alertUserWithVibrationAndSpeech() {

        if(message.isActive() && messageState != null && !messageState.isAlreadyShown()){

            WEAUtil.showMessageIfInDebugMode(getActivity().getApplicationContext(),
                    "Alert is shown for first time, may vibrate and speak");

            messageState.setAlreadyShown(true);
            messageState.setTimeWhenShownToUserInEpoch(System.currentTimeMillis());
            messageState.setState(MessageState.State.shown);

            AlertHelper.updateMessageState(messageState, getActivity().getApplicationContext());

            WEAHttpClient.sendAlertState(getActivity().getApplicationContext(),
                    messageState.getJson(),
                    String.valueOf(messageState.getId()));


            if (message != null && message.isPhoneExpectedToVibrate()) {
                WEAVibrator.vibrate(getActivity().getApplicationContext());
                WEAUtil.lightUpScreen(getActivity().getApplicationContext());
            }

            if(message != null && message.isTextToSpeechExpected()){
                String messageToSay = AlertHelper.getTextWithStyle(message.getText(), 1f, false).toString();
//                        + AlertHelper.getContextTextToShow(message, myLocation);
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
        if (message != null && message.isMapToBeShown()) {
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
                                for (GeoLocation location : message.getPolygon()) {
                                    builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                                }

                                if(messageState != null && messageState.getLocationWhenShown() != null){
                                    builder.include(new LatLng(messageState.getLocationWhenShown().getLatitude(),
                                            messageState.getLocationWhenShown().getLongitude()));
                                }

                                LatLngBounds bounds = builder.build();
                                // Move camera.
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100), 1000, new GoogleMap.CancelableCallback() {
                                    @Override
                                    public void onFinish() {
                                        Context ctxt = fragment.getActivity().getApplicationContext();

                                        boolean activityHistoryEnabled = WEASharedPreferences.isActivityHistoryEnabled(ctxt);

                                        if(WEASharedPreferences.isLocationHistoryEnabled(ctxt)
                                                || WEASharedPreferences.isMotionPredictionEnabled(ctxt)
                                                || activityHistoryEnabled){

                                            LocationDataSource dataSource = new LocationDataSource(ctxt);

                                            List<GeoLocation> historyPoints = dataSource.getAllData();

                                            // Should be >= 3
                                            int newPointsCount = 6;
                                            if(historyPoints.size()> newPointsCount){
                                                List<LatLng> oldPoints = new ArrayList<LatLng>();
                                                for(int i =0; i< historyPoints.size()- newPointsCount; i++){
                                                    LatLng latLng = new LatLng(historyPoints.get(i).getLatitude(), historyPoints.get(i).getLongitude());
                                                    oldPoints.add(latLng);

                                                    if(activityHistoryEnabled){
                                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                                .position(latLng)
                                                                .title(UserActivity.getFriendlyName(historyPoints.get(i).getActivityType()))
                                                                .icon(UserActivity.getBitmap(historyPoints.get(i).getActivityType()
                                                                        , historyPoints.get(i).getSecondaryActivityType())));
                                                    }
                                                }

                                                if(WEASharedPreferences.isLocationHistoryEnabled(ctxt)){
                                                    mMap.addPolygon(new PolygonOptions()
                                                            .addAll(oldPoints)
                                                            .strokeColor(Color.BLUE)
                                                            .strokeWidth(2));
                                                }

                                                //newer points should be in a different color
                                                List<LatLng> newPoints = new ArrayList<LatLng>();
                                                for(int i = historyPoints.size()-newPointsCount; i<historyPoints.size(); i++){
                                                    LatLng latLng = new LatLng(historyPoints.get(i).getLatitude(), historyPoints.get(i).getLongitude());
                                                    newPoints.add(latLng);

                                                    Logger.log(historyPoints.get(i).getLatitude() +
                                                            ", " + historyPoints.get(i).getLongitude());

                                                    if(activityHistoryEnabled){
                                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                                .position(latLng)
                                                                .title(UserActivity.getFriendlyName(historyPoints.get(i).getActivityType()))
                                                                .icon(UserActivity.getBitmap(historyPoints.get(i).getActivityType()
                                                                        , historyPoints.get(i).getSecondaryActivityType())));
                                                    }
                                                }


                                                mMap.addPolygon(new PolygonOptions()
                                                        .addAll(newPoints)
                                                        .strokeColor(Color.YELLOW));

                                                if(WEASharedPreferences.isMotionPredictionEnabled(ctxt)){
                                                    List<LatLng> futurePoints = WEALocationHelper.getFuturePredictionsOfLatLngs(historyPoints);

                                                    if(futurePoints.size()>0){
                                                        mMap.addPolygon(new PolygonOptions()
                                                                .addAll(futurePoints)
                                                                .strokeColor(Color.GREEN));

                                                        Logger.log("Added future points on the map, count:" + futurePoints.size());
                                                    }else{
                                                        Toast.makeText(getActivity(), "Looks like you are still, so no future locations", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                WEAUtil.showMessageIfInDebugMode(ctxt, "No of history points in database : " + historyPoints.size());
                                                Logger.log("Adding history points on the map,  count of points: "+ historyPoints.size());

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
                "Setting up map for this message.");

        mMap.clear();
//        mMap.setMyLocationEnabled(true);

        addUserLocationWhenAlertWasFirstShown();
        drawPolygon();
    }

    private void addUserLocationWhenAlertWasFirstShown() {
        if(myLocation != null){

            if(messageState != null && messageState.getLocationWhenShown() != null){
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(messageState.getLocationWhenShown().getLatitude(),
                                messageState.getLocationWhenShown().getLongitude()))
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

                GeoLocation[] locations = message.getPolygon();

                if(locations != null & locations.length>2){
                    for(GeoLocation location:locations){
                        polyOptions.add(new LatLng(
                                Double.parseDouble(location.getLat()),
                                Double.parseDouble(location.getLng())));
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
            if(message.getPolygon() != null){

                double [] centerLocation = WEALocationHelper.calculatePolyCenter(message.getPolygon());

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
