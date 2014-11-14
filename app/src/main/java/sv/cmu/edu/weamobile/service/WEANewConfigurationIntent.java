package sv.cmu.edu.weamobile.service;

import android.content.Intent;

import sv.cmu.edu.weamobile.utility.Constants;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEANewConfigurationIntent extends Intent{
    public static String WEA_NEW_ALERT = "NewWEAAlert";
    public static String MESSAGE = "MESSAGE";
    public static String STATUS = "STATUS";

    public WEANewConfigurationIntent(String message, String configJson, boolean isOld){
        super(WEA_NEW_ALERT);
        setAction("android.intent.action.NEW_ALERT");
        addCategory(Intent.CATEGORY_DEFAULT);
        putExtra(MESSAGE, message);
        putExtra(Constants.CONFIG_JSON, configJson);
        putExtra(STATUS, isOld);
    }
}
