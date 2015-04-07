package sv.cmu.edu.weamobile.utility.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 4/6/15.
 */
public class MessageDataSource extends WEADataSource<Message> {
    public static final String MESSAGE_TABLE = "Message";

    public static final String FIELD_ROW_ID = "_id";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_MESSAGE_JSON = "messageJson";

    public final static String CREATE_MESSAGE_TABLE_SQL =    "create table " + MESSAGE_TABLE + " ( " +
            FIELD_ROW_ID + " integer primary key autoincrement , " +
            COLUMN_MESSAGE_ID + " integer ," +
            COLUMN_MESSAGE_JSON + " text, " +
            "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP "+
            " ) ";

    public MessageDataSource(Context context) {
        super(context);
    }

    public MessageDataSource(Context context, WEASQLiteHelper helper) {
        super(context, helper);
    }

    @Override
    public void insertData(Message data) {

        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_MESSAGE_ID, data.getId());
        insertValues.put(COLUMN_MESSAGE_JSON, data.getJson());
        long row = insert(insertValues);

        Logger.log("No of rows updated " + row);
    }

    @Override
    public void insertDataIfNotPresent(Message data) {
        boolean found = false;
        for(Message message : getAllData()){
            if(message.getId() == data.getId()){
                found = true;
                break;
            }
        }

        if(!found){
            insertData(data);
        }
    }

    @Override
    public void insertDataItemsIfNotPresent(List<Message> dataItems) {

        List<Message> existingMessages = getAllData();

        for(Message dataItem : dataItems){
            boolean found = false;
            for(Message message : existingMessages){
                if(message.getId() == dataItem.getId()){
                    found = true;
                    break;
                }
            }

            if(!found){
                insertData(dataItem);
            }
        }
    }

    @Override
    public Message getData(int id) {
        Message message = null;
        try{
            open();
            Cursor cursor = database.rawQuery("select * from "+ MESSAGE_TABLE +" where " + COLUMN_MESSAGE_ID + "=" +id, null);
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String json = String.valueOf(cursor.getString(2));
                    message = Message.fromJson(json);
                    cursor.moveToNext();
                }
            }
        }catch (Exception ex){
            Logger.log(ex.getLocalizedMessage());
        }finally {
            close();
        }

        return message;
    }

    @Override
    public void updateData(Message data) {

    }

    @Override
    public void deleteData(Message data) {

    }

    @Override
    public List<Message> getAllData() {
        List<Message> messages = new ArrayList<Message>();
        try{
            open();
            Cursor cursor = database.rawQuery("select * from "+ MESSAGE_TABLE, null);
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String json = String.valueOf(cursor.getString(2));
                    messages.add(Message.fromJson(json));
                    cursor.moveToNext();
                }
            }
        }catch (Exception ex){
            Logger.log(ex.getLocalizedMessage());
        }finally {
            close();
        }

        Logger.log("No of messages in database " + messages.size());
        return  messages;
    }

    @Override
    public String getTableName() {
        return MESSAGE_TABLE;
    }
}
