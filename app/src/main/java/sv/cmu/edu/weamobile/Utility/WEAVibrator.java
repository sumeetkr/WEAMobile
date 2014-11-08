package sv.cmu.edu.weamobile.Utility;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by sumeet on 11/6/14.
 */
public class WEAVibrator{

    private static Vibrator vibrator;

    public static void vibrate(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {200, 200, 200, 200, 200, 200, 200, 400,200,
                400,200, 400,200, 200, 200, 200, 200, 200, 400,
                200, 200, 200, 200, 200, 200,400,200, 400,
                200, 400,200, 200, 200, 200, 200, 200};

        vibrator.vibrate(pattern, -1);
    }

    public void stop(){
        vibrator.cancel();
    }
}
