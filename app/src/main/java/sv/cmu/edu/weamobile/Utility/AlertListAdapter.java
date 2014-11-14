package sv.cmu.edu.weamobile.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.R;

/**
 * Created by sumeet on 10/31/14.
 */
public class AlertListAdapter extends ArrayAdapter<Alert> {
    private ArrayList<Alert> alerts;

    public AlertListAdapter(Context context, int textViewResourceId, ArrayList<Alert> alerts) {
        super(context, textViewResourceId, alerts);
        this.alerts = alerts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
        }

        Alert alert = alerts.get(position);
        if (alert != null) {
            TextView alertMessage = (TextView) v.findViewById(R.id.username);
            TextView alertType = (TextView) v.findViewById(R.id.email);

            if (alertMessage != null) {
                alertMessage.setText(AlertHelper.getTextWithStyle(alert.getText(), 33));
            }

            if (alertType != null) {
                alertType.setText(alert.getAlertType() + " Alert " + alert.getScheduledForString());
            }

            ImageView imView = (ImageView)v.findViewById(R.id.avatar);
            if(alert.getScheduledEpochInSeconds() < System.currentTimeMillis()/1000){
                //if recent show in green
                if(System.currentTimeMillis()/1000 <  alert.getEndingAtEpochInSeconds() ){
                    imView.setImageResource(R.drawable.alert_green);
                }else{
                    imView.setImageResource(R.drawable.alert_red);
                }
                v.setVisibility(View.VISIBLE);
            }
        }

        return v;
    }
}