package sv.cmu.edu.weamobile.utility.db;

/**
 * Created by harshalkutkar on 2/22/15.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.utility.Logger;

/*
  This class is responsible for creating the database.
  The onUpgrade() method will simply delete all existing data and re-create the table.
  It also defines several constants for the table name and the table columns.
 */
public class WEASQLiteHelper extends SQLiteOpenHelper {

    //Database Name
    private static final String DATABASE_NAME = "WEAMobile.db";

    //Defining the table Alerts & its columns
    public static final String TABLE_ALERTS = "alerts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_SCHEDULED_FOR = "scheduledFor";
    public static final String COLUMN_ENDING_AT = "endingAt";
    public static final String COLUMN_POLYGON = "polygon";
    public static final String COLUMN_OPTIONS = "options";
    public static final String COLUMN_IS_PHONE_VIBRATE = "vibrate";
    public static final String COLUMN_IS_TTS_EXPECTED = "isTextToSpeechExpected";
    public static final String COLUMN_IS_GEO_FILTERED = "geoFiltering";
    public static final String COLUMN_ALERT_STATE = "alertState";

    //Defining the alertstate table
    public static final String TABLE_ALERTSTATE = "alertstate";
    public static final String COLUMN_ALERTSTATE_ID = "_id";
    public static final String COLUMN_ALERTSTATE_SCHEDULEDFOR = "scheduledFor";
    public static final String COLUMN_ALERTSTATE_TEXT = "text";

    private static final int DATABASE_VERSION = 6;

    /*
            The following create statement creates a table for storing individual properties of the alerts
     */
    private static final String ALERT_TABLE_CREATE_SQL = "create table "
            + TABLE_ALERTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT
            + " text, "+ COLUMN_SCHEDULED_FOR
            + " text, " + COLUMN_ENDING_AT
            + " text, "+ COLUMN_POLYGON
            + " text, " + COLUMN_OPTIONS
            + " integer," + COLUMN_IS_PHONE_VIBRATE
            + " NUMERIC," + COLUMN_IS_TTS_EXPECTED
            + " NUMERIC," + COLUMN_IS_GEO_FILTERED
            + " NUMERIC," + COLUMN_ALERT_STATE
            + " text "
            +" );";

    /*
            The following create statement creates a table for storing the alert state
     */
    private static final String DATABASE_CREATE_ALERTSTATE = "create table "
            + TABLE_ALERTSTATE + "(" + COLUMN_ALERTSTATE_ID
            + " integer primary key not null, " + COLUMN_ALERTSTATE_SCHEDULEDFOR
            + " text, "+ COLUMN_ALERTSTATE_TEXT
            + " text "
            +" );";


    public WEASQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Logger.log("MySQLiteHelper","========== MYSQLITE HELPER CREATE =======");

        database.execSQL(ALERT_TABLE_CREATE_SQL);
        Logger.log("MySQLiteHelper"," [ Created table alerts ]");

        database.execSQL(DATABASE_CREATE_ALERTSTATE);
        Logger.log("MySQLiteHelper","[ CREATED TABLE ALERTSTATE ]");

        database.execSQL(LocationDataSource.CREATE_LOCATION_TABLE_SQL);
        Logger.log("MySQLiteHelper","[ CREATED TABLE LOCATION ]");

        database.execSQL(MessageDataSource.CREATE_MESSAGE_TABLE_SQL);
        Logger.log("MySQLiteHelper","[ CREATED TABLE MESSAGE ]");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WEASQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTSTATE);
        db.execSQL("DROP TABLE IF EXISTS " + LocationDataSource.LOCATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MessageDataSource.MESSAGE_TABLE);
        onCreate(db);
    }


    //update alert state (AS exists)
    public void updateAlertState(AlertState as)
    {
        Logger.log("MySQLiteHelper","=== UPDATING ALERT STATE ==");
        //first find out if the alert exists in the database
        if (CheckIsDataAlreadyInDBorNot(TABLE_ALERTSTATE,COLUMN_ALERTSTATE_ID,as.getUniqueId()));
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ALERTSTATE_TEXT, as.getJson());
            // updating row
            int rowsUpdated = getWritableDatabase().update(TABLE_ALERTSTATE, values, COLUMN_ALERTSTATE_ID + "=" + as.getUniqueId(), null);
            Logger.log("MySQLiteHelper","Updated Alert State for id "+Integer.toString(as.getId())+" | Rows Affected: "+Integer.toString(rowsUpdated));
        }

    }

    //Adding a new alert State from an ALERT object
    public void addAlertStateToDatabase(Alert alert) {
                Logger.log("MySQLiteHelper","=== ADDING ALERT STATE TO DB ==");
                AlertState as = new AlertState(alert.getId(), alert.getScheduledFor());

                //Check if alert exists in the database
                if (CheckIsDataAlreadyInDBorNot(TABLE_ALERTSTATE,COLUMN_ALERTSTATE_ID,Integer.toString(alert.getId())))
                {
                    Logger.log("MySQLiteHelper","* Exists in db, updating alert.");
                    //yes it exists, update only the alert state
                    updateAlertState(as);
                }
        else
                {
                    Logger.log("MySQLiteHelper","* Does not exist, inserting");
                    //no it does not, create a new alert (blank alert state)
                    // insertAlert(alert);
                    insertAlertState(new AlertState(alert.getId(),alert.getScheduledFor()));
                    //now create the alert state & update it
                    updateAlertState(as);

                }
    }

    private void insertAlertState(AlertState alertState) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_ALERTSTATE_ID, alertState.getUniqueId());
        insertValues.put(COLUMN_ALERTSTATE_SCHEDULEDFOR, alertState.getScheduledFor());
        insertValues.put(COLUMN_ALERTSTATE_TEXT, alertState.getJson());
        long rows = getWritableDatabase().insert(TABLE_ALERTSTATE, null, insertValues);
        Logger.log("Inserted Alert State " + (alertState.getUniqueId()) + " | Affected: rows" + Long.toString(rows));

    }

    private void insertAlert(Alert alert) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_ID, alert.getId());
        insertValues.put(COLUMN_TEXT, alert.getText());
        insertValues.put(COLUMN_SCHEDULED_FOR, alert.getScheduledFor());
        insertValues.put(COLUMN_ENDING_AT, alert.getEndingAt());
        insertValues.put(COLUMN_POLYGON, alert.GeoLocationToJson());
        insertValues.put(COLUMN_OPTIONS, alert.getOptions());
        insertValues.put(COLUMN_IS_PHONE_VIBRATE,alert.isPhoneExpectedToVibrate());
        insertValues.put(COLUMN_IS_TTS_EXPECTED,alert.isTextToSpeechExpected());
        insertValues.put(COLUMN_IS_GEO_FILTERED,alert.isGeoFiltering());
        long rows = getWritableDatabase().insert(TABLE_ALERTS, null, insertValues);
        Logger.log("Inserted Alert " + Integer.toString(alert.getId()) + " | Affected: rows" + Long.toString(rows));

    }

    //Checking function (Utility)
    //This function returns true if record already exists in db
    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield, String fieldValue) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            return false;
        }
        return true;
    }



}