package sv.cmu.edu.weamobile.data;

import com.google.gson.Gson;

import java.util.Date;

import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.WEAUtil;

/**
 * Created by sumeet on 4/2/15.
 */
public class Message {
    private int id;
    private String messageType;
    private String scope;
    private String status;
    private Info[] info;
    private Parameter parameter;
    private Incident [] incidents;

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getId() {
        return id;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getScope() {
        return scope;
    }

    public String getStatus() {
        return status;
    }

    public Info [] getInfo() {
        return info;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public Incident [] getIncidents() {
        return incidents;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    protected void setScope(String scope) {
        this.scope = scope;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    protected void setInfo(Info[] info) {
        this.info = info;
    }

    protected void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    protected void setIncidents(Incident [] incidents) {
        this.incidents = incidents;
    }

    public class Info{
        private int certainty;
        private String eventCategory;
        private String eventType;
        private String headline;
        private String eventDescription;
        private String expires;
        private String onset;
        private String responseType;
        private String urgency;
        private String severity;
        private String [] audiences;
        private String [] resources;

        public int getCertainty() {
            return certainty;
        }

        public String getEventCategory() {
            return eventCategory;
        }

        public String getEventType() {
            return eventType;
        }

        public String getHeadline() {
            return headline;
        }

        public String getEventDescription() {
            return eventDescription;
        }

        public String getExpires() {
            return expires;
        }

        public String getOnset() {
            return onset;
        }

        public String getResponseType() {
            return responseType;
        }

        public String getUrgency() {
            return urgency;
        }

        public String getSeverity() {
            return severity;
        }

        public String [] getAudiences() {
            return audiences;
        }

        public String [] getResources() {
            return resources;
        }

        protected void setCertainty(int certainty) {
            this.certainty = certainty;
        }

        protected void setEventCategory(String eventCategory) {
            this.eventCategory = eventCategory;
        }

        protected void setEventType(String eventType) {
            this.eventType = eventType;
        }

        protected void setHeadline(String headline) {
            this.headline = headline;
        }

        protected void setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
        }

        protected void setExpires(String expires) {
            this.expires = expires;
        }

        protected void setOnset(String onset) {
            this.onset = onset;
        }

        protected void setResponseType(String responseType) {
            this.responseType = responseType;
        }

        protected void setUrgency(String urgency) {
            this.urgency = urgency;
        }

        protected void setSeverity(String severity) {
            this.severity = severity;
        }

        protected void setAudiences(String [] audiences) {
            this.audiences = audiences;
        }

        protected void setResources(String [] resources) {
            this.resources = resources;
        }

        public Info fromJson(String s) {
            return new Gson().fromJson(s, Info.class);
        }

        public class Area{
            private int id;
            private GeoLocation [] polygon;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public GeoLocation[] getPolygon() {
                return polygon;
            }

            public void setPolygon(GeoLocation[] polygon) {
                this.polygon = polygon;
            }

            public Area fromJson(String s) {
                return new Gson().fromJson(s, Area.class);
            }
        }
    }

    public class Parameter{
        private boolean isMapToBeShown;
        private boolean isAlertActive;
        private boolean isPhoneExpectedToVibrate;
        private boolean isTextToSpeechExpected;
        private boolean geoFiltering;
        private boolean historyBasedFiltering;
        private boolean motionPredictionBasedFiltering;
        private String format;
        private String testbedType;

        public boolean isMapToBeShown() {
            return isMapToBeShown;
        }

        public boolean isAlertActive() {
            return isAlertActive;
        }

        public boolean isPhoneExpectedToVibrate() {
            return isPhoneExpectedToVibrate;
        }

        public boolean isTextToSpeechExpected() {
            return isTextToSpeechExpected;
        }

        public boolean isGeoFiltering() {
            return geoFiltering;
        }

        public boolean isHistoryBasedFiltering() {
            return historyBasedFiltering;
        }

        public boolean isMotionPredictionBasedFiltering() {
            return motionPredictionBasedFiltering;
        }

        public String getFormat() {
            return format;
        }

        public String getTestbedType() {
            return testbedType;
        }

        protected void setMapToBeShown(boolean isMapToBeShown) {
            this.isMapToBeShown = isMapToBeShown;
        }

        protected void setAlertActive(boolean isAlertActive) {
            this.isAlertActive = isAlertActive;
        }

        protected void setPhoneExpectedToVibrate(boolean isPhoneExpectedToVibrate) {
            this.isPhoneExpectedToVibrate = isPhoneExpectedToVibrate;
        }

        protected void setTextToSpeechExpected(boolean isTextToSpeechExpected) {
            this.isTextToSpeechExpected = isTextToSpeechExpected;
        }

        protected void setGeoFiltering(boolean geoFiltering) {
            this.geoFiltering = geoFiltering;
        }

        protected void setHistoryBasedFiltering(boolean historyBasedFiltering) {
            this.historyBasedFiltering = historyBasedFiltering;
        }

        protected void setMotionPredictionBasedFiltering(boolean motionPredictionBasedFiltering) {
            this.motionPredictionBasedFiltering = motionPredictionBasedFiltering;
        }

        protected void setFormat(String format) {
            this.format = format;
        }

        protected void setTestbedType(String testbedType) {
            this.testbedType = testbedType;
        }
    }

    public class Incident{

        public Incident fromJson(String s) {
            return new Gson().fromJson(s, Incident.class);
        }
    }

    public static Message fromJson(String s) {
        return new Gson().fromJson(s, Message.class);
    }

    public boolean isInRangeToSchedule() {
        long currentTime = System.currentTimeMillis();
        //only show if not shown before in +60 -1 seconds
        return ((this.getScheduleEpochInMillis()- 1.5* Constants.TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES*60*1000) <currentTime)
                && (this.getScheduleEpochInMillis() > currentTime);
    }

    public boolean isActive(){
        boolean isActive = false;
        long time = System.currentTimeMillis()/1000;
        if(getEndingAtEpochInSeconds() > time && time >= getScheduledEpochInSeconds()){
            isActive = true;
        }
        return  isActive;
    }

    public boolean isNotOfFuture() {
        return this.getScheduledEpochInSeconds() <= System.currentTimeMillis()/1000;
    }

    public boolean isOfFuture(){
        return this.getScheduledEpochInSeconds() >= System.currentTimeMillis()/1000;
    }

    public String getScheduledFor(){
        String start = getInfo()[0].getOnset();
        Date date = WEAUtil.getTimeStringFromJsonTime(start, "UTC");
        return date.toLocaleString();
    }

    public Long getScheduledEpochInSeconds(){
        long epoch = 0;
        String start = getInfo()[0].getOnset();
        Date date = WEAUtil.getTimeStringFromJsonTime(start, "UTC");
        if(date != null){
            epoch = date.getTime();
        }
        return epoch/1000;
    }

    public long getScheduleEpochInMillis(){
        long epoch = 0;
        String start = getInfo()[0].getOnset();
        Date date = WEAUtil.getTimeStringFromJsonTime(start, "UTC");
        if(date != null){
            epoch = date.getTime();
        }

        return  epoch;
    }

    public Long getEndingAtEpochInSeconds(){
        long epoch = 0;
        String end = getInfo()[0].getExpires();
        Date date = WEAUtil.getTimeStringFromJsonTime(end, "UTC");
        if(date != null){
            epoch = date.getTime();
        }
        return epoch/1000;
    }

}

