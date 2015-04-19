package sv.cmu.edu.weamobile.utility.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.data.MessageState;
import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 4/6/15.
 */
public class MessageStateDataSource extends WEADataSource<MessageState> {
//    private boolean isAlreadyShown = false;
//    private boolean isFeedbackGiven = false;
//    private long timeWhenShownToUserInEpoch;
//    private long timeWhenFeedbackGivenInEpoch;
//    private String latWhenShown;
//    private String lngWhenShown;
//    private float accuracyWhenShown = 0.00f;
//    private boolean isToBeShown = false;

    public static final String MESSAGE_STATE_TABLE = "MessageState";

    public static final String COLUMN_ROW_ID = "_id";
    public static final String COLUMN_SCHEDULED_FOR = "scheduledFor";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_IS_ALREADY_SHOWN = "isAlreadyShown";
    public static final String COLUMN_IS_FEEDBACK_GIVEN = "isFeedbackGiven";
    public static final String COLUMN_TIME_WHEN_SHOWN_TO_USER_IN_EPOCH = "timeWhenShownToUserInEpoch";
    public static final String COLUMN_TIME_WHEN_FEEDBACK_GIVEN_IN_EPOCH = "timeWhenFeedbackGivenInEpoch";
    public static final String COLUMN_LAT_WHEN_SHOWN = "latWhenShown";
    public static final String COLUMN_LNG_WHEN_SHOWN = "lngWhenShown";
    public static final String COLUMN_ACCURACY_WHEN_SHOWN = "accuracyWhenShown";
    public static final String COLUMN_IS_IN_POLYGON_OR_MESSAGE_NOT_GEO_TARGETED = "isToBeShown";
//    private static final String COLUMN_ISisAlertToBeShown = false;
    private static final String COLUMN_IS_IN_POLYGON = "isInPolygon";
    private static final String COLUMN_IS_LOCATION_HISTORY_IN_POLYGON = "isLocationHistoryInPolygon";
    private static final String COLUMN_IS_FUTURE_LOCATION_IN_POLYGON = "isFutureLocationInPolygon";
    private static final String COLUMN_IS_GEO_TARGETED = "isNotGeoTargeted";


    public final static String CREATE_MESSAGE_TABLE_SQL =    "create table " + MESSAGE_STATE_TABLE + " ( " +
            COLUMN_ROW_ID + " integer primary key autoincrement , " +
            COLUMN_MESSAGE_ID + " integer ," +
            COLUMN_SCHEDULED_FOR + " text ," +
            COLUMN_IS_ALREADY_SHOWN + " integer ," +
            COLUMN_IS_FEEDBACK_GIVEN + " integer ," +
            COLUMN_TIME_WHEN_SHOWN_TO_USER_IN_EPOCH + " text ," +
            COLUMN_TIME_WHEN_FEEDBACK_GIVEN_IN_EPOCH + " text ," +
            COLUMN_LAT_WHEN_SHOWN + " text ," +
            COLUMN_LNG_WHEN_SHOWN + " text ," +
            COLUMN_ACCURACY_WHEN_SHOWN + " text ," +
            COLUMN_IS_IN_POLYGON_OR_MESSAGE_NOT_GEO_TARGETED + " integer ," +
            COLUMN_IS_IN_POLYGON + " integer ," +
            COLUMN_IS_LOCATION_HISTORY_IN_POLYGON + " integer ," +
            COLUMN_IS_FUTURE_LOCATION_IN_POLYGON + " integer ," +
            COLUMN_IS_GEO_TARGETED + " integer ," +
            "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP "+
            " ) ";

    public MessageStateDataSource(Context context) {
        super(context);
    }

    public MessageStateDataSource(Context context, WEASQLiteHelper helper) {
        super(context, helper);
    }

    @Override
    public void insertData(MessageState messageState) {

        ContentValues insertValues = fillValues(messageState);

        Long rows = insert(insertValues);

        Logger.log("Inserted Message State " + (messageState.getUniqueId()));
        Logger.log("Inserted Message id " + (messageState.getId()));
        Logger.log(insertValues.toString());
        Logger.log("Row updated " + rows);
    }

    private ContentValues fillValues(MessageState messageState) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_MESSAGE_ID, messageState.getId());
        insertValues.put(COLUMN_SCHEDULED_FOR, messageState.getScheduledFor());
        insertValues.put(COLUMN_IS_ALREADY_SHOWN, messageState.isAlreadyShown());
        insertValues.put(COLUMN_IS_FEEDBACK_GIVEN, messageState.isFeedbackGiven());
        insertValues.put(COLUMN_TIME_WHEN_SHOWN_TO_USER_IN_EPOCH, messageState.getTimeWhenShownToUserInEpoch());
        insertValues.put(COLUMN_TIME_WHEN_FEEDBACK_GIVEN_IN_EPOCH, messageState.getTimeWhenFeedbackGivenInEpoch());

        if(messageState.getLocationWhenShown()!= null &&
                messageState.getLocationWhenShown().getLat()!= null &&
                messageState.getLocationWhenShown().getLng()!= null){
            insertValues.put(COLUMN_LAT_WHEN_SHOWN, messageState.getLocationWhenShown().getLat());
            insertValues.put(COLUMN_LNG_WHEN_SHOWN, messageState.getLocationWhenShown().getLng());
            insertValues.put(COLUMN_ACCURACY_WHEN_SHOWN, messageState.getLocationWhenShown().getAccuracy());
        }

        insertValues.put(COLUMN_IS_IN_POLYGON_OR_MESSAGE_NOT_GEO_TARGETED, messageState.isToBeShown());
        insertValues.put(COLUMN_IS_IN_POLYGON, messageState.isInPolygon());
        insertValues.put(COLUMN_IS_LOCATION_HISTORY_IN_POLYGON, messageState.isLocationHistoryInPolygon());
        insertValues.put(COLUMN_IS_FUTURE_LOCATION_IN_POLYGON, messageState.isFutureLocationInPolygon());
        insertValues.put(COLUMN_IS_GEO_TARGETED, messageState.isNotGeoTargeted());
        return insertValues;
    }

    @Override
    public void insertDataIfNotPresent(MessageState data) {
        boolean found = false;

        try{
            open();
            Cursor cursor = database.rawQuery("select * from "+ MESSAGE_STATE_TABLE +" where " + COLUMN_MESSAGE_ID + "=" + data.getId(), null);
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    int messageId = Integer.valueOf(cursor.getString(1));
                    String scheduledFor = cursor.getString(2);

                    if(messageId == data.getId() && scheduledFor.compareTo(data.getScheduledFor())==0){
                        found = true;
                    }
                    cursor.moveToNext();
                }
            }
        }catch (Exception ex){
            Logger.log(ex.getLocalizedMessage());
        }finally {
            close();
        }

        if(!found){
            insertData(data);
        }else{
            Logger.log(data.getId() + " was already in database, not creating again");
        }
    }

    @Override
    public void insertDataItemsIfNotPresent(List<MessageState> data) {

    }

    @Override
    public MessageState getData(int id) {
        MessageState messageState = null;
        try{
            open();
            Cursor cursor = database.rawQuery("select * from "+ MESSAGE_STATE_TABLE +" where " + COLUMN_MESSAGE_ID + "=" +id, null);
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    int messageId = Integer.valueOf(cursor.getString(1));
                    String scheduledFor = cursor.getString(2);

                    MessageState state = new MessageState(messageId, scheduledFor);
                    state.setAlreadyShown(cursor.getInt(3)>0?true:false);
                    state.setFeedbackGiven(cursor.getInt(4) > 0 ? true : false);
                    state.setTimeWhenFeedbackGivenInEpoch(Long.parseLong(cursor.getString(5)));
                    state.setTimeWhenFeedbackGivenInEpoch(Long.parseLong(cursor.getString(6)));

                    state.setLocationWhenShown(new GeoLocation(cursor.getString(7),
                            cursor.getString(8),
                            cursor.getFloat(9) ));

                    state.setIsToBeShown(cursor.getInt(10) > 0 ? true : false);

                    state.setInPolygon(cursor.getInt(11) > 0 ? true : false);
                    state.setLocationHistoryInPolygon(cursor.getInt(12) > 0 ? true : false);
                    state.setFutureLocationInPolygon(cursor.getInt(13) > 0 ? true : false);
                    state.setNotGeoTargeted(cursor.getInt(14) > 0 ? true : false);

                    messageState = state;
//                    Logger.log("Retrieved messageState " + state.getUniqueId());
                    cursor.moveToNext();
                }
            }
        }catch (Exception ex){
            Logger.log(ex.getLocalizedMessage());
        }finally {
            close();
        }

        if(messageState != null){
            Logger.log("Retrieved message sate with id " + messageState.getUniqueId());
            Logger.log("Retrieved message sate with json " + messageState.getJson());
        }else{
            Logger.log("Could not retrieve message sates with id " + id);
        }

        return  messageState;
    }

    @Override
    public void updateData(MessageState messageState) {
        if (messageState != null) {
            try{
                open();

                ContentValues values = fillValues(messageState);
                String strFilter = COLUMN_MESSAGE_ID +"=" + messageState.getId();
                database.update(MESSAGE_STATE_TABLE, values, strFilter, null);

            }catch (Exception ex){
                Logger.log(ex.getLocalizedMessage());
            }finally {
                close();
            }

            Logger.log("Updated message sates with id " + messageState.getUniqueId());
            Logger.log(messageState.getJson());
        }
    }

    @Override
    public void deleteData(MessageState data) {

    }

    @Override
    public List<MessageState> getAllData() {
        List<MessageState> messageStates = new ArrayList<MessageState>();
        try{
            open();
            Cursor cursor = database.rawQuery("select * from "+ MESSAGE_STATE_TABLE, null);
            if (cursor .moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    int id = Integer.valueOf(cursor.getString(1));
                    String scheduledFor = cursor.getString(2);

                    MessageState state = new MessageState(id, scheduledFor);
                    state.setAlreadyShown(cursor.getInt(3)>0?true:false);
                    state.setFeedbackGiven(cursor.getInt(4) > 0 ? true : false);
                    state.setTimeWhenFeedbackGivenInEpoch(Long.parseLong(cursor.getString(5)));
                    state.setTimeWhenFeedbackGivenInEpoch(Long.parseLong(cursor.getString(6)));

//                    double lat = Double.valueOf(cursor.getDouble(7));
//                    double lng = Double.valueOf(cursor.getDouble(8));
//                    float accuracy = Float.valueOf(cursor.getFloat(9));
                    state.setLocationWhenShown(new GeoLocation(cursor.getString(7), cursor.getString(7),cursor.getFloat(9) ));

                    state.setIsToBeShown(cursor.getInt(10) > 0 ? true : false);

                    state.setInPolygon(cursor.getInt(11) > 0 ? true : false);
                    state.setLocationHistoryInPolygon(cursor.getInt(12) > 0 ? true : false);
                    state.setFutureLocationInPolygon(cursor.getInt(13) > 0 ? true : false);
                    state.setNotGeoTargeted(cursor.getInt(14) > 0 ? true : false);

                    messageStates.add(state);
                    cursor.moveToNext();
                }
            }
        }catch (Exception ex){
            Logger.log(ex.getLocalizedMessage());
        }finally {
            close();
        }

        Logger.log("No of messages states in database " + messageStates.size());
        return  messageStates;
    }

    @Override
    public String getTableName() {
        return MESSAGE_STATE_TABLE;
    }
}

