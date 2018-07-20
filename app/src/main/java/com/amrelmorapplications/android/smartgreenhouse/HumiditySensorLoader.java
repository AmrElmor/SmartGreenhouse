package com.amrelmorapplications.android.smartgreenhouse;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Loads a list of sensors by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class HumiditySensorLoader extends AsyncTaskLoader<List<Sensors>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = HumiditySensorLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link TemperatureSensorLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public HumiditySensorLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Sensors> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of sensors.
        List<Sensors> sensors = HumidityQueryUtils.fetchSensorsData(mUrl);
        return sensors;
    }
}

