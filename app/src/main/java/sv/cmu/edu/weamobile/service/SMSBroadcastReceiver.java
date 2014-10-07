package sv.cmu.edu.weamobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    public SMSBroadcastReceiver() {
    }

    static final String ACTION_NOTIFICATION = "sv.cmu.edu.weamobile.service.NOTIFICATION_RECEIVER";

    static Integer base62_to_int(String sequence, Integer base, Integer digits) //This is only signed 32-bit int!!! Enough for now, I hope
    {
        Integer res = 0;
        Integer seq_len = sequence.length();
        char[] seq_char = sequence.toCharArray();
        for(Integer n = 0; n < digits; n++)
        {
            Integer c;
            char dig = seq_char[seq_len - 1 - n];
            if (dig > 'Z')
                c = dig - 'a' + 36;
            else if (dig > '9')
                c = dig - 'A' + 10;
            else
                c = dig - '0';
            res += c*Double.valueOf(Math.pow(base, n)).intValue();
        }
        return res;
    }

    // http://www.learn-android-easily.com/2012/11/how-to-receive-sms.html
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.w("smsreceiver", "Received an intent, action: " + intent.getAction() + "; type: " + intent.getType() + "; categories: " + intent.getCategories());
        String action = intent.getAction();
        if(action != null && action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String messageReceived = "";
            if (bundle != null)
            {
                //---retrieve the SMS messages received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i=0; i<msgs.length; i++)

                {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    messageReceived += msgs[i].getMessageBody().toString();
                    //messageReceived += "\n";
                }

                Integer start_index = messageReceived.indexOf("myPAWS1");
                Log.w("smsreceiver", "Raw message: " + messageReceived.replace("\n", "").replace("  ", ""));
                if (start_index != -1) {
                    String meaningful_part = messageReceived.substring(start_index);
                    process_meaningful_part(context, meaningful_part);
                }
            }
        } else {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                String meaningful_part = intent.getStringExtra("message_body");
                if (meaningful_part != null) {
                    Log.w("smsreceiver", "Message body (possibly trimed): " + meaningful_part.replace("\n", "").replace("  ", ""));
                    process_meaningful_part(context, meaningful_part);
                }
            }
        }
    }

    public void process_meaningful_part(Context context, String meaningful_part) {

        meaningful_part = meaningful_part.substring(7); //remove myPAWS1 prefix
        Log.w("smsreceiver", "Message body (possibly trimed): " + meaningful_part.replace("\n", "").replace("  ", ""));

        WEANewAlertIntent newAlertIntent = new WEANewAlertIntent(meaningful_part, "");
//        Intent newAlertIntent = new Intent();
//        newAlertIntent.setClass(context, MainActivity.class);

        String string_id = meaningful_part.substring(0, 4);

        Integer numerical_id = base62_to_int(string_id, 62, 4);

        newAlertIntent.putExtra("message_id", numerical_id);

        char[] meaningful_array = meaningful_part.toCharArray();
        Integer alert_and_alarm_type = Integer.parseInt(meaningful_part.substring(4, 5), 16);
        Integer alert_type = alert_and_alarm_type / 5;
        Integer alarm_type = alert_and_alarm_type % 5;

        newAlertIntent.putExtra("alert_type", alert_type);
        newAlertIntent.putExtra("alarm_type", alarm_type);

        String message_body = "";
        Integer n;
        for(n = 5; n < 95; n++)
        {
            if (meaningful_array[n] == '#')
                break;
            message_body = message_body + meaningful_array[n];
        }

        switch(alert_type) {
            case 0:
                message_body = "Presidential alert: " + message_body;
                break;
            case 1:
                message_body = "AMBER alert: " + message_body;
                break;
            case 2:
                message_body = "Extreme alert: " + message_body;
                break;
            default:
                break;

        }

        newAlertIntent.putExtra("message_body", message_body);

        if(meaningful_array.length < n) {
            newAlertIntent.putExtra("has_geotargeting_info", false);
            Log.d("SmsReceiver", "Message lenght too short: " + Integer.valueOf(meaningful_array.length).toString());

        } else {
            if (meaningful_array[n+1] == 'C')
            {
                String[] values = meaningful_part.substring(n+2).split(",");
                if (values.length == 3)
                {
                    Log.d("SmsReceiver", "Circle geotargeting received");
                    Double circle_center_lat = Double.valueOf(values[0]);
                    Double circle_center_lon = Double.valueOf(values[1]);
                    Double circle_radius = Double.valueOf(values[2]);
                    newAlertIntent.putExtra("circle_center_lat", circle_center_lat);
                    newAlertIntent.putExtra("circle_center_lon", circle_center_lon);
                    newAlertIntent.putExtra("circle_radius", circle_radius);
                    newAlertIntent.putExtra("has_geotargeting_info", true);
                    newAlertIntent.putExtra("geotargeting_type", "circle");
                } else {
                    newAlertIntent.putExtra("has_geotargeting_info", false);
                }
            } else if (meaningful_array[n+1] == 'R') {
                String[] values = meaningful_part.substring(n+2).split(",");
                if (values.length == 4)
                {
                    Double lat0 = Double.valueOf(values[0]);
                    Double lon0 = Double.valueOf(values[1]);
                    Double dlat = Double.valueOf(values[2]);
                    Double dlon = Double.valueOf(values[2]);
                    newAlertIntent.putExtra("lat0", lat0);
                    newAlertIntent.putExtra("lon0", lon0);
                    newAlertIntent.putExtra("dlat", dlat);
                    newAlertIntent.putExtra("dlon", dlon);
                    newAlertIntent.putExtra("has_geotargeting_info", true);
                    newAlertIntent.putExtra("geotargeting_type", "rectangle");
                } else {
                    newAlertIntent.putExtra("has_geotargeting_info", false);
                }
            } else {
                newAlertIntent.putExtra("has_geotargeting_info", false);
            }
        }
        newAlertIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(newAlertIntent);
        context.sendBroadcast(newAlertIntent);
    }
}
