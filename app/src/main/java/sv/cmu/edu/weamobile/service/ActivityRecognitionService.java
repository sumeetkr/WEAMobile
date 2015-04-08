package sv.cmu.edu.weamobile.service;

/**
 * Created by sumeet on 2/27/15.
 */

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;

import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.ActivityRecognition.UserActivityRecognizer;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;

public class ActivityRecognitionService extends IntentService {

    private static final String TAG ="ActivityRecognition";
    private static int activitiesResultsCount = 0;


    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
        Logger.log("ActivityRecognitionService constructor called");
    }

    /**
     * Google Play Services calls this once it has analysed the sensor data
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.log("ActivityRecognitionService onHandleIntent called");
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            UserActivity activity = new UserActivity(result);
            Logger.log("ActivityRecognitionResult: " + activity.getActivityName() + " confidence: " + activity.getActivityConfidence());

            broadcastNewActivityIntent(activity, activity.getActivityName(), activity.getActivityConfidence());

            activitiesResultsCount += 1;
            Logger.log("Activity result count : " + activitiesResultsCount);

            if(activitiesResultsCount>10) {
                UserActivityRecognizer.stopActivityRecognitionScan();
                activitiesResultsCount =0;
//                UserActivityRecognizer.completeWakefulIntent(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.log("ActivityRecognitionService onDestroy called");
    }


    private void broadcastNewActivityIntent(UserActivity activity,
                                            String activityName,
                                            int activityConfidence) {
        Logger.log("ActivityRecognitionService broadcastNewActivityIntent called");

        Intent newActivityIntent = new Intent();
        newActivityIntent.setAction("android.intent.action.NEW_ACTIVITY");
        newActivityIntent.addCategory(Intent.CATEGORY_DEFAULT);
        newActivityIntent.putExtra(Constants.ACTIVITY_TYPE, activityName);
        newActivityIntent.putExtra(Constants.ACTIVITY_CONFIDENCE, activityConfidence);
        newActivityIntent.putExtra(Constants.ACTIVITY, activity);
        getApplicationContext().sendBroadcast(newActivityIntent);
    }
}