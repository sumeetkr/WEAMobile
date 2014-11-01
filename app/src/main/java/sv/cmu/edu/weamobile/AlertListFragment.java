package sv.cmu.edu.weamobile;

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

import sv.cmu.edu.weamobile.Data.Alert;
import sv.cmu.edu.weamobile.Data.AppConfiguration;
import sv.cmu.edu.weamobile.Utility.Logger;

public class AlertListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private List<Alert> alertItems;
    private Map<Integer, Alert> alertsMap;

    public interface Callbacks {
        public void onItemSelected(String id);
    }

    private Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    public void updateList(Alert[] alerts) {
        if(alertItems == null)
        {
            alertItems = new ArrayList<Alert>();
            alertsMap = new HashMap<Integer, Alert>();
        }
        alertItems.clear();
        alertsMap.clear();
        for(Alert alert:alerts){
            addItem(alert);
        }

        if(getListAdapter()!= null) ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertItems = new ArrayList<Alert>();
        alertsMap = new HashMap<Integer, Alert>();

        setListAdapter(new ArrayAdapter<Alert>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                alertItems));


        if(getArguments() != null && getArguments().containsKey(AlertDetailFragment.ALERTS_JSON)) {
            AppConfiguration configuration = AppConfiguration.fromJson(getArguments().getString(AlertDetailFragment.ALERTS_JSON));

            for(Alert alert:configuration.getAlerts()){
                addItem(alert);
            }

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
