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
        AlertState state = AlertHelper.getAlertStateFromId(getContext(), String.valueOf(alert.getId()));

        if(alert!=null && alert.getScheduledEpochInSeconds() < System.currentTimeMillis()/1000 ) {

            ImageView imView = (ImageView) v.findViewById(R.id.avatar);
            TextView alertMessage = (TextView) v.findViewById(R.id.username);
            TextView alertType = (TextView) v.findViewById(R.id.email);

            if (state != null && state.isFeedbackGiven()) {
                imView.setImageResource(R.drawable.email_alert_icon);
                alertMessage.setText(AlertHelper.getTextWithStyle((alert.getAlertType() + " Alert "), 33, true));
                alertType.setText(AlertHelper.getTextWithStyle(alert.getText(), 28, true));

            } else {
                imView.setImageResource(R.drawable.alert_red);
                alertMessage.setText(AlertHelper.getTextWithStyle((alert.getAlertType() + " Alert "), 33, false));
                alertType.setText(AlertHelper.getTextWithStyle(alert.getText(), 28, false));
            }
        }
        return v;
    }
}