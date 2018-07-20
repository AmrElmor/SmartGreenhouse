package com.amrelmorapplications.android.smartgreenhouse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

public class HumidityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Sensors>> {

    private static final String LOG_TAG = HumidityFragment.class.getName();
    /**
     * URL for sensor data from the server dataset
     */
    private static final String Server_REQUEST_URL =
            "http://192.168.137.1:3000/temphum/";
    /**
     * Constant value for the sensor loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int SENSORS_LOADER_ID = 2;
    private TextView readingTextView;
    private TextView readingStatusTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView mEmptyStateTextView;
    private ProgressBar loadingIndicator;
    /**
     * Adapter for the list of sensors
     */
    private HumiditySensorAdapter mAdapter;

    private RequestQueue mQueue;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    public HumidityFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sensors_activity, container, false);

        // Find a reference to the {@link ListView} in the layout
        ListView sensorsListView = rootView.findViewById(R.id.list);

        mEmptyStateTextView = rootView.findViewById(R.id.empty_view);
        sensorsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of sensors as input
        mAdapter = new HumiditySensorAdapter(getActivity(), new ArrayList<Sensors>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        sensorsListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        if (getActivity() != null) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = LoaderManager.getInstance(this);

                    // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                    // because this activity implements the LoaderCallbacks interface).
                    loaderManager.initLoader(SENSORS_LOADER_ID, null, this);

                } else {
                    // Otherwise, display error
                    // First, hide loading indicator so error message will be visible
                    loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisibility(ProgressBar.GONE);
                    }

                    // Update empty state with no connection error message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        }

        return rootView;

    }

    @NonNull
    @Override
    public Loader<List<Sensors>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new HumiditySensorLoader(getActivity(), Server_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Sensors>> loader, List<Sensors> sensors) {
        // Hide loading indicator because the data has been loaded
        loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(ProgressBar.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (sensors != null && !sensors.isEmpty()) {
            mAdapter.addAll(sensors);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Sensors>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
        // Hide loading indicator because the data has been loaded
    }

    private void myUpdateOperation() {
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.restartLoader(SENSORS_LOADER_ID, null, this);
        mAdapter.notifyDataSetChanged();
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }
                }
        );

    }
}
