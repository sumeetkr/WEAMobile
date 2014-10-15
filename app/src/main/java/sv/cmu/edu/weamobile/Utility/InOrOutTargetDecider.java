package sv.cmu.edu.weamobile.Utility;

import android.content.Context;

/**
 * Created by sumeet on 9/24/14.
 */
public class InOrOutTargetDecider {

    public static boolean isInTarget(Context context, String polygon){
        boolean isInTarget = true;

        GPSTracker tracker =  new GPSTracker(context);
        //Get location from tracker and calculate if location is in polygon.

        return isInTarget;
    }
}
