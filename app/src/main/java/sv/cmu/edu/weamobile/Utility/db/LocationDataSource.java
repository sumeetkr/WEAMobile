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
    public static final String PRIMARY_ACTIVITY_TYPE = "primary_activity_type";
    public static final String PRIMARY_ACTIVITY_CONFIDENCE = "primary_activity_confidence";
    public static final String SECONDARY_ACTIVITY_TYPE = "secondary_activity_type";
    public static final String SECONDARY_ACTIVITY_CONFIDENCE = "secondary_activity_confidence";

    /** A constant, stores the the table name */
    public static final String LOCATION_TABLE = "Location";

    public final static String CREATE_LOCATION_TABLE_SQL =    "create table " + LOCATION_TABLE + " ( " +
            FIELD_ROW_ID + " integer primary key autoincrement , " +
            FIELD_LAT + " double , " +
            FIELD_LNG + " double , " +
            ACCURACY + " double ," +
            PRIMARY_ACTIVITY_TYPE + " integer ," +
            PRIMARY_ACTIVITY_CONFIDENCE + " integer ," +
            SECONDARY_ACTIVITY_TYPE + " integer ," +
            SECONDARY_ACTIVITY_CONFIDENCE + " integer ," +
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
        insertValues.put(FIELD_LAT, data.getLatitude() );
        insertValues.put(FIELD_LNG, data.getLongitude() );
        insertValues.put(ACCURACY, Double.valueOf(data.getAccuracy()));
        insertValues.put(PRIMARY_ACTIVITY_TYPE, data.getActivityType());
        insertValues.put(PRIMARY_ACTIVITY_CONFIDENCE, data.getActivityConfidence());
        insertValues.put(SECONDARY_ACTIVITY_TYPE, data.getSecondaryActivityType());
        insertValues.put(SECONDARY_ACTIVITY_CONFIDENCE,data.getSecondaryActivityConfidence());
        insert(insertValues);
    }

    @Override
    public void insertDataIfNotPresent(GeoLocation data) {

    }

    @Override
    public void insertDataItemsIfNotPresent(List<GeoLocation> data) {

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
    public void deleteData(GeoLocation data) {

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
                            Integer.valueOf(cursor.getString(4)),
                            Integer.valueOf(cursor.getInt(5)),
                            Integer.valueOf(cursor.getString(6)),
                            Integer.valueOf(cursor.getInt(7)),
                            Timestamp.valueOf(cursor.getString(8)));

                    locations.add(location);
                    cursor.moveToNext();
                }

//                For accuracy changes
//                                            double totalAccuracy = 0.0;
//                                            double avgAccuracy =100.0;// in metres
//                                            for(GeoLocation location :rawLocations){
//                                                totalAccuracy += location.getAccuracy();
//                                            }
//                                            if(rawLocations.size()>1){
//                                                avgAccuracy = totalAccuracy/rawLocations.size();
//                                            }

//                                            List<LatLng> historyPoints = new ArrayList<LatLng>();
//                                            for(GeoLocation location :rawLocations){
//                                                // Remove out liars
////                                                if(location.getAccuracy() < 2*avgAccuracy){
//                                                    historyPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
////                                                }
//                                            }

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
