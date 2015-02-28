package sv.cmu.edu.weamobile.utility.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 2/27/15.
 */
public abstract class WEADataSource<T> {

    protected SQLiteDatabase database;
    protected WEASQLiteHelper dbHelper;

    public WEADataSource(Context context) {
        dbHelper = new WEASQLiteHelper(context);
    }

    public WEADataSource(Context context, WEASQLiteHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    protected void close() {
        dbHelper.close();
    }

    protected long insert(ContentValues insertValues ){
        long noOfRows =0;
        try{
            open();
            noOfRows = database.insert(getTableName(), null, insertValues);
        }catch (Exception ex){
            Logger.log(ex.getMessage());
        }finally {
            close();
        }

        return  noOfRows;
    }

    public abstract void insertData(T data);

    public abstract T getData(int id);

    public abstract void updateData(T data);

    public abstract List<T> getAllData();

    public abstract String getTableName();

}
