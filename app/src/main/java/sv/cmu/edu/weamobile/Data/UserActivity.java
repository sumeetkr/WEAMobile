package sv.cmu.edu.weamobile.data;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by sumeet on 2/27/15.
 */
public class UserActivity {

    private int detectedActivityTYpe;
    private String activityName;
    private int activityConfidence;

    public UserActivity(ActivityRecognitionResult result){
        DetectedActivity activity = result.getMostProbableActivity();
        setDetectedActivityTYpe(activity.getType());
        setActivityName(getFriendlyName(getDetectedActivityTYpe()));
        setActivityConfidence(activity.getConfidence());
    }

    private static String getFriendlyName(int detected_activity_type){
        switch (detected_activity_type ) {
            case DetectedActivity.IN_VEHICLE:
                return "in vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on bike";
            case DetectedActivity.ON_FOOT:
                return "on foot";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.STILL:
                return "still";
            default:
                return "unknown";
        }
    }

    public int getDetectedActivityTYpe() {
        return detectedActivityTYpe;
    }

    private void setDetectedActivityTYpe(int detectedActivityTYpe) {
        this.detectedActivityTYpe = detectedActivityTYpe;
    }

    public String getActivityName() {
        return activityName;
    }

    private void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getActivityConfidence() {
        return activityConfidence;
    }

    private void setActivityConfidence(int activityConfidence) {
        this.activityConfidence = activityConfidence;
    }
}
