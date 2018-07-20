package com.amrelmorapplications.android.smartgreenhouse;

/**
 * An {@link Sensors} object contains information related to a single sensor.
 */
public class Sensors {

    /**
     * Magnitude of the earthquake
     */
    private String mMagnitude;

    /**
     * Location of the earthquake
     */
    private String mLocation;

    /**
     * Time of the earthquake
     */
    private long mTimeInMilliseconds;

    /**
     * Website URL of the earthquake
     */
    private String mUrl;

    private String mFanStatus;

    private String mLampStatus;

    private String mPumpStatus;

    private int mMag;

    private String mDate;

    private String mTime;

    /**
     * Constructs a new {@link Sensors} object.
     *
     * @param magnitude          is the magnitude (size) of the earthquake
     * @param location           is the location where the earthquake happened
     * @param timeInMilliseconds is the time in milliseconds (from the Epoch) when the
     *                           earthquake happened
     * @param url                is the website URL to find more details about the earthquake
     */
    public Sensors(String magnitude, String location, long timeInMilliseconds, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }

    public Sensors(String magnitude, String location) {
        mMagnitude = magnitude;
        mLocation = location;
    }

    public Sensors(int mag, String location, String date, String time) {
        mMag = mag;
        mLocation = location;
        mDate = date;
        mTime = time;
    }

    public Sensors(String fanStatus, String lampStatus, String pumpStatus) {
        mFanStatus = fanStatus;
        mLampStatus = lampStatus;
        mPumpStatus = pumpStatus;
    }

    /**
     * Returns the magnitude of the earthquake.
     */
    public String getMagnitude() {
        return mMagnitude;
    }

    /**
     * Returns the location of the earthquake.
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Returns the time of the earthquake.
     */
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /**
     * Returns the website URL to find more information about the earthquake.
     */
    public String getUrl() {
        if (mUrl != null)
            return mUrl;
        else return null;
    }

    public String getFanStatus() {
        return mFanStatus;
    }

    public String getmLampStatus() {
        return mLampStatus;
    }

    public String getmPumpStatus() {
        return mPumpStatus;
    }

    public int getMag() {
        return mMag;
    }

    public String getTime() {
        return mTime;
    }

    public String getDate() {
        return mDate;
    }
}


