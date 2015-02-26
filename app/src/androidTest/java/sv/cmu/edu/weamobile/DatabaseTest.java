package sv.cmu.edu.weamobile;

import android.content.ContentValues;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import java.util.Random;

import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.db.MySQLiteHelper;

/**
 * Created by harshalkutkar on 2/26/15.
*/
public class DatabaseTest extends AndroidTestCase {
    private MySQLiteHelper db;

    public void setUp(){
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_");
        db = new MySQLiteHelper(context);
    }


    /* Test to check if Database Entry can be done. [Alert State Table] */
    public void testAddSampleAlertStateEntry(){

        ContentValues insertValues = new ContentValues();
        Random r = new Random();
        insertValues.put(MySQLiteHelper.COLUMN_ALERTSTATE_ID, String.format("%14d", r.nextInt(1000000000)));
        insertValues.put(MySQLiteHelper.COLUMN_ALERTSTATE_SCHEDULEDFOR,"2015-02-25T23:03:59.000Z" );
        insertValues.put(MySQLiteHelper.COLUMN_ALERTSTATE_TEXT,"{\"scheduledFor\":\"2015-02-25T23:03:59.000Z\",\"id\":1101,\"timeWhenFeedbackGivenInEpoch\":0,\"timeWhenShownToUserInEpoch\":0,\"isAlreadyShown\":false,\"isFeedbackGiven\":false,\"isInPolygonOrAlertNotGeoTargeted\":false}");
        long rows = db.getWritableDatabase().insert(MySQLiteHelper.TABLE_ALERTSTATE, null, insertValues);
        assertTrue(rows > 0);

    }

    /* Test to check if Database Entry can be done. [Alerts Table] */
    public void testAddSampleAlertEntry(){

        ContentValues insertValues = new ContentValues();
        insertValues.put(MySQLiteHelper.COLUMN_ID,String.format("%14d", r.nextInt(1000000000)));
        insertValues.put(MySQLiteHelper.COLUMN_SCHEDULED_FOR,"2015-02-25T23:03:59.000Z" );
        insertValues.put(MySQLiteHelper.COLUMN_TEXT,"THIS HAZARDOUS WEATHER OUTLOOK IS FOR WESTERN MARYLAND...EAST\n" +
                "CENTRAL OHIO...NORTHWEST PENNSYLVANIA...SOUTHWEST\n" +
                "PENNSYLVANIA...WEST CENTRAL PENNSYLVANIA...NORTHERN WEST VIRGINIA\n" +
                "AND NORTHERN PANHANDLE OF WEST VIRGINIA.");
        insertValues.put(MySQLiteHelper.COLUMN_ENDING_AT,"2015-02-25T23:04:05.000Z");
        insertValues.put(MySQLiteHelper.COLUMN_POLYGON,"{\"polygon\": [\n" +
                "    {\n" +
                "    \"lng\": \"-122.02078729751275\",\n" +
                "    \"lat\": \"37.40305151883054\",\n" +
                "    \"id\": 5391\n" +
                "    },\n" +
                "    {\n" +
                "    \"lng\": \"-122.02052980544734\",\n" +
                "    \"lat\": \"37.404023111407156\",\n" +
                "    \"id\": 5401\n" +
                "    },\n" +
                "    {\n" +
                "    \"lng\": \"-122.01563745620417\",\n" +
                "    \"lat\": \"37.40315379232662\",\n" +
                "    \"id\": 5411\n" +
                "    },\n" +
                "    {\n" +
                "    \"lng\": \"-122.02213913085627\",\n" +
                "    \"lat\": \"37.39914797606258\",\n" +
                "    \"id\": 5421\n" +
                "    }\n" +
                "    ]\n" +
                "}");
        insertValues.put(MySQLiteHelper.COLUMN_OPTIONS,1);
        insertValues.put(MySQLiteHelper.COLUMN_IS_PHONE_VIBRATE,1);
        insertValues.put(MySQLiteHelper.COLUMN_IS_TTS_EXPECTED,1);
        insertValues.put(MySQLiteHelper.COLUMN_IS_GEO_FILTERED,1);
        long rows = db.getWritableDatabase().insert(MySQLiteHelper.TABLE_ALERTS, null, insertValues);
        Log.d("TEST","Running Test for adding data to db. Rows Affected:"+Long.toString(rows));
        assertTrue(rows > 0);



    }



    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }
}