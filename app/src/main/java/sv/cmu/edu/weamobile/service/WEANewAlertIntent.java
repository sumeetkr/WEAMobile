package sv.cmu.edu.weamobile.service;

import android.content.Intent;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEANewAlertIntent extends Intent{
    public static String WEA_NEW_ALERT = "NewWEAAlert";
    public static String CONFIG_JSON= "CONFIG_JSON";
    public static String MESSAGE = "MESSAGE";

    public WEANewAlertIntent(String message, String configJson){
        super(WEA_NEW_ALERT);
        setAction("android.intent.action.NEW_ALERT");
        addCategory(Intent.CATEGORY_DEFAULT);
        putExtra(MESSAGE, message);
        putExtra(CONFIG_JSON, configJson);
    }
}
