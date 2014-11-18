package sv.cmu.edu.weamobile.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.cmu.edu.weamobile.data.Alert;
import sv.cmu.edu.weamobile.data.AlertState;
import sv.cmu.edu.weamobile.data.AppConfiguration;
import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.utility.AlertListAdapter;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;

public class AlertListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private List<Alert> alertItems;
    private Map<Integer, Alert> alertsMap;
    private List<AlertState> alertStates;
    private Map<Integer, AlertState> alertStateMap;


    public interface Callbacks {
        public void onItemSelected(String id);
    }

    private Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    public List<Alert> updateListAndReturnAnyActiveAlertNotShown(List<Alert> alerts, List<AlertState> alertsStates) {

        List<Alert> activeButNotShown = new ArrayList<Alert>();

        if(this.alertStates == null){
            this.alertStates = new ArrayList<AlertState>();
            alertStateMap = new HashMap<Integer, AlertState>();
        }
        this.alertStates.clear();
        alertStateMap.clear();

        if(alertItems == null)
        {
            alertItems = new ArrayList<Alert>();
            alertsMap = new HashMap<Integer, Alert>();
        }
        alertItems.clear();
        alertsMap.clear();

        for(AlertState state : alertsStates){
            alertStates.add(state);
            alertStateMap.put(state.getId(), state);
        }

        for(Alert alert:alerts){
            AlertState state = alertStateMap.get(alert.getId());

            if(alert.isNotOfFuture() && state!= null && state.isInPolygonOrAlertNotGeoTargeted()){
                addItem(alert);
            }

            if(alert.isActive()){
                if(state!=null && !state.isAlreadyShown()){
                    activeButNotShown.add(alert);
                }
            }
        }

        if(getListAdapter()!= null) ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();

        return  activeButNotShown;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertItems = new ArrayList<Alert>();
        alertsMap = new HashMap<Integer, Alert>();

        setListAdapter(new AlertListAdapter(
                getActivity(),
                R.id.username,
                (ArrayList<Alert>) alertItems));


        if(getArguments() != null && getArguments().containsKey(Constants.CONFIG_JSON)) {
            AppConfiguration configuration = AppConfiguration.fromJson(getArguments().getString(Constants.CONFIG_JSON));

            for(Alert alert:configuration.getAlertsWhichAreNotGeoTargetedOrGeotargetedAndUserWasInTarget(getActivity().getApplicationContext())){
                addItem(alert);
            }

//            for(Alert alert:configuration.getAlerts(getActivity().getApplicationContext())){
//                addItem(alert);
//            }

            ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Alert alert = alertItems.get(position);
        Logger.log(alert.toString());
        String idStr = String.valueOf(alert.getId());
        mCallbacks.onItemSelected(idStr);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private void addItem(Alert item) {
        alertItems.add(item);
        alertsMap.put(item.getId(), item);
    }
}
