package sv.cmu.edu.weamobile.service;

/**
 * Created by sumeet on 2/27/15.
 */
import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;

import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.ActivityRecognition.UserActivityRecognizer;
import sv.cmu.edu.weamobile.utility.Logger;

public class ActivityRecognitionService extends IntentService{

    private static final String TAG ="ActivityRecognition";
    private static int activitiesResultsCount = 0;

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    /**
     * Google Play Services calls this once it has analysed the sensor data
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            UserActivity activity = new UserActivity(result);
            Logger.log("ActivityRecognitionResult: " + activity.getActivityName() + " confidence: " + activity.getActivityConfidence());

            broadcastNewActivityIntent("Activity name: "+activity.getActivityName() + "    Confidence: " + activity.getActivityConfidence());

//            UserActivityRecognizer.stopActivityRecognitionScan();
            UserActivityRecognizer.completeWakefulIntent(intent);
            activitiesResultsCount += 1;
            Logger.log("Activity result count : " + activitiesResultsCount);
        }
    }

    private void broadcastNewActivityIntent(String name) {
        Intent newActivityIntent = new Intent();
        newActivityIntent.setAction("android.intent.action.NEW_ACTIVITY");
        newActivityIntent.addCategory(Intent.CATEGORY_DEFAULT);
        newActivityIntent.putExtra("message",name );
        getApplicationContext().sendBroadcast(newActivityIntent);
    }

}