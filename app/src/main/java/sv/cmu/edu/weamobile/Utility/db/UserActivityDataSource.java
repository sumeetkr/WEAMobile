package sv.cmu.edu.weamobile.utility.db;

import android.content.Context;

import java.util.List;

import sv.cmu.edu.weamobile.data.UserActivity;

/**
 * Created by sumeet on 2/27/15.
 */
public class UserActivityDataSource extends WEADataSource<UserActivity>{

    public UserActivityDataSource(Context context) {
        super(context);
    }

    @Override
    public void insertData(UserActivity data) {

    }

    @Override
    public void insertDataIfNotPresent(UserActivity data) {

    }

    @Override
    public void insertDataItemsIfNotPresent(List<UserActivity> data) {

    }

    @Override
    public UserActivity getData(int id) {
        return null;
    }

    @Override
    public void updateData(UserActivity data) {

    }

    @Override
    public List<UserActivity> getAllData() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }
}
