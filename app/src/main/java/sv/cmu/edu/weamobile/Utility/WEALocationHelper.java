package sv.cmu.edu.weamobile.utility;//30.57,-90.86 30.65,-90.75 30.65,-90.57 30.39,-90.6 30.39,-90.9 30.57,-90.86

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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

    public static boolean isInPolygon(LatLng location, GeoLocation [] polygon) {
        double [] longs = new double[polygon.length];
        double [] lats = new double[polygon.length];

        for(int i = 0; i<polygon.length; i++){
            lats[i]= Double.parseDouble(polygon[i].getLat());
            longs[i] = Double.parseDouble(polygon[i].getLng());
        }

        Logger.log("Verifying presence in polygon.");
        return WEALocationHelper.pointInPoly(polygon.length, lats, longs,
                location.latitude,
                location.longitude);
    }

    public static  boolean areAnyPointsInPolygon2(List<LatLng> locations, GeoLocation [] polygon){
        boolean pointsInPolygon = false;

        for(LatLng location : locations){
            if(isInPolygon(location, polygon)){
                pointsInPolygon = true;
                break;
            }
        }

        return  pointsInPolygon;
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
        double dist=0;
        try{
            float [] results= new float[2];
            //Test was failing as Location (android type) is not availabel while running unit test
//            Location.distanceBetween(polyCenter[0], polyCenter[1], myLocation.getLatitude(), myLocation.getLongitude(), results);
            center= results[0]/1000;

            dist = distance(polyCenter[0], polyCenter[1], myLocation.getLatitude(), myLocation.getLongitude(), "K");
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            //Logger.log(ex.getMessage());
        }
        return dist;
    }

    public static double getPolygonAverageRadius(GeoLocation [] polygon){
        double avgRadius =0;

        double []  centers = calculatePolyCenter(polygon);

        for (GeoLocation vertex:polygon) {
            avgRadius += distance(centers[0], centers[1], vertex.getLatitude(), vertex.getLongitude(), "K");
        }

        avgRadius = avgRadius/polygon.length;

        return avgRadius;
    }

    public static double getDistance(GeoLocation [] polygon, GeoLocation location){
        double distance= getDistanceFromCentroid(
                location,
                calculatePolyCenter(polygon)
        );

        return distance;
    }

    public static float distFromInMeters(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    //it is very approximate algorithm and was written in hurry
    public static double getNearestDistance(GeoLocation [] polygon, GeoLocation location){
        double minDistance = Double.MAX_VALUE;

        for (GeoLocation vertex:polygon) {
            double dist  = distance(location.getLatitude(), location.getLongitude(), vertex.getLatitude(), vertex.getLongitude(), "K");
            if(dist<minDistance) minDistance = dist;
        }

        return  minDistance;
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

    public static List<LatLng> getFuturePredictionsOfLatLngs(List<GeoLocation> historyPoints) {

        int newPointsCount = historyPoints.size();
        List<LatLng> futurePoints = new ArrayList<LatLng>();
        if(newPointsCount >3){
            Location loc1 = WEALocationHelper.getLocationFromCoordinates(
                    historyPoints.get(newPointsCount - 3).getLatitude(),
                    historyPoints.get(newPointsCount - 3).getLongitude());
            Location loc2 = WEALocationHelper.getLocationFromCoordinates(
                    historyPoints.get(newPointsCount-2).getLatitude(),
                    historyPoints.get(newPointsCount-2).getLongitude());
            Location loc3 = WEALocationHelper.getLocationFromCoordinates(
                    historyPoints.get(newPointsCount-1).getLatitude(),
                    historyPoints.get(newPointsCount-1).getLongitude());

            double timDiffInSecs1 = (historyPoints.get(historyPoints.size()-1).getTimestamp().getTime()
                    - historyPoints.get(historyPoints.size()-2).getTimestamp().getTime())/(1000);
            double speed1 = WEALocationHelper.getSpeedMPH(loc2, loc3, timDiffInSecs1);

            double timDiffInSecs2 = (historyPoints.get(historyPoints.size()-2).getTimestamp().getTime()
                    - historyPoints.get(historyPoints.size()-3).getTimestamp().getTime())/(1000);
            double speed2 = WEALocationHelper.getSpeedMPH(loc1, loc2, timDiffInSecs2);

            double heading1 = WEALocationHelper.getCurrentHeading(loc2, loc3);
            double heading2 = WEALocationHelper.getCurrentHeading(loc1, loc2);

            //give more weight to newer info
            double heading = (0.9*heading1+0.1*heading2);
            double speed = (0.8*speed1+0.2* speed2);

            Logger.log("Speed: " + speed + " heading: " + heading);

            if(Math.abs(speed)>= 0.01){ //else assume the user is still
                for (int j=0; j<13; j++){
                    //get every 5 minutes, for next hour
                    Location futureLocation = WEALocationHelper.getFutureLocation(loc3, heading, speed, j * 5 * 60);
                    futurePoints.add(new LatLng(futureLocation.getLatitude(), futureLocation.getLongitude()));
                }
            }
        }

        return futurePoints;
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

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::                                                                         :*/
/*::  This routine calculates the distance between two points (given the     :*/
/*::  latitude/longitude of those points). It is being used to calculate     :*/
/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
/*::                                                                         :*/
/*::  Definitions:                                                           :*/
/*::    South latitudes are negative, east longitudes are positive           :*/
/*::                                                                         :*/
/*::  Passed to function:                                                    :*/
/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
/*::    unit = the unit you desire for results                               :*/
/*::           where: 'M' is statute miles (default)                         :*/
/*::                  'K' is kilometers                                      :*/
/*::                  'N' is nautical miles                                  :*/
/*::  Worldwide cities and other features databases with latitude longitude  :*/
/*::  are available at http://www.geodatasource.com                          :*/
/*::                                                                         :*/
/*::  For enquiries, please contact sales@geodatasource.com                  :*/
/*::                                                                         :*/
/*::  Official Web site: http://www.geodatasource.com                        :*/
/*::                                                                         :*/
/*::           GeoDataSource.com (C) All Rights Reserved 2015                :*/
/*::                                                                         :*/
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
