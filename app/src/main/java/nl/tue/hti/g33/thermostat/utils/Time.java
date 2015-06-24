package nl.tue.hti.g33.thermostat.utils;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Represents time in format HH:MM.
 *
 * Despite the availability of many classes in both Java and Android library to
 * represent time, most of them have very complicated API, which is unnecessary
 * in our case.
 * Time UNDEFINED is equivalent to 24:00; nevertheless, please use UNDEFINED
 * instead of 24:00 whenever possible for the sake of consistency and clarity.
 *
 * @author Aleksandr Popov, 21.06.2015, HTI group 33, TU/e.
 */
public class Time implements Comparable<Time> {

    private int mH;                                         // Hours (0 – 24)
    private int mM;                                         // Minutes (0 – 59)

    private static final String LOG_TAG = "utils.Time";     // Logging identifier

    public static final Time UNDEFINED = new Time(24, 0);   // Undefined time

    /**
     * Construct a Time object given a single integer representing the number of
     * minutes of the current day since 00:00 (i.e. if it is 13:45, then
     * {@code time} will be 13 * 60 + 45 = 825.
     *
     * @param time Minutes since midnight of the current day.
     * @throws IllegalArgumentException if the argument is out of specified
     * bounds.
     */
    public Time(int time) {

        if (time < 0 || time > 24 * 60) {
            Log.e(LOG_TAG, "Time(int time) got bad argument");
            throw new IllegalArgumentException(LOG_TAG + ": Time("+ time +")");
        }
        mH = time / 60;
        mM = time % 60;
    }

    /**
     * Constructs a Time object given current hour ({@code h}) and minute
     * ({@code m}). Constraints are usual for this kind of time; however,
     * 24:00 is also allowed to represent undefined time. Whenever possible,
     * use constant {@code UNDEFINED} instead of calling {@code new Time(24, 0)}.
     *
     * @param h Number of hours since midnight (0 – 24).
     * @param m Number of minutes since last hour (0 – 59).
     * @throws IllegalArgumentException if the arguments are out of specified
     * bounds.
     */
    public Time(int h, int m) {

        if (h < 0 || h > 24 || m < 0 || m > 59 || h == 24 && m != 0) {
            Log.e(LOG_TAG, "Time(int h, int m) got bad argument");
            throw new IllegalArgumentException(LOG_TAG + ": Time(" + h + ", "
                    + m + ")");
        }
        mH = h;
        mM = m;
    }

    /**
     * Get the time stored in this instance of Time in form of the number of
     * minutes since last midnight (i.e. 0 – 24 * 60).
     *
     * @return Number of minutes since midnight.
     */
    public int getTime() {

        return mH * 60 + mM;
    }

    /**
     * Get the hour stored in this instance of time (0 – 24). Returns 24 for
     * {@code UNDEFINED}.
     *
     * @return Number of hours since midnight.
     */
    public int getTimeH() {

        return mH;
    }

    /**
     * Get the minute stored in this instance of time (0 – 59). Returns 0 for
     * {@code UNDEFINED}.
     *
     * @return Number of minutes since the start of last hour.
     */
    public int getTimeM() {

        return mM;
    }

    /**
     * Represent the Time object as a String. {@code UNDEFINED} is represented as
     * "24:00".
     *
     * @return String in format HH:MM.
     */
    @Override
    public String toString() {

        return (mH < 10 ? "0" : "") + Integer.toString(mH) + ":"
                + (mM < 10 ? "0" : "") + Integer.toString(mM);
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another The non-null Time to compare to this instance of Time.
     * @return A negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another} (here "less" means "less minutes since midnight").
     */
    @Override
    public int compareTo(@NonNull Time another) {

        return getTime() - another.getTime();
    }
}
