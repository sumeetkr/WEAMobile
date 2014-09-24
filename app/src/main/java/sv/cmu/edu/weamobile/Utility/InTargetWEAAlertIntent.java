package sv.cmu.edu.weamobile.Utility;

import sv.cmu.edu.weamobile.service.WEAAlertIntent;

/**
 * Created by sumeet on 9/24/14.
 */
public class InTargetWEAAlertIntent extends WEAAlertIntent {

    public InTargetWEAAlertIntent(String message, String polygonEncoded){
        super(message, polygonEncoded);
        setAction("IN_TARGET_WEA_ALERT");
    }
}
