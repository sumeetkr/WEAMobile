package sv.cmu.edu.weamobile;

import android.content.ContentValues;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.db.LocationDataSource;
import sv.cmu.edu.weamobile.utility.db.WEASQLiteHelper;

/**
 * Created by harshalkutkar on 2/26/15.
*/
public class DatabaseTest extends AndroidTestCase {
    private WEASQLiteHelper db;

    public void setUp(){
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_");
        db = new WEASQLiteHelper(context);
    }

    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }

    public void testAddLocation(){
        GeoLocation location = new GeoLocation("22.2222","127.222", (float)1.0000);

        ContentValues insertValues = new ContentValues();
        insertValues.put(LocationDataSource.FIELD_LAT,location.getLatitude() );
        insertValues.put(LocationDataSource.FIELD_LNG,location.getLongitude() );
        insertValues.put(LocationDataSource.ACCURACY, Double.valueOf(location.getAccuracy()));

        long rows = db.getWritableDatabase().insert(LocationDataSource.LOCATION_TABLE, null, insertValues);
        assertTrue(rows > 0);
    }

    /* Test to check if Database Entry can be done. [Alert State Table] */
    public void testAddSampleAlertStateEntry(){

        ContentValues insertValues = new ContentValues();
        insertValues.put(WEASQLiteHelper.COLUMN_ALERTSTATE_SCHEDULEDFOR,"2015-02-25T23:03:59.000Z" );
        insertValues.put(WEASQLiteHelper.COLUMN_ALERTSTATE_TEXT,"{\"scheduledFor\":\"2015-02-25T23:03:59.000Z\",\"id\":1101,\"timeWhenFeedbackGivenInEpoch\":0,\"timeWhenShownToUserInEpoch\":0,\"isAlreadyShown\":false,\"isFeedbackGiven\":false,\"isInPolygonOrAlertNotGeoTargeted\":false}");
        long rows = db.getWritableDatabase().insert(WEASQLiteHelper.TABLE_ALERTSTATE, null, insertValues);
        assertTrue(rows > 0);
    }

    /* Test to check if Database Entry can be done. [Alerts Table] */
    public void testAddSampleAlertEntry(){
        ContentValues insertValues = new ContentValues();
        insertValues.put(WEASQLiteHelper.COLUMN_SCHEDULED_FOR,"2015-02-25T23:03:59.000Z" );
        insertValues.put(WEASQLiteHelper.COLUMN_TEXT,"THIS HAZARDOUS WEATHER OUTLOOK IS FOR WESTERN MARYLAND...EAST\n" +
                "CENTRAL OHIO...NORTHWEST PENNSYLVANIA...SOUTHWEST\n" +
                "PENNSYLVANIA...WEST CENTRAL PENNSYLVANIA...NORTHERN WEST VIRGINIA\n" +
                "AND NORTHERN PANHANDLE OF WEST VIRGINIA.");
        insertValues.put(WEASQLiteHelper.COLUMN_ENDING_AT,"2015-02-25T23:04:05.000Z");
        insertValues.put(WEASQLiteHelper.COLUMN_POLYGON,"{\"polygon\": [\n" +
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
        insertValues.put(WEASQLiteHelper.COLUMN_OPTIONS,1);
        insertValues.put(WEASQLiteHelper.COLUMN_IS_PHONE_VIBRATE,1);
        insertValues.put(WEASQLiteHelper.COLUMN_IS_TTS_EXPECTED,1);
        insertValues.put(WEASQLiteHelper.COLUMN_IS_GEO_FILTERED,1);
        long rows = db.getWritableDatabase().insert(WEASQLiteHelper.TABLE_ALERTS, null, insertValues);
        Log.d("TEST","Running Test for adding data to db. Rows Affected:"+Long.toString(rows));
        assertTrue(rows > 0);
    }
}