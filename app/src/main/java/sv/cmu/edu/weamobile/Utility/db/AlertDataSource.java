package sv.cmu.edu.weamobile.utility.db;

/**
 * Created by harshalkutkar on 2/22/15.
 */
        import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.Logger;

public class AlertDataSource {

    // Database fields
    private SQLiteDatabase database;
    private WEASQLiteHelper dbHelper;
    private String[] allColumns = { WEASQLiteHelper.COLUMN_ID,
            WEASQLiteHelper.COLUMN_TEXT,
            WEASQLiteHelper.COLUMN_SCHEDULED_FOR,
            WEASQLiteHelper.COLUMN_ENDING_AT,
            WEASQLiteHelper.COLUMN_POLYGON,
            WEASQLiteHelper.COLUMN_OPTIONS,
            WEASQLiteHelper.COLUMN_IS_PHONE_VIBRATE,
            WEASQLiteHelper.COLUMN_IS_TTS_EXPECTED,
            WEASQLiteHelper.COLUMN_IS_GEO_FILTERED
    };

    public AlertDataSource(Context context) {
        dbHelper = new WEASQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addAlertToDatabase(Alert alert) {
        ContentValues values = new ContentValues();
        values.put(WEASQLiteHelper.COLUMN_ID, alert.getId());
        values.put(WEASQLiteHelper.COLUMN_TEXT, alert.getText());
        values.put(WEASQLiteHelper.COLUMN_SCHEDULED_FOR, alert.getScheduledFor());
        values.put(WEASQLiteHelper.COLUMN_ENDING_AT, alert.getScheduledFor());
        values.put(WEASQLiteHelper.COLUMN_POLYGON, alert.GeoLocationToJson());
        values.put(WEASQLiteHelper.COLUMN_OPTIONS, alert.getOptions());
        values.put(WEASQLiteHelper.COLUMN_IS_PHONE_VIBRATE, alert.isPhoneExpectedToVibrate());
        values.put(WEASQLiteHelper.COLUMN_IS_TTS_EXPECTED, alert.isTextToSpeechExpected());
        values.put(WEASQLiteHelper.COLUMN_IS_GEO_FILTERED, alert.isGeoFiltering());


    }


    public void deleteAlert(Alert Alert) {
        long id = Alert.getId();
        System.out.println("Alert deleted with id: " + id);
        database.delete(WEASQLiteHelper.TABLE_ALERTS, WEASQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Alert> getAllAlerts() {
        List<Alert> Alerts = new ArrayList<Alert>();

        Cursor cursor = database.query(WEASQLiteHelper.TABLE_ALERTS,
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

    public AlertState getAlertState(Context context, Alert alert) throws NoSuchFieldException {
        SQLiteDatabase sqldb = database;
        AlertState newState = null;
        long id = alert.getId()+alert.getScheduledEpochInSeconds();
        String Query = "Select * from " + WEASQLiteHelper.TABLE_ALERTSTATE + " where " + WEASQLiteHelper.COLUMN_ALERTSTATE_ID + " = " + Long.toString(id);
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            Log.e("ERROR", "Alert State Does not exist!!!!");
           throw new NoSuchFieldException();
        }
        if (cursor != null ) {
            if  (cursor.moveToFirst()) {

                    //Right now we only need text but incase there's something to be done with id/sFor:
                    int newId = cursor.getInt(cursor.getColumnIndex(WEASQLiteHelper.COLUMN_ALERTSTATE_ID));
                    String sFor = cursor.getString(cursor.getColumnIndex(WEASQLiteHelper.COLUMN_ALERTSTATE_SCHEDULEDFOR));

                    String text = cursor.getString(cursor.getColumnIndex(WEASQLiteHelper.COLUMN_ALERTSTATE_TEXT));
                    newState = AlertState.fromJson(text);

            }
        }
        cursor.close();

        return newState;
    }
}

