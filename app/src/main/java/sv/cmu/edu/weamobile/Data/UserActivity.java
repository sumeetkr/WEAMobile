package sv.cmu.edu.weamobile.data;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.Serializable;
import java.util.List;

import sv.cmu.edu.weamobile.R;

/**
 * Created by sumeet on 2/27/15.
 */
public class UserActivity implements Serializable {

    private int detectedActivityTYpe;
    private String activityName;
    private int activityConfidence;
    private int secondaryActivityType;
    private String secondaryActivity="NA";
    private int secondaryActivityConfidence = 0;

    public UserActivity(ActivityRecognitionResult result){
        DetectedActivity activity = result.getMostProbableActivity();
        setDetectedActivityTYpe(activity.getType());
        setActivityName(getFriendlyName(getPrimaryActivityType()));
        setActivityConfidence(activity.getConfidence());

        // Get the most probable activity from the list of activities in the update
        DetectedActivity mostProbableActivity = result.getMostProbableActivity();

        // Get the type of activity
        int activityType = mostProbableActivity.getType();

        if (activityType == DetectedActivity.ON_FOOT) {
            DetectedActivity betterActivity = walkingOrRunning(result.getProbableActivities());
            if (null != betterActivity){
                mostProbableActivity = betterActivity;
                setSecondaryActivity(getFriendlyName(betterActivity.getType()));
                setSecondaryActivityTYpe(betterActivity.getType());
                setActivityConfidence(betterActivity.getConfidence());
            }
        }
    }

    public static String getFriendlyName(int detected_activity_type){
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
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
            case -1:
                return "NA";
            default:
                return "unknown";
        }
    }

    public static BitmapDescriptor getBitmap(int primaryActivityType, int secondaryActivityType){

        int detected_activity_type = primaryActivityType;
        if(primaryActivityType==DetectedActivity.ON_FOOT){
            if(secondaryActivityType>-1){
                detected_activity_type = secondaryActivityType;
            }
        }

        switch (detected_activity_type ) {
            case DetectedActivity.IN_VEHICLE:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_driving);
            case DetectedActivity.ON_BICYCLE:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_biking);
            case DetectedActivity.ON_FOOT:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
            case DetectedActivity.TILTING:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_tilting);
            case DetectedActivity.STILL:
//                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_still);
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_sitting);
            case DetectedActivity.RUNNING:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_running);
            case DetectedActivity.WALKING:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_walking);
            case -1:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_question_mark);
            default:
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_question_mark);
        }
    }

    public int getPrimaryActivityType() {
        return detectedActivityTYpe;
    }

    private void setDetectedActivityTYpe(int detectedActivityTYpe) {
        this.detectedActivityTYpe = detectedActivityTYpe;
    }

    public int getSecondaryActivityTYpe() {
        return secondaryActivityType;
    }

    private void setSecondaryActivityTYpe(int detectedActivityTYpe) {
        this.secondaryActivityType = detectedActivityTYpe;
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

    public String getSecondaryActivity() {
        return secondaryActivity;
    }

    public void setSecondaryActivity(String secondaryActivity) {
        this.secondaryActivity = secondaryActivity;
    }

    public int getSecondaryActivityConfidence() {
        return secondaryActivityConfidence;
    }

    public void setSecondaryActivityConfidence(int secondaryActivityConfidence) {
        this.secondaryActivityConfidence = secondaryActivityConfidence;
    }

    private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING)
                continue;

            if (activity.getConfidence() > confidence)
                myActivity = activity;
        }

        return myActivity;
    }

}
