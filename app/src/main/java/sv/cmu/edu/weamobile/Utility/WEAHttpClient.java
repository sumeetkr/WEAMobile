package sv.cmu.edu.weamobile.utility;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

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

    public static void sendHeartbeat(String data, Context context ,String server_url) {
        final Context ctxt = context;
        String response = "";
        try {

            Logger.log("sendHeartbeat ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(data);

            client.put(ctxt, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {

                    WEASharedPreferences.setStringProperty(ctxt,
                            "lastTimeChecked",
                            String.valueOf(System.currentTimeMillis()));

                    Logger.log("JsonSender", "Success - ");
                    Logger.debug(response);
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

    public static void getConfigurationAsync(Context context){

        GPSTracker tracker = new GPSTracker(context);
        GeoLocation location ;
        if(tracker.canGetLocation()){
            location = tracker.getNetworkGeoLocation();
            Logger.log("Sending lat " + location.getLatitude());
            Logger.log("Sending lng " + location.getLongitude());
            sendHeartbeat(location.getJson(), context, Constants.URL_TO_GET_CONFIGURATION + WEAUtil.getIMSI(context));
        }else{
            location = new GeoLocation("0.00", "0.00");
            Logger.log("Cannot get location for heartbeat");
            sendHeartbeat(location.getJson(), context, Constants.URL_TO_GET_CONFIGURATION + WEAUtil.getIMSI(context));
        }

        tracker.stopUsingGPS();
        //fetch application configuration from server

    }

    public static void saveUserLogin(final Context context, final String mUserId) {
        final Context ctxt = context;
        String response = "";
        String server_url = Constants.REGISTRATION_URL_ROOT + WEAUtil.getIMSI(context) + "/" + mUserId;
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
        String server_url = Constants.STATE_URL_ROOT+ alertId+ "/"+ WEAUtil.getIMSI(context) ;
        try {

            StringEntity entity = new StringEntity(alertStateInJson);
            Logger.log("send alert state ", server_url);
            Logger.debug(alertStateInJson);
            AsyncHttpClient client = new AsyncHttpClient();

            client.put(ctxt, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Logger.log("WEA sending alert state", "Success - ");
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.w("WEA sending alert state", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("WEA JsonSender",e.getMessage());
        }
    }
}
