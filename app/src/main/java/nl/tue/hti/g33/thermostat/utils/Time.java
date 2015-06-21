package nl.tue.hti.g33.thermostat.utils;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Alex on 21.06.2015.
 */
public class Time implements Serializable {

    private int mH;
    private int mM;

    private static final String LOG_TAG = "utils.Time";

    public Time(int time) {

        mH = time / 60;
        mM = time % 60;
        if (mH < 0 || mH > 24 || mM < 0 || mM > 59 || mH == 24 && mM != 0) {
            Log.e(LOG_TAG, "Time(int time) went wrong");
            throw new IllegalArgumentException(LOG_TAG + "Time(int time) failed");
        }
    }

    public Time(int h, int m) {

        mH = h;
        mM = m;
        if (mH < 0 || mH > 24 || mM < 0 || mM > 59 || mH == 24 && mM != 0) {
            Log.e(LOG_TAG, "Time(int time) went wrong");
            throw new IllegalArgumentException(LOG_TAG + "Time(int time) failed");
        }
    }

    public int getTime() {

        return mH * 60 + mM;
    }

    public int getTimeH() {

        return mH;
    }

    public int getTimeM() {

        return mM;
    }

    @Override
    public String toString() {

        return (mH < 10 ? "0" : "") + Integer.toString(mH) + ":"
                + (mM < 10 ? "0" : "") + Integer.toString(mM);
    }
}
