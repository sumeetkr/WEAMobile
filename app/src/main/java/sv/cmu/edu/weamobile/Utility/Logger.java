package sv.cmu.edu.weamobile.Utility;

import android.util.Log;

/**
 * Created by sumeet on 10/9/14.
 */
public class Logger {
    public static final String TAG = "IPS";

    public static void log(String text){
        if(text==null || text.isEmpty()){
            text = "no message";
        }
        Log.d(TAG, text);
    }

    public static void log(String tag, String text){
        Log.d(tag, text);
    }
}
