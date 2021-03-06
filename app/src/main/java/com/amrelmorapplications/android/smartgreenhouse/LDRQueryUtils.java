package com.amrelmorapplications.android.smartgreenhouse;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper methods related to requesting and receiving sensors data from Server.
 */
public final class LDRQueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = LDRQueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link LDRQueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private LDRQueryUtils() {
    }

    /**
     * Query the Server dataset and return a list of {@link Sensors} objects.
     */
    public static List<Sensors> fetchSensorsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Sensors}
        List<Sensors> sensors = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Sensors}
        return sensors;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the sensors JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Sensors} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Sensors> extractFeatureFromJson(String sensorsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(sensorsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding sensors to
        List<Sensors> sensors = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            // JSONObject baseJsonResponse = new JSONObject(sensorsJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or sensors).
            JSONArray sensorsArray = new JSONArray(sensorsJSON);


            // For each sensor in the sensorArray, create an {@link Sensors} object
            for (int i = 0; i < sensorsArray.length(); i++) {

                // Get a single sensor at position i within the list of sensors
                JSONObject currentSensor = sensorsArray.getJSONObject(i);

                // For a given sensor, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that sensor.


                int mag = currentSensor.getInt("ldrData");

                String location = "High";

                String timeStr = currentSensor.getString("time");
                String[] timeArr;
                timeArr = timeStr.split(" ");
                String date = timeArr[1] + " " + timeArr[2] + "," + timeArr[3];
                String time = timeArr[4];


                // Create a new {@link Sensors} object with the magnitude, location, time,
                // and url from the JSON response.
                Sensors sensor = new Sensors(mag, location, date, time);

                // Add the new {@link Sensors} to the list of sensors.
                sensors.add(sensor);
            }
            Collections.reverse(sensors);

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the sensors JSON results", e);
        }

        // Return the list of sensors
        return sensors;
    }

}
