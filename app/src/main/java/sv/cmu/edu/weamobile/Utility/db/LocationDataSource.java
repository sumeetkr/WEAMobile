package sv.cmu.edu.weamobile.utility.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 2/27/15.
 */
public class LocationDataSource extends WEADataSource<GeoLocation> {

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table locations, stores the latitude */
    public static final String FIELD_LAT = "lat";

    /** Field 3 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    public static final String ACCURACY = "accuracy";

    /** A constant, stores the the table name */
    public static final String LOCATION_TABLE = "Location";

    public final static String CREATE_LOCATION_TABLE_SQL =    "create table " + LOCATION_TABLE + " ( " +
            FIELD_ROW_ID + " integer primary key autoincrement , " +
            FIELD_LAT + " double , " +
            FIELD_LNG + " double , " +
            ACCURACY + " double ," +
            "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP "+
            " ) ";

    public LocationDataSource(Context context){
        super(context);
    }

    public LocationDataSource(Context context, WEASQLiteHelper helper) {
        super(context, helper);
    }

    @Override
    public void insertData(GeoLocation data) {

        ContentValues insertValues = new ContentValues();
        insertValues.put(FIELD_LAT,data.getLatitude() );
        insertValues.put(FIELD_LNG,data.getLongitude() );
        insertValues.put(ACCURACY, Double.valueOf(data.getAccuracy()));

        insert(insertValues);
    }

    @Override
    public GeoLocation getData(int id) {
        //not yet implemented
        return null;
    }

    @Override
    public void updateData(GeoLocation data) {

    }

    @Override
    public List<GeoLocation> getAllData() {

        List<GeoLocation> locations = new ArrayList<GeoLocation>();
        try{
            open();
            Cursor cursor = database.rawQuery("select * from "+ LOCATION_TABLE, null);
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    GeoLocation location = new GeoLocation(String.valueOf(cursor.getDouble(1)),
                            String.valueOf(cursor.getDouble(2)),
                            (float) cursor.getFloat(3),
                            Timestamp.valueOf(cursor.getString(4)));

                    locations.add(location);
                    cursor.moveToNext();
                }
            }
        }catch (Exception ex){
            Logger.log(ex.getLocalizedMessage());
        }finally {
            close();
        }
        return  locations;
    }

    @Override
    public String getTableName() {
        return LOCATION_TABLE;
    }
}
