package nl.tue.hti.g33.thermostat.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Helper class that represents a period of time for the thermostat.
 * @author Alex, 17.06.2015
 */
public class Period implements Comparable<Period>, Parcelable, Serializable {

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

        if (0 > startH || startH > 23 || 0 > startM || startM > 59
                || 0 > endH || endH > 24 || 0 > endM || endM > 59
                || endH == 24 && endM != 0 || startH > endH
                || (startH == endH && startM > endM)) {
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
    public Time getStartingTimeT() {

        return new Time(mStartH, mStartM);
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
    public Time getEndTimeT() {

        return new Time(mEndH, mEndM);
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

        start = (start < pStart ? start : pStart);
        end = (end > pEnd ? end : pEnd);

        return new Period(start / 60, start % 60, end / 60, end % 60);
    }

    /**
     * Returns textual representation of the period in format "h:m – h:m".
     * @return String representation of the period.
     */
    @Override
    public String toString() {

        return mStartH + ":" + mStartM + " – " + mEndH + ":" + mEndM;
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

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mStartH);
        dest.writeInt(mStartM);
        dest.writeInt(mEndH);
        dest.writeInt(mEndM);
    }

    public static final Parcelable.Creator<Period> CREATOR = new Creator<Period>() {
        @Override
        public Period createFromParcel(Parcel source) {

            return new Period(source);
        }

        @Override
        public Period[] newArray(int size) {
            return new Period[size];
        }
    };

    private Period(Parcel in) {

        mStartH = in.readInt();
        mStartM = in.readInt();
        mEndH = in.readInt();
        mEndM = in.readInt();
    }
}
