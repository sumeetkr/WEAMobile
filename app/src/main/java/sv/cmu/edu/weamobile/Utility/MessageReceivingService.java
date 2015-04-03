package sv.cmu.edu.weamobile.utility;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.views.MainActivity;

/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class MessageReceivingService extends Service {
    private GoogleCloudMessaging gcm;
    public static SharedPreferences savedValues;

    //Staging Registration URL : Registers an endpoint for Amazon SNS through this server.
    public static String SERVER_REGISTRATION_URL = "http://wea-stage.herokuapp.com/wea/api/registration/android";

    /*
     * Amazon Provided Method to send the notification to the app via intent / or trigger the app.
     */
    public static void sendToApp(Bundle extras, Context context){
           if (extras.containsKey("default"))
           {
                Intent newIntent = new Intent();
                newIntent.setClass(context, MainActivity.class);
                newIntent.putExtras(extras);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }

    }

    public void onCreate(){

        Log.e("DEBUG", "Message Receiving service created");

        super.onCreate();

        //Get Shared Preferences.
        final String preferences = getString(R.string.preferences);
        savedValues = getSharedPreferences(preferences, Context.MODE_PRIVATE);
        // In later versions multi_process is no longer the default
        if(VERSION.SDK_INT >  9){
            savedValues = getSharedPreferences(preferences, Context.MODE_MULTI_PROCESS);
        }

        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*
            First Launch : These functions get executed only on the first Launch of your app.
            Remember to restart on making any changes.
         */
        if(sharedPreferences.getBoolean(getString(R.string.first_launch), true)){
            //On the first launch: Register the application with the Server:
            //ToDo: What if first launch had an issue and the phone was not registered :(
            // like it happened to my phone
            register();

            //Write out that we have done a first launch.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.first_launch), false);
            editor.commit();
        }

        // Let AndroidMobilePushApp know we have just initialized and there may be stored messages
        sendToApp(new Bundle(), this);
    }

    /*
            Amazon Provided method that saves the method to the shared prefs.
     */
    protected static void saveToLog(Bundle extras, Context context){

        SharedPreferences.Editor editor=savedValues.edit();
        String numOfMissedMessages = context.getString(R.string.num_of_missed_messages);
        int linesOfMessageCount = 0;
        for(String key : extras.keySet()){
            String line = String.format("%s=%s", key, extras.getString(key));
            editor.putString("MessageLine" + linesOfMessageCount, line);
            linesOfMessageCount++;
        }
        editor.putInt(context.getString(R.string.lines_of_message_count), linesOfMessageCount);
        editor.putInt(context.getString(R.string.lines_of_message_count), linesOfMessageCount);
        editor.putInt(numOfMissedMessages, savedValues.getInt(numOfMissedMessages, 0) + 1);
        editor.commit();

        //show notification only for debugging
        //Users should only see full alert
        if(WEASharedPreferences.isShowNotificationsEnabled(context)){
            WEAUtil.postNotification(new Intent(context, MainActivity.class), context, extras.getString("default"));
        }
    }

    /*
        This is where the bulk of the registration process happens.
     */
    private void register() {
        Log.e("DEBUG", "Register Executed.");
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(AWSHelperUtility.GCM_PROJECT_NUMBER);

                    //This will print out the token or the unique registration id.
                    Log.e("REGISTRATION ID", token);
                    //now register the endpoint

                    //If you want to bypass Joao's server and create an endpoint yourself on AWS:
                    //Uncomment the following:
                    //AWSHelperUtility.createEndpoint(token);

                    //Ask the wea server to register the token and send us back an id.
                    askServerToRegister(token);
                } 
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    /*
            Takes the token provided, sends it to the WEA Server via
            AsyncHTTPPost Task.
     */
    private void askServerToRegister(String token) {

                //Hashmap is for a parameter list to be passed.
                HashMap hm = new HashMap();
                hm.put("token",token);

                AsyncHttpPost aTask = new AsyncHttpPost(hm,this);
                aTask.execute(SERVER_REGISTRATION_URL);

    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*
            AsyncTask for creating Post Calls.
     */
    class AsyncHttpPost extends AsyncTask<String, String, String> {
        private HashMap<String, String> mData = null;// post data
        private Context mContext;


        /**
         * constructor
         */
        public AsyncHttpPost(HashMap<String, String> data, Context c) {

            mData = data;
            mContext = c;
        }

        /**
         * background
         */
        @Override
        protected String doInBackground(String... params) {
            Log.i("ASYNCTASK","Registering token with server");

            byte[] result = null;
            String str = "";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL

            //Set header
            post.setHeader(HTTP.CONTENT_TYPE,
                    "application/json");

            try {
                // set up post data
                JSONObject json = new JSONObject(mData);

                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);


                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    Log.e("INTERNAL ERROR: 500", Integer.toString(statusLine.getStatusCode()));
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");
                    Log.e("INTERNAL ERROR: 500: " ,"STRING"+ str);
                }

                if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");

                }
                String responseBody = EntityUtils.toString(response.getEntity());

                //From the Response Parse JSON
                Log.e("RESPONSE_RETURNED:",responseBody);

                //Convert to JSON Object
                JSONObject json_response = new JSONObject(responseBody);
                String id = json_response.getString("message");

                //tags : [phone_id,registration_id]
                //Here's where the server sends back a phone id which can be retrieved later for the heartbeat
                WEASharedPreferences.setStringProperty(mContext,"phone_id",id);

                // Note : Since this is an asynctask there's no guarantee that it will be called before
                // the hearbeat call and so the first sync might not be able to connect, but subsequent
                // calls should be fine. *need to test*

                Log.i("PHONE_ID","======= PHONE ID ======== >>> "+id);


            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
            }
            return str;
        }

        /**
         * on getting result
         */
        @Override
        protected void onPostExecute(String result) {
            // something...
            Log.e("RESULT",result);

        }
    }

}