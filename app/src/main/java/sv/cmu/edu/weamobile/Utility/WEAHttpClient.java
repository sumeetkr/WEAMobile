package sv.cmu.edu.weamobile.Utility;

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
            Log.d("IPS JsonSender",e.getMessage());
        }

//        data.Clean();
        return result;
    }

    public static void sendHeartbeat(String data, Context context ,String server_url) {
        final Context ctxt = context;
        String response = "";
        try {

            Log.w("IPS getDataFromServer ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(data);

            client.put(ctxt, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.w("IPS JsonSender", "Success - ");
                    Intent intent = new Intent("new-config-event");
                    intent.putExtra("message", response);
                    LocalBroadcastManager.getInstance(ctxt).sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("IPS JsonSender", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                    Intent intent = new Intent("new-config-event");
                    intent.putExtra("message", "");
                    LocalBroadcastManager.getInstance(ctxt).sendBroadcast(intent);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("IPS JsonSender",e.getMessage());
        }
    }

    public static String getDataFromServer(final Context context ,String server_url) {
        String response = "";
        try {

            Log.w("IPS getDataFromServer ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(context, server_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.w("IPS JsonSender", "Success - " + response);
                    Intent intent = new Intent("new-config-event");
                    intent.putExtra("message", response);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("IPS JsonSender", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("IPS JsonSender",e.getMessage());
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
}
