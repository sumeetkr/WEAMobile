package sv.cmu.edu.weamobile.Data;

/**
 * Created by sumeet on 10/7/14.
 */
public class AppConfiguration {

//    {
//        "configId": 1,
//            "channel": "CM",
//            "format": "Short",
//            "protocol": "paginated",
//            "scheduledFor": 123123123,
//            "endingAt": 1231231123,
//            "text": "Free Food Alert",
//            "alertType": "Extreme",
//            "alertType2": "TBD",
//            "polygonType": "Polygon",
//            "feedbackURL": "http://maxwell.sv.cmu.edu/api/feedback/1"
//    }

    private int configId;
    private String channel;
    private String protocol;
    private long scheduledFor;
    private long endingAt;
    private String text;
    private String alertType;
    private String alertType2;
    private String polygonType;
    private String feedbackURL;


    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public long getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(long scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public long getEndingAt() {
        return endingAt;
    }

    public void setEndingAt(long endingAt) {
        this.endingAt = endingAt;
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

    public String getAlertType2() {
        return alertType2;
    }

    public void setAlertType2(String alertType2) {
        this.alertType2 = alertType2;
    }

    public String getPolygonType() {
        return polygonType;
    }

    public void setPolygonType(String polygonType) {
        this.polygonType = polygonType;
    }

    public String getFeedbackURL() {
        return feedbackURL;
    }

    public void setFeedbackURL(String feedbackURL) {
        this.feedbackURL = feedbackURL;
    }
}
