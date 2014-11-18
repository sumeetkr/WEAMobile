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
import sv.cmu.edu.weamobile.data.AlertState;

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
        AlertState state = AlertHelper.getAlertState(getContext(), alert);

        if(alert!=null && alert.getScheduledEpochInSeconds() < System.currentTimeMillis()/1000 ) {

            ImageView imView = (ImageView) v.findViewById(R.id.avatar);
            TextView alertMessage = (TextView) v.findViewById(R.id.username);
            TextView alertType = (TextView) v.findViewById(R.id.email);
            TextView alertTime = (TextView) v.findViewById(R.id.time);

            if (state != null && state.isFeedbackGiven()) {
                imView.setImageResource(R.drawable.email_alert_icon);
                alertMessage.setText(AlertHelper.getTextWithStyle((alert.getAlertType() + " Alert "), 1.6f, true));
                alertType.setText(AlertHelper.getTextWithStyle(alert.getText(), 1f, true));
                alertTime.setText(AlertHelper.getTextWithStyle(alert.getScheduledForString() + " to " + alert.getEndingAtString(), 0.7f, true));
            } else {
                if(alert.isActive()){
                    imView.setImageResource(R.drawable.alert_green);
                    alertMessage.setText(AlertHelper.getTextWithStyle((alert.getAlertType() + " Alert "), 1.6f, false));
                    alertType.setText(AlertHelper.getTextWithStyle(alert.getText(), 1f, false));
                    alertTime.setText(AlertHelper.getTextWithStyle(alert.getScheduledForString() + " to " + alert.getEndingAtString(), 0.7f, false));
                }else{
                    imView.setImageResource(R.drawable.alert_red);
                    alertMessage.setText(AlertHelper.getTextWithStyle((alert.getAlertType() + " Alert "), 1.6f, false));
                    alertType.setText(AlertHelper.getTextWithStyle(alert.getText(), 1f, false));
                    alertTime.setText(AlertHelper.getTextWithStyle(alert.getScheduledForString() + " to " + alert.getEndingAtString(), 0.7f, false));
                }
            }
        }
        return v;
    }
}