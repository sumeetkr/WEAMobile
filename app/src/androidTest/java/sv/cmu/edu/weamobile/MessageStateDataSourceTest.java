package sv.cmu.edu.weamobile;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import sv.cmu.edu.weamobile.data.MessageState;
import sv.cmu.edu.weamobile.utility.db.MessageStateDataSource;
import sv.cmu.edu.weamobile.utility.db.WEASQLiteHelper;

/**
 * Created by sumeet on 4/6/15.
 */
public class MessageStateDataSourceTest extends AndroidTestCase {

    private WEASQLiteHelper db;
    private MessageStateDataSource messageStateDataSource;

    public void setUp(){
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_");
        db = new WEASQLiteHelper(context);
        messageStateDataSource = new MessageStateDataSource(getContext(),db);
    }

    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }

    public void testAddMessage(){
        MessageState data = new MessageState(1, "2015-04-05T09:40:57.000Z");
        messageStateDataSource.insertData(data);

        assertTrue(messageStateDataSource.getAllData().size()>0);
    }

    public void testAddTwoMessages(){
        MessageState data = new MessageState(1, "2015-04-05T09:40:57.000Z");
        messageStateDataSource.insertData(data);

        MessageState data2 = new MessageState(2, "2015-03-05T09:40:57.000Z");
        messageStateDataSource.insertData(data2);

        assertTrue(messageStateDataSource.getAllData().size()> 1);
    }

    public void testDuplicateMessages(){
        MessageState data = new MessageState(1, "2015-04-05T09:40:57.000Z");
        messageStateDataSource.insertDataIfNotPresent(data);

        MessageState data2 = new MessageState(1, "2015-04-05T09:40:57.000Z");
        messageStateDataSource.insertDataIfNotPresent(data2);

        assertTrue(messageStateDataSource.getAllData().size() < 2);
    }
}
