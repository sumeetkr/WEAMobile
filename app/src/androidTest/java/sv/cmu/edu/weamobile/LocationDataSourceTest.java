package sv.cmu.edu.weamobile;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.List;

import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.db.LocationDataSource;
import sv.cmu.edu.weamobile.utility.db.WEASQLiteHelper;

/**
 * Created by sumeet on 2/27/15.
 */
public class LocationDataSourceTest extends AndroidTestCase {
    private WEASQLiteHelper db;
    private LocationDataSource locationDataSource;

    public void setUp(){
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_");
        db = new WEASQLiteHelper(context);
        locationDataSource = new LocationDataSource(getContext(),db);
    }

    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }


    public void testAddGeoLocation(){
        GeoLocation location = new GeoLocation("22.2222","127.222", (float)1.0000);
        locationDataSource.insertData(location);

        assertTrue(locationDataSource.getAllData().size()>0);
    }

    public void testAddTwoGeoLocation(){
        GeoLocation location = new GeoLocation("22.2222","127.222", (float)1.0000);
        locationDataSource.insertData(location);

        GeoLocation location2 = new GeoLocation("22.2222","127.222", (float)1.0000);
        locationDataSource.insertData(location2);

        assertTrue(locationDataSource.getAllData().size()>1);
    }

    public void testAddTwoGeoLocationAndVerify(){
        GeoLocation location = new GeoLocation("22.2222","127.221", (float)1.0000);
        locationDataSource.insertData(location);

        GeoLocation location2 = new GeoLocation("22.2223","127.222", (float)2.0000);
        locationDataSource.insertData(location2);

        List<GeoLocation> locations = locationDataSource.getAllData();

        assertEquals(locations.get(0).getLat().compareTo("22.2222"),0);
        assertEquals(locations.get(0).getLng(), "127.221");

        assertEquals(locations.get(1).getLat(), "22.2223");
        assertEquals(locations.get(1).getLng(), "127.222");
    }

    public void testGetAllData(){
        GeoLocation location = new GeoLocation("22.2222","127.221", (float)1.0000);
        locationDataSource.insertData(location);

        GeoLocation location2 = new GeoLocation("22.2223","127.222", (float)2.0000);
        locationDataSource.insertData(location2);

        List<GeoLocation> locations = locationDataSource.getAllData();
        assertTrue(locationDataSource.getAllData().size()==2);
    }
}

