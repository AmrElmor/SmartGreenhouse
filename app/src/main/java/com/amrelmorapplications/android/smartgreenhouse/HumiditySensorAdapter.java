package com.amrelmorapplications.android.smartgreenhouse;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link HumiditySensorAdapter} knows how to create a list item layout for each sensor
 * in the data source (a list of {@link Sensors} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */

public class HumiditySensorAdapter extends ArrayAdapter<Sensors> {

    /**
     * Constructs a new {@link HumiditySensorAdapter}.
     *
     * @param context of the app
     * @param sensors is the list of sensors, which is the data source of the adapter
     */
    public HumiditySensorAdapter(Context context, List<Sensors> sensors) {
        super(context, 0, sensors);
    }

    /**
     * Returns a list item view that displays information about the sensor at the given position
     * in the list of sensors.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.sensor_list_item, parent, false);
        }

        // Find the sensor at the given position in the list of sensors
        Sensors currentSensor = getItem(position);

        // Find the TextView with view ID magnitude
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.reading);
        // Format the magnitude to show 1 decimal place
        int formattedMagnitude = (currentSensor.getMag());
        String formatMagnitude = Integer.toString(formattedMagnitude);
        String finalMagnitude = formatMagnitude + "%";
        // Display the magnitude of the current earthquake in that TextView
        magnitudeView.setText(finalMagnitude);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentSensor.getMag());
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        if (currentSensor.getMag() > 50) {
            // Find the TextView with view ID location
            TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.reading_status);
            // Display the location of the current earthquake in that TextView
            primaryLocationView.setText("high");
        } else if (currentSensor.getMag() < 38) {
            TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.reading_status);
            // Display the location of the current earthquake in that TextView
            primaryLocationView.setText("Low");
        } else {
            TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.reading_status);
            // Display the location of the current earthquake in that TextView
            primaryLocationView.setText("Normal");
        }

        TextView dateView = listItemView.findViewById(R.id.date);
        dateView.setText(currentSensor.getDate());

        TextView timeView = listItemView.findViewById(R.id.time);
        timeView.setText(currentSensor.getTime());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the color for the magnitude circle based on the intensity of the earthquake.
     *
     * @param magnitude of the earthquake
     */
    private int getMagnitudeColor(int magnitude) {
        int magnitudeColorResourceId;
        if (magnitude > 50) {

            magnitudeColorResourceId = R.color.magnitude10plus;
        } else if (magnitude < 38) {
            magnitudeColorResourceId = R.color.magnitude1;
        } else {
            magnitudeColorResourceId = R.color.primary_color;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
