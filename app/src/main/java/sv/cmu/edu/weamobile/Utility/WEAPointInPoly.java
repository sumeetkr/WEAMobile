package sv.cmu.edu.weamobile.utility;//30.57,-90.86 30.65,-90.75 30.65,-90.57 30.39,-90.6 30.39,-90.9 30.57,-90.86

import android.location.Location;

import java.util.Arrays;
import java.util.List;

import sv.cmu.edu.weamobile.data.GeoLocation;

public class WEAPointInPoly {
    public static void main(String args[]) {
        double[] lats = {30.57, 30.65, 30.65, 30.39, 30.39, 30,57};
        double[] longs = {-90.86, -90.75, -90.57, -90.6, -90.9, -90.86};
        
        int npol = 5;
        System.out.println("Outside test case:" + pointInPoly(npol, lats, longs, 40.57, -90.86));
        System.out.println("Boundary test case:" + pointInPoly(npol, lats, longs, 30.39, -90.9));
        System.out.println("Inside test case:" + pointInPoly(npol, lats, longs, 30.58, -90.80));
    }

    /**
     * pointInPoly(...): determines whether a point lies within a polygon or not; the method creates an infinite ray horizontally from the test point, and counts the number of edges it crosses. 
     * Params:
     * npol - number of points which define the polygon excluding the repetion of the first point
     * lats - array of latitudes of size (npol + 1)
     * longs - array of longtitudes of size (npol + 1)
     * testLat, testLong - the point to be tested
     *
     * Returns a boolean if (testLat, testLong) lie within the polygon
     */
    public static boolean pointInPoly(int npol, double[] lats, double[] longs, double testLat, double testLong)
    {
        boolean inside = false;
        int i, j;
        for (i = 0, j = npol-1; i < npol; j = i++) {
            if ((((lats[i] <= testLat) && (testLat < lats[j])) ||
                        ((lats[j] <= testLat) && (testLat < lats[i]))) &&
                    (testLong < (longs[j] - longs[i]) * (testLat - lats[i]) / (lats[j] - lats[i]) + longs[i]))
                inside = !inside;
        }
        return inside;
    }

    public static boolean isInPolygon(GeoLocation location, GeoLocation [] polygon) {
        double [] longs = new double[polygon.length];
        double [] lats = new double[polygon.length];

        for(int i = 0; i<polygon.length; i++){
            lats[i]= Double.parseDouble(polygon[i].getLat());
            longs[i] = Double.parseDouble(polygon[i].getLng());
        }

        Logger.log("Verifying presence in polygon.");
        return WEAPointInPoly.pointInPoly(polygon.length, lats, longs, Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng()));
    }

    public static double []  calculatePolyCenter(GeoLocation [] polygon) {
        double [] polyCenter= null;
        try{
            polyCenter = getCentroid(Arrays.asList(polygon));
        }
        catch(Exception ex){
            Logger.log(ex.getMessage());
        }

        return polyCenter;
    }

    public static double getDistanceFromCentroid(GeoLocation myLocation, double[] polyCenter) {
        float center = 0000;
        try{
            float [] results= new float[2];
            Location.distanceBetween(polyCenter[0], polyCenter[1], myLocation.getLatitude(), myLocation.getLongitude(), results);
            center= results[0]/1000;
        }catch(Exception ex){

        }
        return center;
    }

    public static double getDistance(GeoLocation [] polygon, GeoLocation location){
        double distance= getDistanceFromCentroid(
                location,
                calculatePolyCenter(polygon)
        );

        return distance;
    }

    private static double[] getCentroid(List<GeoLocation> points) {
        double[] centroid = { 0.0, 0.0 };

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).getLatitude();
            centroid[1] += points.get(i).getLongitude();
        }

        int totalPoints = points.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return centroid;
    }
}
