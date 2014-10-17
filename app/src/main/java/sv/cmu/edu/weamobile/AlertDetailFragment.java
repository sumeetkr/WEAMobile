package sv.cmu.edu.weamobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AlertContent;
import sv.cmu.edu.weamobile.Utility.Logger;


/**
 * A fragment representing a single Alert detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link AlertDetailActivity}
 * on handsets.
 */
public class AlertDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Alert mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlertDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String key = getArguments().getString(ARG_ITEM_ID);
            Logger.log("AlertDetailFragment key: " + key);
            mItem = AlertContent.getAlertsMap().get(Integer.parseInt(key));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alert_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            Logger.log("Item is there"+ mItem.getText());
//            ((TextView) rootView.findViewById(R.id.alert_detail)).setText("Alert "+ mItem.toString());
        }else{
            Logger.log("Item is null");
        }


        return rootView;
    }
}
