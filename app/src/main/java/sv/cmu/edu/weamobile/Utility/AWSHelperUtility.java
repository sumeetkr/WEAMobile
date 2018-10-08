package sv.cmu.edu.weamobile.utility;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

import sv.cmu.edu.weamobile.R;


/**
 * Created by harshalkutkar on 3/15/15.
 */
public class AWSHelperUtility {

    //Constants

    /* Constants for Harsh
    public static String IDENTITY_POOL_ID="us-east-1:d165f78e-74d2-49c9-91f2-dc27c6c45125";
    public static String GCM_PROJECT_NUMBER = "258636788597";
    public static String AWS_ACCESS_ID = "AKIAI3DKVXUUFFLKGF2Q";
    public static String AWS_ACCESS_SECRET = "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";
    public static String AWS_ARN = "arn:aws:sns:us-east-1:198769273005:app/GCM/weamobile";
    public static String AWS_TOPIC_ARN = "arn:aws:sns:us-east-1:198769273005:weamobile";
    */


    //these Id's are given by Joel Krebs.
    public static String GCM_PROJECT_NUMBER = "";
    public static String AWS_ACCESS_ID = "";
    public static String AWS_ACCESS_SECRET = "";
    public static String AWS_ARN = "";
    public static String AWS_TOPIC_ARN = "";

    /*
        This function registers an Endpoint with Amazon AWS automatically through a access key and secret key
        You can change the above (You probably want a better auth system than giving direct access to aws creds)
        You would also want to subscribe to a topic.
     */

    public static void createEndpoint(String registrationId)
    {
        Log.i("DEBUG_ARN","Creating Endpoint");
        AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_ID,AWS_ACCESS_SECRET);
        AmazonSNSClient pushClient = new AmazonSNSClient(awsCredentials);
        //probably no need for this
        String customPushData = "WEAMobileTest"+Long.toString(System.currentTimeMillis());
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setCustomUserData(customPushData);
        platformEndpointRequest.setToken(registrationId);
        platformEndpointRequest.setPlatformApplicationArn(AWS_ARN);

        CreatePlatformEndpointResult result = pushClient.createPlatformEndpoint(platformEndpointRequest);
        //subscribe to an SNS topic
        SubscribeRequest subRequest = new SubscribeRequest(AWS_TOPIC_ARN, "application", result.getEndpointArn());
        pushClient.subscribe(subRequest);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(Context context,String title,String text) {
        String notificationID = "000";
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // intent triggered, you can add other intent for other actions
        Notification mNotification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.alert_red)
                .setSound(soundUri).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, mNotification);



    }

}
