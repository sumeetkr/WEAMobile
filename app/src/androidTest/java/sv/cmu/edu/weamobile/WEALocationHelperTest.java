package sv.cmu.edu.weamobile;

import android.location.Location;
import android.test.AndroidTestCase;

import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEALocationHelper;

/**
 * Created by sumeet on 3/19/15.
 */
public class WEALocationHelperTest extends AndroidTestCase{

    public void setUp(){
    }

    public void tearDown() throws Exception{
        super.tearDown();
    }

    public void testAddLocation(){
        double[] lats = {30.57, 30.65, 30.65, 30.39, 30.39, 30,57};
        double[] longs = {-90.86, -90.75, -90.57, -90.6, -90.9, -90.86};

        int npol = 5;

        assertFalse(WEALocationHelper.pointInPoly(npol, lats, longs, 40.57, -90.86));
        assertTrue(WEALocationHelper.pointInPoly(npol, lats, longs, 30.39, -90.9));
        assertTrue(WEALocationHelper.pointInPoly(npol, lats, longs, 30.58, -90.80));
    }

    public void testGetFutureLocation(){
        //        40.4443954, -79.9454169
        //        40.4427384, -79.9429012
        //        40.4412782, -79.9434841

        Location loc1 = WEALocationHelper.getLocationFromCoordinates(40.4443954, -79.9454169);
        Location loc2 = WEALocationHelper.getLocationFromCoordinates(40.4427384, -79.9429012);
        Location loc3 = WEALocationHelper.getLocationFromCoordinates(40.4412782, -79.9434841);

        double heading = WEALocationHelper.getCurrentHeading(loc1, loc3);
        Location futureLocation = WEALocationHelper.getFutureLocation(loc3, heading, 100, 5*60);
        Logger.log(String.valueOf(futureLocation.getLatitude()));
        Logger.log(String.valueOf(futureLocation.getLongitude()));

        assertEquals(futureLocation.getLatitude(),40.3324262, .0001);
        assertEquals(futureLocation.getLongitude(),-79.86398623, .0001);
    }

    public void testGetFutureLocation2(){
        //        40.4443954, -79.9454169
        //        40.4427384, -79.9429012
        //        40.4412782, -79.9434841

        Location loc1 = WEALocationHelper.getLocationFromCoordinates(40.4443954, -79.9454169);
        Location loc2 = WEALocationHelper.getLocationFromCoordinates(40.4427384, -79.9429012);
        Location loc3 = WEALocationHelper.getLocationFromCoordinates(40.4412782, -79.9434841);

        double speed = WEALocationHelper.getSpeedMPH(loc2, loc3, 5*60);
        double heading = WEALocationHelper.getCurrentHeading(loc1, loc3);
        Location futureLocation = WEALocationHelper.getFutureLocation(loc3, heading, speed, 5*60);
        Logger.log(String.valueOf(futureLocation.getLatitude()));
        Logger.log(String.valueOf(futureLocation.getLongitude()));

        assertEquals(futureLocation.getLatitude(),40.4399022, .0001);
        assertEquals(futureLocation.getLongitude(), -79.94247923, .0001);
        //40.4399022,-79.94247923
    }
}
