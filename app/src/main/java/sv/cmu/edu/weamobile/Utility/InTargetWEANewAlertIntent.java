package sv.cmu.edu.weamobile.Utility;

import sv.cmu.edu.weamobile.service.WEANewAlertIntent;

/**
 * Created by sumeet on 9/24/14.
 */
public class InTargetWEANewAlertIntent extends WEANewAlertIntent {

    public InTargetWEANewAlertIntent(String message, String polygonEncoded){
        super(message, polygonEncoded);
        setAction("IN_TARGET_WEA_ALERT");
    }
}
