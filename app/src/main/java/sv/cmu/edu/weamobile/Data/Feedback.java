package sv.cmu.edu.weamobile.Data;

/**
 * Created by sumeet on 9/25/14.
 */

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sumeet on 7/7/14.
 */
public class Feedback {

    //check below sheet for format
    //https://docs.google.com/a/west.cmu.edu/spreadsheets/d/1sUD3ZtXgOgCPb0P9YHzwebzNGp3y4YuOeCyAmM_I7Ho/edit#gid=1899141696
    private int alertId;
    private String phoneId;
    private double [] geolocation;
    private String abstractLocation = "NA";
    private long timeReceived;
    private long timeAcknowledged;
    private Feedback feedback;
    private int inTarget; //0 or 1
    private int versionCode;
    private String comment;
    private List<Answer> answers = new ArrayList<Answer>();


    public String getJSON(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public void Clean(){
        Log.d("SmsReceiver","Cleaning feedback");
        //phoneId="";
        //geolocation= null;
        //timeReceived=0;
        timeAcknowledged = 0;
        comment="";
        feedback = null;
        inTarget=0;

    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public double[] getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(double [] geolocation) {
        this.geolocation = geolocation;
    }

    public String getAbstractLocation() {
        return abstractLocation;
    }

    public void setAbstractLocation(String abstractLocation) {
        this.abstractLocation = abstractLocation;
    }

    public long getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(long timeReceived) {
        this.timeReceived = timeReceived;
    }

    public long getTimeAcknowledged() {
        return timeAcknowledged;
    }

    public void setTimeAcknowledged(long timeAcknowledged) {
        this.timeAcknowledged = timeAcknowledged;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public int getInTarget() {
        return inTarget;
    }

    public void setInTarget(int inTarget) {
        this.inTarget = inTarget;
    }
    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public class Answer{
        private String question;
        private String answerCode;

        public Answer(String question, String answer){
            this.question= question;
            this.answerCode= answer;
        }

        public String getJSON(){
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}