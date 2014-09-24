package sv.cmu.edu.weamobile.service;

import android.content.Intent;

/**
 * Created by sumeet on 9/24/14.
 */
public class WEAAlertIntent extends Intent{
    public WEAAlertIntent(String message, String polygonEncoded){
        setAction("NEW_WEA_ALERT");
        addCategory(Intent.CATEGORY_DEFAULT);
        putExtra("MESSAGE", message);
        putExtra("POLYGON_ENCODED", polygonEncoded);
    }
}
