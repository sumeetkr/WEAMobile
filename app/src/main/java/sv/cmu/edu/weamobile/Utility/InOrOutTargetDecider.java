package sv.cmu.edu.weamobile.Utility;

/**
 * Created by sumeet on 9/24/14.
 */
public class InOrOutTargetDecider {

    public static boolean isInTarget(String polygon){
        boolean isInTarget = true;

        GPSTracker tracker =  GPSTracker.getGPSTrackerInstance();
        //Get location from tracker and calculate if location is in polygon.
        return isInTarget;
    }
}
