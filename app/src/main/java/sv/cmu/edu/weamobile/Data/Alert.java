package sv.cmu.edu.weamobile.data;

import com.google.gson.Gson;

import java.util.Date;

import sv.cmu.edu.weamobile.utility.WEAUtil;

/**
 * Created by sumeet on 9/28/14.
 */
public class Alert {
//    {
//            "id": 1,
//                "capFilePath": null,
//                "channel": "CM",
//                "format": "short",
//                "protocol": "simple",
//                "scheduledFor": "2015-01-01T05:19:11.000Z",
//                "endingAt": "2013-01-01T07:15:00.000Z",
//                "text": "Flash Flood Warning issued January 1 at 5:18AM CST expiring January 1 at 7:15AM CST by NWS Houston/Galveston TX",
//                "alertType": "Extreme",
//                "alertType2": "TBD",
//                "polygonType": "Polygon",
//                "fdbURL": "NWS-120723-220266-154673",
//                "createdAt": null,
//                "updatedAt": null,
//                "Polygon": [
//            {
//                "lng": "-94.71",
//                    "lat": "29.34",
//                    "id": 1
//            },
//            {
//                "lng": "-95.11",
//                    "lat": "29.09",
//                    "id": 2
//            },
//            {
//                "lng": "-94.98",
//                    "lat": "29.2",
//                    "id": 3
//            },
//            {
//                "lng": "-94.97",
//                    "lat": "29.19",
//                    "id": 4
//            },
//            {
//                "lng": "-94.91",
//                    "lat": "29.27",
//                    "id": 5
//            },
//            {
//                "lng": "-94.81",
//                    "lat": "29.31",
//                    "id": 6
//            },
//            {
//                "lng": "-94.83",
//                    "lat": "29.35",
//                    "id": 7
//            },
//            {
//                "lng": "-94.78",
//                    "lat": "29.33",
//                    "id": 8
//            },
//            {
//                "lng": "-94.8",
//                    "lat": "29.31",
//                    "id": 9
//            },
//            {
//                "lng": "-94.74",
//                    "lat": "29.33",
//                    "id": 10
//            },
//            {
//                "lng": "-94.74",
//                    "lat": "29.37",
//                    "id": 11
//            },
//            {
//                "lng": "-94.77",
//                    "lat": "29.36",
//                    "id": 12
//            },
//            {
//                "lng": "-94.78",
//                    "lat": "29.38",
//                    "id": 13
//            },
//            {
//                "lng": "-94.68",
//                    "lat": "29.48",
//                    "id": 14
//            },
//            {
//                "lng": "-94.61",
//                    "lat": "29.48",
//                    "id": 15
//            },
//            {
//                "lng": "-94.58",
//                    "lat": "29.53",
//                    "id": 16
//            },
//            {
//                "lng": "-94.5",
//                    "lat": "29.5",
//                    "id": 17
//            },
//            {
//                "lng": "-94.49",
//                    "lat": "29.54",
//                    "id": 18
//            },
//            {
//                "lng": "-94.44",
//                    "lat": "29.54",
//                    "id": 19
//            },
//            {
//                "lng": "-94.71",
//                    "lat": "29.34",
//                    "id": 20
//            }
//            ]
//        }

    private int id;
    private String text;
    private String alertType;
    private String scheduledFor;
    private String endingAt;
    private GeoLocation [] Polygon;
    private int options;
    private boolean isMapToBeShown = true;
    private boolean isPhoneExpectedToVibrate = true;
    private boolean isTextToSpeechExpected = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public GeoLocation[] getPolygon() {
        return Polygon;
    }

    public void setPolygon(GeoLocation[] polygon) {
        this.Polygon = polygon;
    }

    public static Alert fromJson(String s) {
        return new Gson().fromJson(s, Alert.class);
    }

    public String getEndingAt(){
        return endingAt;
    }

    public String getScheduledFor(){
        return scheduledFor;
    }

    public String getScheduledForString(){
        Date date = WEAUtil.getTimeStringFromJsonTime(scheduledFor, "UTC");
        return date.toLocaleString();
    }

    public Long getScheduledEpochInSeconds(){
        long epoch = 0;
        Date date = WEAUtil.getTimeStringFromJsonTime(scheduledFor, "UTC");
        if(date != null){
            epoch = date.getTime();
        }
        return epoch/1000;
    }

    public Long getEndingAtEpochInSeconds(){
        long epoch = 0;
        Date date = WEAUtil.getTimeStringFromJsonTime(endingAt, "UTC");
        if(date != null){
            epoch = date.getTime();
        }
        return epoch/1000;
    }

    public void setScheduledFor(String scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public void setEndingAt(String endingAt) {
        this.endingAt = endingAt;
    }

    @Override
    public String toString(){
        return getText();
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public boolean isMapToBeShown() {
        return isMapToBeShown;
    }

    public void setMapToBeShown(boolean isMapToBeShown) {
        this.isMapToBeShown = isMapToBeShown;
    }

    public boolean isPhoneExpectedToVibrate() {
        return isPhoneExpectedToVibrate;
    }

    public void setPhoneExpectedToVibrate(boolean isPhoneExpectedToVibrate) {
        this.isPhoneExpectedToVibrate = isPhoneExpectedToVibrate;
    }

    public boolean isTextToSpeechExpected() {
        return isTextToSpeechExpected;
    }

    public void setTextToSpeechExpected(boolean isTextToSpeechExpected) {
        this.isTextToSpeechExpected = isTextToSpeechExpected;
    }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public boolean isActive(){
        boolean isActive = false;
        long time = System.currentTimeMillis()/1000;
        if(getEndingAtEpochInSeconds() > time && time >= getScheduledEpochInSeconds()){
            isActive = true;
        }
        return  isActive;
    }

    public String getEndingAtString() {
        Date date = WEAUtil.getTimeStringFromJsonTime(endingAt, "UTC");
        return date.toLocaleString();
    }
}
