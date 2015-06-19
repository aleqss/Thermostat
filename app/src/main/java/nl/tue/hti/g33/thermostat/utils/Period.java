package nl.tue.hti.g33.thermostat.utils;

import android.util.Log;

import java.util.ArrayList;

/**
 * Helper class that represents a period of time for the thermostat.
 * @author Alex, 17.06.2015
 */
public class Period implements Comparable<Period> {

    private int mStartH;
    private int mStartM;
    private int mEndH;
    private int mEndM;

    private static final String LOG_TAG = "utils.Period";

    /**
     * Construct a new Period; parameters are checked.
     * @param startH Starting time, hours.
     * @param startM Starting time, minutes.
     * @param endH End time, hours.
     * @param endM End time, minutes.
     * @throws IllegalArgumentException
     */
    public Period(int startH, int startM, int endH, int endM) {

        if (0 < startH || startH > 23 || 0 < startM || startM > 59
                || 0 < endH || endH > 24 || 0 < endM || endM > 59
                || endH == 24 && endM != 0 || startH > endH
                || (startH == endH && endH > endM)) {
            Log.e(LOG_TAG, "Illegal arguments used in constructor");
            throw new IllegalArgumentException(LOG_TAG + ": constructor failed");
        }
        mStartH = startH;
        mStartM = startM;
        mEndH = endH;
        mEndM = endM;
    }

    /**
     * Get the starting time.
     * @return Starting time in format {hh, mm} in an {@link java.util.ArrayList}.
     */
    public ArrayList<Integer> getListStartingTime() {

        ArrayList<Integer> stTime =  new ArrayList<>(2);
        stTime.set(0, mStartH);
        stTime.set(1, mStartM);
        return stTime;
    }

    /**
     * Get the starting time.
     * @return Starting time in minutes since 00:00.
     */
    public int getStartingTime() {

        return mStartH * 60 + mStartM;
    }

    /**
     * Get the starting time.
     * @return Hours value of the starting time.
     */
    public int getStartingTimeH() {

        return mStartH;
    }

    /**
     * Get the starting time.
     * @return Minutes value of the starting time.
     */
    public int getStartingTimeM() {

        return mStartM;
    }

    /**
     * Get the end time.
     * @return End time in format {hh, mm} in an {@link java.util.ArrayList}.
     */
    public ArrayList<Integer> getListEndTime() {

        ArrayList<Integer> endTime =  new ArrayList<>(2);
        endTime.set(0, mEndH);
        endTime.set(1, mEndM);
        return endTime;
    }

    /**
     * Get the end time.
     * @return End time in minutes since 00:00.
     */
    public int getEndTime() {

        return mEndH * 60 + mEndM;
    }

    /**
     * Get the end time.
     * @return Hours value of the end time.
     */
    public int getEndTimeH() {

        return mEndH;
    }

    /**
     * Get the end time.
     * @return Minutes value of the end time.
     */
    public int getEndTimeM() {

        return mEndM;
    }

    /**
     * Checks if the two time periods intersect.
     * @param p Period to be compared with {@code this}.
     * @return True if two periods intersect.
     */
    public boolean intersects(Period p) {

        int start = getStartingTime();
        int end = getEndTime();
        int pStart = p.getStartingTime();
        int pEnd = p.getEndTime();

        return (start <= pEnd && start >= pStart) || (start <= pStart && end >= pStart);
    }

    /**
     * Combines this period with period {@code p} if they intersect.
     * Make sure they DO intersect by calling {@link #intersects(Period p)} before
     * calling this function.
     * @param p Intersecting period to be combined with.
     * @return Combined period.
     */
    public Period combine(Period p) {

        if (!intersects(p)) {
            Log.e(LOG_TAG, "Periods could not be combined: " + toString() + " " + p.toString());
            throw new IllegalArgumentException(LOG_TAG + ": combine failed.");
        }

        int start = getStartingTime();
        int pStart = p.getStartingTime();
        int end = getEndTime();
        int pEnd = p.getEndTime();

        start = Math.min(start, pStart);
        end = Math.max(end, pEnd);

        return new Period(start / 60, start % 60, end / 60, end % 60);
    }

    /**
     * Returns textual representation of the period in format "h:m – h:m".
     * @return String representation of the period.
     */
    @Override
    public String toString() {

        return mStartH + ":" + mStartH + " – " + mEndH + ":" + mEndM;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param p the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Period p) {

        return getStartingTime() - p.getStartingTime();
    }
}
