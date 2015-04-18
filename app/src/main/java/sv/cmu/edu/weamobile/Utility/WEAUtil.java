package sv.cmu.edu.weamobile.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.data.UserActivity;
import sv.cmu.edu.weamobile.utility.ActivityRecognition.UserActivityRecognizer;
import sv.cmu.edu.weamobile.utility.db.LocationDataSource;
import sv.cmu.edu.weamobile.utility.db.MessageDataSource;
import sv.cmu.edu.weamobile.views.LoginActivity;

/**
 * Created by sumeet on 10/17/14.
 */
public class WEAUtil {
    private static UserActivityRecognizer activityRecognizer = null;

    public static String getTimeStringFromEpoch(long epoch){
        Date date = new Date(epoch * 1000L);
        DateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        format.setTimeZone(tz);
        String formatted = format.format(date);
        return  formatted;
    }

    public static String getTimeStringFromDate(Date date){
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

    public static String getPackageVersion(Context context){
        String version="";
        try {
            PackageInfo pInfo = null;
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if(pInfo != null) version = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(e.getMessage());
        }
        return version;
    }

    public static void sendHeartBeat(Context context,
                                     UserActivity activity) {
        GeoLocation location = new GeoLocation("0.00", "0.00", 0.00f);
        GPSTracker tracker =null;
        try{
            tracker = new GPSTracker(context);
            float batteryLevel = WEAUtil.getBatteryLevel(context);
            if(tracker.canGetLocation()){
                location = tracker.getNetworkGeoLocation();
                Logger.log("Sending lat " + location.getLatitude());
                Logger.log("Sending lng " + location.getLongitude());
            }else{
                Logger.log("Cannot get location for heartbeat");
            }

            location.setBatteryLevel(batteryLevel);
            location.setPackageVersion(WEAUtil.getPackageVersion(context));
            if(activity!= null){
                location.setActivityType(activity.getPrimaryActivityType());
                location.setActivityConfidence(activity.getActivityConfidence());
                location.setSecondaryActivity(activity.getSecondaryActivityTYpe());
                location.setSecondaryActivityConfidence(activity.getSecondaryActivityConfidence());
            }

            location.setAdditionalInfo("Activity: "+ UserActivity.getFriendlyName(activity.getPrimaryActivityType()) +
                    " Confidence :" +activity.getActivityConfidence());

            //provide last alert id, to return the new alerts
            MessageDataSource dataSource = new MessageDataSource(context);
            List<Message> messages = dataSource.getAllData();
            int lastMessageId = 0;
            for(Message message : messages){
                if(lastMessageId < message.getId()) lastMessageId = message.getId();
            }


            location.setLastReceivedMessageId(lastMessageId);

        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
        finally{
            try{
                //ToDo: Move this code to its right place
                if(tracker != null) tracker.stopUsingGPS();
                if(checkIfPhoneIsRegisteredIfNotRegister(context)){

                    WEAHttpClient.sendHeartbeat(location.getJson(), context);

                    //TODO: Need to move it at right location
                    LocationDataSource dataSource = new LocationDataSource(context);
                    dataSource.insertData(location);

                }else{
                    // try after 2 seconds
                    Handler handler = new Handler();
                    final GeoLocation finalLocation = location;
                    final Context ctxtCopy = context;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                WEAHttpClient.sendHeartbeat(finalLocation.getJson(), ctxtCopy);

                                //TODO: Need to move it at right location
                                LocationDataSource dataSource = new LocationDataSource(ctxtCopy);
                                dataSource.insertData(finalLocation);
                            }
                            catch(Exception ex){
                                Logger.log(ex.getMessage());
                            }
                        }
                    }, 2000);
                }
            }catch (Exception ex){
                Logger.log(ex.getMessage());
            }
        }
    }

    public static boolean checkIfPhoneIsRegisteredIfNotRegister(Context context){
        boolean isRegistered = false;
        String phoneId = WEASharedPreferences.getStringProperty(context,Constants.PHONE_ID);
        String token = WEASharedPreferences.getStringProperty(context, Constants.PHONE_TOKEN);

        if(phoneId!= null && !phoneId.isEmpty() && token!= null && !token.isEmpty()){
            isRegistered = true;
            Logger.log("Phone is registered");
        }else{
            Logger.log("Phone is not registered, registering");

            Intent dialogIntent = new Intent(context, LoginActivity.class);

            WEAUtil.showMessageIfInDebugMode(context, "Showing login view to register");
            context.startActivity(dialogIntent);

//            WEAHttpClient.registerPhoneAync(context);
        }

        return  isRegistered;
    }

    public static void sendHeartBeat(Context context){
        sendHeartBeat(context, null);
    }

    public  static  void  getUserActivityInfo(Context context) {
        try {
            Logger.log("Starting User Activity Recognition Service");
            Intent service = new Intent(context, UserActivityRecognizer.class);
            service.setAction(UserActivityRecognizer.START_ACTIVITY_RECOGNITION);
            context.startService(service);

//            activityRecognizer = new UserActivityRecognizer(context);
//            activityRecognizer.startActivityRecognitionScan();
//            Thread.sleep(500,0);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//            if(activityRecognizer != null) activityRecognizer.stopActivityRecognitionScan();
        }
    }

    /*
    * This function just posts an Android Style Notification in the title bar at the top.
    * Currently no intent is attached on opening it.
    */
    public static void postNotification(Intent intentAction, Context context, String message){
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Message Received!")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .getNotification();

        mNotificationManager.notify(R.string.notification_number, notification);
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static void restoreDebugSettingToOriginalState(Context context) {
        WEASharedPreferences.restoreDebugSetting(context, Constants.TIME_GAP_TO_RESTORE_TO_DEFAULT);
    }
}
