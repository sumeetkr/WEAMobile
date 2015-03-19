package sv.cmu.edu.weamobile.utility;//30.57,-90.86 30.65,-90.75 30.65,-90.57 30.39,-90.6 30.39,-90.9 30.57,-90.86

import android.location.Location;

import java.util.Arrays;
import java.util.List;

import sv.cmu.edu.weamobile.data.GeoLocation;

public class WEALocationHelper {

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
        return WEALocationHelper.pointInPoly(polygon.length, lats, longs, Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng()));
    }

    public static  boolean areAnyPointsInPolygon(List<GeoLocation> locations, GeoLocation [] polygon){
        boolean pointsInPolygon = false;

        for(GeoLocation location : locations){
            if(isInPolygon(location, polygon)){
                pointsInPolygon = true;
                break;
            }
        }

        return  pointsInPolygon;
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

    public static Location getLocationFromCoordinates(double latitude, double longitude){
        Location location = new Location(Constants.WEA_GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return  location;
    }

    public static float getCurrentHeading(Location locationFrom, Location locationTo){
        return locationFrom.bearingTo(locationTo);
    }

    public static double getSpeedMPH(Location locationFrom, Location locationTo, double timeInSeconds){
        double distanceInMiles = locationFrom.distanceTo(locationTo)*0.000621371;
        if(timeInSeconds==0) timeInSeconds =1;
        return distanceInMiles*60*60/timeInSeconds;
    }

    public static Location getFutureLocation(Location currentLocation,
                                            double heading,
                                            double speedMph,
                                            double durationInSeconds)
    {
        double earthRadius= 3964.037911746;
        double pi = Math.PI;
        double x = speedMph * Math.sin(heading * pi / 180) * durationInSeconds/ 3600;
        double y = speedMph * Math.cos(heading * pi / 180) * durationInSeconds / 3600;


        double newLat = currentLocation.getLatitude() + 180 / pi * y / earthRadius;
        double newLong = currentLocation.getLongitude() + 180 / pi / Math.sin(currentLocation.getLatitude() * pi / 180) * x / earthRadius;

        Location finalLocation = new Location(currentLocation);
        finalLocation.setLatitude(newLat);
        finalLocation.setLongitude(newLong);

        return finalLocation;
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
