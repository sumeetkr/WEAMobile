package sv.cmu.edu.weamobile.utility.ActivityRecognition;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;

import sv.cmu.edu.weamobile.service.ActivityRecognitionService;
import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 2/27/15.
 */
public class UserActivityRecognizer implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{
    private Context context;
    private static final String TAG = "ActivityRecognition";
    private static ActivityRecognitionClient mActivityRecognitionClient;
    private static PendingIntent callbackIntent;

    public UserActivityRecognizer(Context context) {
        this.context=context;
    }
    /**
     * Call this to start a scan - don't forget to stop the scan once it's done.
     * Note the scan will not start immediately, because it needs to establish a connection with Google's servers - you'll be notified of this at onConnected
     */
    public void startActivityRecognitionScan(){
        mActivityRecognitionClient	= new ActivityRecognitionClient(context, this, this);
        mActivityRecognitionClient.connect();
        Logger.log("startActivityRecognitionScan");
    }

    public void stopActivityRecognitionScan(){
        try{
            mActivityRecognitionClient.removeActivityUpdates(callbackIntent);
            Logger.debug("stopActivityRecognitionScan");
        } catch (IllegalStateException e){
            Logger.log(e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Logger.log("Activity recognition onConnectionFailed");
    }

    /**
     * Connection established - start listening now
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Intent intent = new Intent(context, ActivityRecognitionService.class);
        callbackIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mActivityRecognitionClient.requestActivityUpdates(200, callbackIntent); // 0 sets it to update as fast as possible, just use this for testing!
    }

    @Override
    public void onDisconnected() {
        Logger.log("onDisconnected");
    }

}
