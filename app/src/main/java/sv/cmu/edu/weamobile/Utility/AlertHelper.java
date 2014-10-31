package sv.cmu.edu.weamobile.Utility;

import android.graphics.Typeface;
import android.location.Location;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;

/**
 * Created by sumeet on 10/30/14.
 */
public class AlertHelper {

    public static double getDistanceFromCentroid(Location myLocation, double[] polyCenter) {
        float center = 0000;
        try{
            float [] results= new float[2];
            Location.distanceBetween(polyCenter[0], polyCenter[1], myLocation.getLatitude(), myLocation.getLongitude(), results);
            center= results[0]/1000;
        }catch(Exception ex){

        }
        return center;
    }

    public static SpannableString getTextWithStyle(String text, int fontSize){
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan (new AbsoluteSizeSpan(fontSize), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


}
