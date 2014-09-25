package sv.cmu.edu.weamobile.service;

import android.content.Intent;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEANewAlertIntent extends Intent{
    public static String WEA_NEW_ALERT = "NewWEAAlert";

    public WEANewAlertIntent(String message, String polygonEncoded){
        setAction(WEA_NEW_ALERT);
        addCategory(Intent.CATEGORY_DEFAULT);
        putExtra("MESSAGE", message);
        putExtra("POLYGON_ENCODED", polygonEncoded);
    }
}
