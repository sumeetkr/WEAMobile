package sv.cmu.edu.weamobile.utility;

/**
 * Created by sumeet on 9/25/14.
 */
public class Constants {
//    public static final String URL_TO_GET_CONFIGURATION = "http://code.sumeetkumar.in/config.json";
    public static final String URL_TO_GET_CONFIGURATION = "http://cmu-wea.herokuapp.com/wea/api/heartbeat/";
//public static final String URL_TO_GET_CONFIGURATION = "http://10.0.17.19:5000/wea/api/heartbeat/";
//    public static final String FEEDBACK_URL_ROOT = "http://code.sumeetkumar.in/user_study/WEA/feedback_form.html";
    public static final String FEEDBACK_URL_ROOT = "http://cmu-wea.herokuapp.com/feedback/"; //alertid/phoneid
    public static final String STATE_URL_ROOT = "http://cmu-wea.herokuapp.com/wea/api/seen/"; //alertid/phoneid
    public static final String REGISTRATION_URL_ROOT = "http://cmu-wea.herokuapp.com/wea/api/registration/"; //emsi/participant
    public static final int TIME_RANGE_TO_SHOW_ALERT_IN_MINUTES = 5;
    public static final int TIME_THRESHOLD_TO_SHOW_ALERT_IN_SECONDS = 2;
    public static final String SMS_CODE_FOR_WEA_MESSAGES = "myPAWS1";
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

    public static final String USER_NAME = "user_name";

    public static final String THANKS_FOR_FEEDBACK = "Thanks for Feedback !! CMU WEA+ team";
    public static final String SHOWING_FEEDBACK_FORM = "Loading feedback form...";
}
