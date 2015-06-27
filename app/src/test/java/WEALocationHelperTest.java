/**
 * Created by sumeet on 6/25/15.
 */

import org.junit.Test;

import java.io.FileReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.WEALocationHelper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WEALocationHelperTest {

    @Test
    public void testAddLocation(){
        double[] lats = {30.57, 30.65, 30.65, 30.39, 30.39, 30,57};
        double[] longs = {-90.86, -90.75, -90.57, -90.6, -90.9, -90.86};

        int npol = 5;
        assertFalse(WEALocationHelper.pointInPoly(npol, lats, longs, 40.57, -90.86));
        assertTrue(WEALocationHelper.pointInPoly(npol, lats, longs, 30.39, -90.9));
        assertTrue(WEALocationHelper.pointInPoly(npol, lats, longs, 30.58, -90.80));
    }

    @Test
    public void testLoadData(){
        try {
            CSVReader reader = new CSVReader(new FileReader("/Users/sumeet/Google Drive/PhD/Research/WEA/db_backups/query_result.csv"));
            List myEntries = reader.readAll();
            double [] distances = new double[myEntries.size()-1];

            //convert 6th entry to lats
            //convert 7th entry to lngs
            //convert 4th entry to lat (double)
            //convert 5th entry to lng (double)

            for(int i =1; i<myEntries.size(); i++){
                String [] entry = (String[]) myEntries.get(i);
//                System.out.println("Entry no " + i);
//                System.out.println(Arrays.toString(entry));

                float accuracy = Float.valueOf(entry[6]);
                GeoLocation point = new GeoLocation(entry[2].replace("\"","").replace(",", ""), entry[3].replace("\"", "").replace(",",""), accuracy);


                String lats = entry[4];
                String [] latSplits = lats.split(",");
                double [] latsDouble = new double[latSplits.length];
                for (int j=0;j<latSplits.length; j++) {
                    latsDouble[j] = Double.parseDouble(latSplits[j]);
                }


                String lngs = entry[5];
                String [] lngSplits = lngs.split(",");
                double [] lngsDouble = new double[lngSplits.length];
                for (int j=0;j<lngSplits.length; j++) {
                    lngsDouble[j] = Double.parseDouble(lngSplits[j]);
                }

                GeoLocation [] polygonLocation = new GeoLocation[latSplits.length];
                for(int j=0; j<latSplits.length; j++){
                    polygonLocation[j] = new GeoLocation(latSplits[j],lngSplits[j], j);
                }

                double distance = WEALocationHelper.getDistance(polygonLocation,point);
                distances[i-1] = distance;
//                System.out.println(distance);

                double avgRadius = WEALocationHelper.getPolygonAverageRadius(polygonLocation);
//                System.out.println(avgRadius);

                double nearestDistance = WEALocationHelper.getNearestDistance(polygonLocation, point);
                System.out.println(nearestDistance);
            }
            assertTrue(myEntries.size()>0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDistance(){
        //public static double getDistance(GeoLocation [] polygon, GeoLocation location){

    }

}
