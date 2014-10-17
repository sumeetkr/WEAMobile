package sv.cmu.edu.weamobile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AlertContent;
import sv.cmu.edu.weamobile.Data.GeoLocation;
import sv.cmu.edu.weamobile.Utility.GPSTracker;
import sv.cmu.edu.weamobile.Utility.Logger;


/**
 * A fragment representing a single Alert detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link AlertDetailActivity}
 * on handsets.
 */
public class AlertDetailFragment extends Fragment {
    private GoogleMap mMap;
    public static final String ARG_ITEM_ID = "item_id";
    private Alert alert;
    private String localTime;
    private View rootView;
    private Location myLocation;
    private boolean isDialog = false;
    private Vibrator vibrator;
    private TextToSpeech tts;


    public AlertDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String key = getArguments().getString(ARG_ITEM_ID);
            Logger.log("AlertDetailFragment key: " + key);
            alert = AlertContent.getAlertsMap().get(Integer.parseInt(key));
        }

        if(getArguments().containsKey("isDialog")){
            isDialog = getArguments().getBoolean("isDialog");
            if(isDialog){
                alertUserWithVibrationAndSpeech();
            }
        }
        setUpMapIfNeeded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alert_detail, container, false);


        // Show the dummy content as text in a TextView.
        if (alert != null) {
            Logger.log("Item is there"+ alert.getText());
             ((TextView) rootView.findViewById(R.id.alertText)).setText(getTextWithStyle(alert.getAlertType() + " : "+ alert.toString(),25));
              localTime = getTimeString(Long.parseLong(alert.getScheduledFor()));
             ((TextView) rootView.findViewById(R.id.txtLabel)).setText(getTextWithStyle(alert.getAlertType() + " : "+ alert.toString(),20));
        }else{
            Logger.log("Item is null");
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onDestroy() {
        vibrator = null;
        if(tts != null) tts.shutdown();
        super.onDestroy();
    }

    private void alertUserWithVibrationAndSpeech() {

        vibrator = (Vibrator) getActivity().getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {200, 200, 200, 200, 200, 200, 200, 400,200,
                400,200, 400,200, 200, 200, 200, 200, 200, 400,
                200, 200, 200, 200, 200, 200,400,200, 400,
                200, 400,200, 200, 200, 200, 200, 200};

        vibrator.vibrate(pattern, -1);

        tts = new TextToSpeech(this.getActivity(), new TTSListener(alert.getText(), 2));
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
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

    private void setUpMap() {
        GPSTracker tracker = new GPSTracker(this.getActivity().getApplicationContext());
        myLocation = tracker.getLocation();
        Logger.log("my location: " +myLocation.toString());

        mMap.clear();
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                .title("Your location"));
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

            double [] polyCenter = getCentroid(Arrays.asList(alert.getPolygon()));
            Polygon polygon = mMap.addPolygon(polyOptions);
            setCenter(polyCenter[0],polyCenter[1]);

            String distance = "close";
            float [] results= new float[2];
            Location.distanceBetween(polyCenter[0],polyCenter[1],myLocation.getLatitude(), myLocation.getLongitude(),results);
            ((TextView) rootView.findViewById(R.id.txtLabel)).setText(getTextWithStyle("Time: " + localTime + " You are at distance " + results[0]/1000 + " miles",20));
        }
    }

    private void setCenter(double lat, double lng){
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(lat,lng));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(17);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    private static double[] getCentroid(List<GeoLocation> points) {
        double[] centroid = { 0.0, 0.0 };

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).getLatitude();
            centroid[1] += points.get(i).getLongitude();
        }

        int totalPoints = points.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return centroid;
    }

    private String getTimeString(long epoch){
        Date date = new Date(epoch * 1000L);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        return  formatted;
    }

    private SpannableString getTextWithStyle(String text, int fontSize){
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan (new AbsoluteSizeSpan(fontSize), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    protected class TTSListener implements TextToSpeech.OnInitListener {

        String what_to_speak = null;
        Integer how_many_times = null;

        public TTSListener(String to_speak, Integer times) {
            what_to_speak = to_speak;
            how_many_times = times;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.speak(what_to_speak, TextToSpeech.QUEUE_FLUSH, null);
                    for (Integer n = 1; n < how_many_times; n++) {
                        tts.speak(what_to_speak, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        }
    }

}
