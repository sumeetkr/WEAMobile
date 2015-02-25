package sv.cmu.edu.weamobile.utility.db;

/**
 * Created by harshalkutkar on 2/22/15.
 */
        import java.lang.reflect.Type;
        import java.util.ArrayList;
        import java.util.List;

        import android.app.Activity;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import sv.cmu.edu.weamobile.data.Alert;
        import sv.cmu.edu.weamobile.data.AlertState;
        import sv.cmu.edu.weamobile.data.AppConfiguration;
        import sv.cmu.edu.weamobile.data.GeoLocation;
        import sv.cmu.edu.weamobile.utility.Constants;
        import sv.cmu.edu.weamobile.utility.Logger;
        import sv.cmu.edu.weamobile.utility.WEASharedPreferences;

public class AlertDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TEXT,
            MySQLiteHelper.COLUMN_SCHEDULED_FOR,
            MySQLiteHelper.COLUMN_ENDING_AT,
            MySQLiteHelper.COLUMN_POLYGON,
            MySQLiteHelper.COLUMN_OPTIONS,
            MySQLiteHelper.COLUMN_IS_PHONE_VIBRATE,
            MySQLiteHelper.COLUMN_IS_TTS_EXPECTED,
            MySQLiteHelper.COLUMN_IS_GEO_FILTERED
    };

    public AlertDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addAlertToDatabase(Alert alert) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, alert.getId());
        values.put(MySQLiteHelper.COLUMN_TEXT, alert.getText());
        values.put(MySQLiteHelper.COLUMN_SCHEDULED_FOR, alert.getScheduledFor());
        values.put(MySQLiteHelper.COLUMN_ENDING_AT, alert.getScheduledFor());
        values.put(MySQLiteHelper.COLUMN_POLYGON, alert.GeoLocationToJson());
        values.put(MySQLiteHelper.COLUMN_OPTIONS, alert.getOptions());
        values.put(MySQLiteHelper.COLUMN_IS_PHONE_VIBRATE, alert.isPhoneExpectedToVibrate());
        values.put(MySQLiteHelper.COLUMN_IS_TTS_EXPECTED, alert.isTextToSpeechExpected());
        values.put(MySQLiteHelper.COLUMN_IS_GEO_FILTERED, alert.isGeoFiltering());


    }


    public void deleteAlert(Alert Alert) {
        long id = Alert.getId();
        System.out.println("Alert deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ALERTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Alert> getAllAlerts() {
        List<Alert> Alerts = new ArrayList<Alert>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ALERTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Alert Alert = cursorToAlert(cursor);
            Alerts.add(Alert);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Alerts;
    }

    private Alert cursorToAlert(Cursor cursor) {
        Alert Alert = new Alert();
        Alert.setId(cursor.getInt(0));
        Alert.setText(cursor.getString(1));
        Alert.setScheduledFor(cursor.getString(2));
        Alert.setEndingAt(cursor.getString(3));
        Alert.setPolygon(JSONtoGeoLocation(cursor.getString(4)));

        return Alert;
    }




    private GeoLocation[] JSONtoGeoLocation(String string) {
        Gson gson = new Gson();
        GeoLocation[] polygon = gson.fromJson(string, GeoLocation[].class);
        Logger.log("Converting JSON to Geolocation...");
        Logger.log("JSON_TO_GEO",polygon.toString());
        return  polygon;



    }

    /*
        This gets called from WEABackground
     */
    public void addAlertStateToDatabase(Alert alert)
    {
            //Redirecting to the underlying db helper.
            dbHelper.addAlertStateToDatabase(alert);
    }

}

