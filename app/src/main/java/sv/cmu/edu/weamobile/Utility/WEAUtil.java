package sv.cmu.edu.weamobile.utility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sumeet on 10/17/14.
 */
public class WEAUtil {
    public static String getTimeStringFromEpoch(long epoch){
        Date date = new Date(epoch * 1000L);
        DateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        format.setTimeZone(tz);
        String formatted = format.format(date);
        return  formatted;
    }

    public static Date getTimeStringFromJsonTime(String jsonTime, String jsonTimeZone ){
        //2014-10-30T00:15:00.000Z
        SimpleDateFormat sdfDateWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar cal = Calendar.getInstance();
        sdfDateWithTime.setTimeZone(TimeZone.getTimeZone(jsonTimeZone));
        Date  myDate = null;
        try {
            myDate = sdfDateWithTime.parse(jsonTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return  myDate;
    }

    public static String getIMSINumber(Context context){
        TelephonyManager telephoneMananger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephoneMananger.getSimSerialNumber();
        return imsi;
    }

    public static String getIMEI(Context context){
        TelephonyManager telephoneMananger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephoneMananger.getDeviceId();
        return imei;
    }

    public static void lightUpScreen(Context context){
        try{

            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

            boolean isScreenOn = pm.isScreenOn();

            Logger.log("screen on.................................", "" + isScreenOn);

            if(isScreenOn==false)
            {

                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
                wl.acquire(1000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
                wl_cpu.acquire(1000);
            }
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    public static void showMessageIfInDebugMode(Context context, String message){
        try{
            if(WEASharedPreferences.isInDebugMode(context)){
                Logger.log("Showing toast: " + message);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            }
        }catch (Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    public static float getBatteryLevel(Context context){
        float batteryPct =0.0f;
        try{
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            batteryPct = level / (float)scale;

        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
        return batteryPct;
    }
}
