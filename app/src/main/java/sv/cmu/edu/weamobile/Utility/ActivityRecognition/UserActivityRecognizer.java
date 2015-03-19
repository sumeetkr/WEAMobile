package sv.cmu.edu.weamobile.utility.ActivityRecognition;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;

import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.service.ActivityRecognitionService;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAUtil;

/**
 * Created by sumeet on 2/27/15.
 */
public class UserActivityRecognizer extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String START_ACTIVITY_RECOGNITION = "start_activity_recognition";
    private static final String TAG = "ActivityRecognition";
    private static PendingIntent callbackIntent;
    private static int activitiesResultsCount = 0;
    private static GoogleApiClient googleApiClient;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        UserActivityRecognizer getService() {
            return UserActivityRecognizer.this;
        }
    }

//    public UserActivityRecognizer(Context context) {
//        this.context=context;
//    }

//    public UserActivityRecognizer() {
//        super("UserActivityRecognizer");
//        Logger.log("UserActivityRecognizer constructor called");
//    }
    /**
     * Call this to start a scan - don't forget to stop the scan once it's done.
     * Note the scan will not start immediately, because it needs to establish a connection with Google's servers - you'll be notified of this at onConnected
     */
    public void startActivityRecognitionScan(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

//        mActivityRecognitionClient	= new ActivityRecognitionClient(getApplicationContext(), this, this);
//        mActivityRecognitionClient.connect();
        Logger.log("UserActivityRecognizer startActivityRecognitionScan called");
    }

    public static void stopActivityRecognitionScan(){
        try{

            if(googleApiClient != null){
//                googleApiClient.unregisterConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this);
                googleApiClient.disconnect();
            }
        } catch (IllegalStateException e){
            Logger.log(e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Logger.log("Activity recognition onConnectionFailed called");
    }

    /**
     * Connection established - start listening now
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Logger.log("UserActivityRecognizer onConnected");

        //This issue wasted so much time
        //https://code.google.com/p/android/issues/detail?id=61850
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        callbackIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Logger.log("isConnectionCallbacksRegistered: " + String.valueOf(googleApiClient.isConnectionCallbacksRegistered(this)));
        Logger.log("isConnected: " + String.valueOf(googleApiClient.isConnected()));

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 0, callbackIntent);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.log(" UserActivityRecognizer onConnectionSuspended called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d("WEA", "UserActivityRecognizer started at " + WEAUtil.getTimeStringFromEpoch(System.currentTimeMillis() / 1000));
        Log.d("WEA", " UserActivityRecognizer Service onStart called with "+ intent);

        if(intent.getAction() !=null && START_ACTIVITY_RECOGNITION.compareTo(intent.getAction())==0){
                startActivityRecognitionScan();
        }

        return Service.START_NOT_STICKY;
    }
    /**
     * Google Play Services calls this once it has analysed the sensor data
     */
//    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.log("UserActivityRecognizer onHandleIntent called");
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            UserActivity activity = new UserActivity(result);
            Logger.log("ActivityRecognitionResult: " + activity.getActivityName() + " confidence: " + activity.getActivityConfidence());

            broadcastNewActivityIntent("Activity name: "+activity.getActivityName() + "    Confidence: " + activity.getActivityConfidence());

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
        Logger.log("UserActivityRecognizer onDestroy called");
    }

    private void broadcastNewActivityIntent(String name) {
        Logger.log("UserActivityRecognizer broadcastNewActivityIntent called");

        Intent newActivityIntent = new Intent();
        newActivityIntent.setAction("android.intent.action.NEW_ACTIVITY");
        newActivityIntent.addCategory(Intent.CATEGORY_DEFAULT);
        newActivityIntent.putExtra("message",name );
        getApplicationContext().sendBroadcast(newActivityIntent);
    }
}
