package sv.cmu.edu.weamobile.utility;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sv.cmu.edu.weamobile.data.GeoLocation;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEAHttpClient {
    // http://loopj.com/android-async-http/
    public static String sendToServer(String data, Context context ,String server_url) {
        String result = "success";
        try {
            StringEntity entity = new StringEntity(data);

            Log.w("WEA JsonSender ", data);
            Log.w("WEA JsonSender ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();

            client.post(context, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.w("WEA JsonSender result", "Success - " + response);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("WEA JsonSender result", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }

//        data.Clean();
        return result;
    }

    public static void sendHeartbeat(String data, Context context ) {

        String phoneId = WEASharedPreferences.getStringProperty(context,Constants.PHONE_ID);
        String serverUrl = Constants.URL_TO_SEND_HEARTBEAT +phoneId+"/heartbeat";

        final Context ctxt = context;
        String response = "";
        try {

            Logger.log("sendHeartbeat ", serverUrl+ " " + data);
            AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(data);

            client.put(ctxt, serverUrl, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {

                    WEASharedPreferences.setStringProperty(ctxt,
                            "lastTimeChecked",
                            String.valueOf(System.currentTimeMillis()));

                    Logger.log("JsonSender", "Success - ");
//                    Logger.debug(response);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("WEA JsonSender", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }
    }

    public static void fetchAlerts( Context context ){
        String serverUrl = Constants.URL_TO_FETCH_ALERTS;
        final Context ctxt = context;
        String response = "";
        try {

            Logger.log("Fetching alerts ", serverUrl);
            AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity("");

            client.get(ctxt, serverUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {

                    WEASharedPreferences.setStringProperty(ctxt,
                            Constants.LAST_TIME_WHEN_HEARTBEAT_SENT,
                            String.valueOf(System.currentTimeMillis()));

                    Logger.log("JsonSender", "Success - ");
//                    Logger.debug(response);
                    Intent intent = new Intent("new-config-event");
                    intent.putExtra("message", response);
                    LocalBroadcastManager.getInstance(ctxt).sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("WEA JsonSender", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                    Intent intent = new Intent("new-config-event");
                    intent.putExtra("message", "");
                    LocalBroadcastManager.getInstance(ctxt).sendBroadcast(intent);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }
    }
    public static String getDataFromServer(final Context context ,String server_url) {
        String response = "";
        try {

            Log.w("WEA getDataFromServer ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(context, server_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.w("WEA JsonSender", "Success - " + response);
                    Intent intent = new Intent("new-config-event");
                    intent.putExtra("message", response);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("WEA JsonSender", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }

        return response;
    }

    public static String getDataFromServerSynchronously(final Context context ,String server_url) {
        SyncHttpClient client = new SyncHttpClient();
        String response = "";
        client.get(context, server_url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // you can do something here before request starts
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                response = response;
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                // handle failure here
            }

        });

        return  response;
    }

    public static void saveUserLogin(final Context context, final String mUserId) {
        final Context ctxt = context;
        String response = "";
        String server_url = Constants.REGISTRATION_URL_ROOT + WEAUtil.getIMEI(context) + "/" + mUserId;
        try {

            Logger.log("send registration ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();

            client.put(ctxt, server_url, null, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {

                    Logger.log("registration", "Success - ");
                    WEASharedPreferences.setStringProperty(context,Constants.USER_NAME, mUserId);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    //Toast.makeText(context, "Registration failed, please check WEA team.", Toast.LENGTH_LONG);
                    Log.w("WEA registration", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }
    }

    public static void sendAlertState(Context context, String alertStateInJson, String alertId) {
        final Context ctxt = context;
        String response = "";
        String phoneId = WEASharedPreferences.getStringProperty(context,Constants.PHONE_ID);
        String server_url = Constants.STATE_URL_ROOT+ alertId+ "/"+ phoneId ;
        try {

            StringEntity entity = new StringEntity(alertStateInJson);
            Logger.log("send message state ", server_url);
            Logger.debug(alertStateInJson);
            AsyncHttpClient client = new AsyncHttpClient();

            client.post(ctxt, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Logger.log("WEA sending message state", "Success - ");
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    Logger.log("Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }
    }

    public static void registerPhoneAync(final Context context, final GeoLocation location){

        final String server_url = Constants.SERVER_REGISTRATION_URL;

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {

                    String token = WEASharedPreferences.getStringProperty(context, Constants.PHONE_TOKEN);
                    if(token == null || (token!= null && token.isEmpty()) ){
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                        token = gcm.register(AWSHelperUtility.GCM_PROJECT_NUMBER);

                        if(token!= null && !token.isEmpty()){
                            Logger.log("Got new token");
                            WEASharedPreferences.setStringProperty(context, Constants.PHONE_TOKEN, token);
                        }
                        else
                        {
                            // if token is null, you cannot register phone
                            WEAUtil.showMessageIfInDebugMode(context, "Could not get AWS token");
                            Logger.log("Could not get AWS token");
                            return null;
                        }
                    }
                    Logger.log("Current token : " + token);

                    String phoneId = WEASharedPreferences.getStringProperty(context, Constants.PHONE_ID);

                    if(phoneId == null || (phoneId!= null && phoneId.isEmpty())){
                        HashMap hm = new HashMap();
                        hm.put("token",token);
                        hm.put("imei", WEAUtil.getIMEI(context));
                        hm.put("lat", location.getLat());
                        hm.put("lng", location.getLng());

                        JSONObject json = new JSONObject(hm);
                        StringEntity entity = new StringEntity(json.toString());

                        Logger.log("Registering phone ", server_url);
                        Logger.log("Data ", json.toString());
                        AsyncHttpClient client = new AsyncHttpClient();

                        client.post(context, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(String response) {
                                Logger.log("WEA trying to register phone", "Success - ");
                                JSONObject json_response = null;
                                try {
                                    json_response = new JSONObject(response);
                                    String id = json_response.getString("message");

                                    //tags : [phone_id,registration_id]
                                    //Here's where the server sends back a phone id which can be retrieved later for the heartbeat
                                    if(id!= null && !id.isEmpty()){
                                        WEASharedPreferences.setStringProperty(context, Constants.PHONE_ID, id);
                                    }

                                    // Note : Since this is an asynctask there's no guarantee that it will be called before
                                    // the hearbeat call and so the first sync might not be able to connect, but subsequent
                                    // calls should be fine. *need to test*

                                    Logger.log("PHONE_ID","======= PHONE ID ======== >>> "+id);

                                    Intent intent = new Intent("new-register-event");
                                    intent.putExtra(Constants.PHONE_ID, id);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                                } catch (JSONException e) {
                                    Logger.log(e.getMessage());
                                    Logger.log("Phone registration failed");
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {

                                Logger.log("Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);

                                Intent intent = new Intent("new-register-event");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
