package sv.cmu.edu.weamobile.utility;

/**
 * Created by sumeet on 9/25/14.
 */
public class Constants {
    public static final String BASE_URL = "https://wea-stage.herokuapp.com";
//    public static final String URL_TO_GET_CONFIGURATION = "http://code.sumeetkumar.in/config.json";
    public static final String URL_TO_GET_CONFIGURATION = BASE_URL +"/wea/api/heartbeat/";
    public static final String URL_TO_SEND_HEARTBEAT = BASE_URL +"/wea/api/phone/";
    public static final String URL_TO_FETCH_ALERTS = BASE_URL + "/wea/api/message/all/"; //GET /message/all/{phoneId}

//public static final String URL_TO_GET_CONFIGURATION = "http://10.0.17.19:5000/wea/api/heartbeat/";
//    public static final String FEEDBACK_URL_ROOT = "http://code.sumeetkumar.in/user_study/WEA/feedback_form.html";
    public static final String FEEDBACK_URL_ROOT = BASE_URL+ "/feedback/"; //alertid/phoneid
    public static final String STATE_URL_ROOT = BASE_URL+"/wea/api/seen/"; //alertid/phoneid
    public static final String REGISTRATION_URL_ROOT = BASE_URL+"/wea/api/registration/"; //emsi/participant

    public static final String FETCH_CONFIG = "fetch_config";
    public static final String PHONE_ID = "phone_id";
    public static final String SERVER_REGISTRATION_URL = "http://wea-stage.herokuapp.com/wea/api/registration/android";
    public static final int TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES = 5;
    public static final int TIME_THRESHOLD_TO_SHOW_ALERT_IN_SECONDS = 2;
    public static final String SMS_CODE_FOR_WEA_MESSAGES = "myPAWS1";
    public static final String ACTIVATION_CODE = "weacmu2015";
    public static final String ALERT_ID= "item_id";
    public static final String SHOW_MAIN_VIEW_ACTION= "show_main_View";
    public static final String ARG_ITEM_ID = "item_id";
    public static final String CONFIG_JSON = "alert_json";
    public static final String FEEDBACK_URL = "feedback_url";
    public static final String WEA_CONFIG_FILE_NAME_ON_DISK = "WEA_Configuration.json";
    public static final String SHARED_PREFERENCES = "preferences";
    public static final String ALERT_STATE = "alert_state";
    public static final boolean IS_IN_DEBUG_MODE = false;

    public static final String IS_DEBUG_MODE = "is_in_debug_mode";
    public static final String IS_LOCATION_HISTORY_ENABLED = "is_location_history_enabled";
    public static final String IS_ACTIVITY_HISTORY_ENABLED = "is_activity_history_enabled";
    public static final String IS_MOTION_ENABLED = "is_motion_enabled";
    public static final String IS_SHOW_NOTIFICATIONS_ENABLED = "is_show_notifications_enabled";
    public static final String IS_SHOW_ALL_ALERTS_ENABLED = "is_show_all_alerts_enabled";
    public static final String IS_ACTIVITY_RECOGNITION_ENABLED = "is_activity_recognition_enabled";
    public static final String IS_FETCH_ALERTS_PANEL_ENABLED = "is_fetch_alerts_enabled";

    public static final String USER_NAME = "user_name";
    public static final String THANKS_FOR_FEEDBACK = "Thanks for Feedback !! CMU WEA+ team";
    public static final String SHOWING_FEEDBACK_FORM = "Loading feedback form...";
    public static final String ACTIVITY ="activity";
    public static final String ACTIVITY_TYPE ="activity_type";
    public static final String ACTIVITY_CONFIDENCE ="activity_confidence";
    public static final String WEA_GPS_PROVIDER = "wea_gps_provider";
    public static final String PHONE_TOKEN = "phone_token";
    public static final String EPOCH_TIME_WHEN_LAST_UPDATED = "epoch_time_when_last_updated";
    public static final int TIME_GAP_TO_RESTORE_TO_DEFAULT = 1000*60*10;
    public static final String LAST_TIME_WHEN_ALERT_RECEIVED = "last_alert_time";
    public static final String LAST_TIME_WHEN_HEARTBEAT_SENT = "lastTimeChecked";


    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;
}
