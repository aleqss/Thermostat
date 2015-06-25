package nl.tue.hti.g33.thermostat.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;

/**
 * Represents a period of time (e.g. has starting time and end time). The period
 * is meant to happen within one day.
 *
 * @author Alex, 17.06.2015, HTI group 33, TU/e.
 */
public class Period implements Comparable<Period>, Serializable {

    private Time mStartTime;                                // Start Time
    private Time mEndTime;                                  // End Time

    private static final String LOG_TAG = "utils.Period";   // Logging identifier

    /**
     * Construct a new Period represented as startH:startM – endH:endM.
     * Parameters are checked: starting time should be strictly less than end
     * time; end time can be at most 24:00 (represents absent end time).
     *
     * @param startH Starting time, hours (0 – 23).
     * @param startM Starting time, minutes (0 – 59).
     * @param endH End time, hours (0 – 24).
     * @param endM End time, minutes (0 – 59).
     * @throws IllegalArgumentException if any of the arguments are out of
     * specified bounds.
     */
    public Period(int startH, int startM, int endH, int endM) {

        if (startH < 0 || startH > 23 || startM < 0 || startM > 59
                || endH < 0 || endH > 24 || endM < 0 || endM > 59
                || endH == 24 && endM != 0 || startH > endH
                || (startH == endH && startM >= endM)) {
            Log.e(LOG_TAG, "Period(int startH, int startM, int endH, int endM) " +
                    "got bad argument");
            throw new IllegalArgumentException(LOG_TAG + ": Period(" + startH
                    + ", " + startM + ", " + endH + ", " + endM + ")");
        }
        mStartTime = new Time(startH, startM);
        mEndTime = new Time(endH, endM);
    }

    /**
     * Constructs a new Period represented as start – end, where start and end
     * are the number of minutes since last midnight, and start is strictly less
     * than end. Value of 24 * 60 that end can have represents a period that does
     * not have an end (i.e. {@code UNDEFINED} {@link Time}.
     *
     * @param start Start of period as number of minutes since midnight
     *              (0 – 24 * 60 - 1)
     * @param end End of period as number of minutes since midnight (0 – 24 * 60),
     *            24 * 60 stands for {@code {@link Time}.UNDEFINED}.
     * @throws IllegalArgumentException if any of the arguments are out of
     * specified bounds.
     */
    public Period(int start, int end) {

        if (start < 0 || start >= 24 * 60 || end < 0 || end > 24 * 60
                || start >= end) {
            Log.e(LOG_TAG, "Period(int start, int end) got bad argument");
            throw new IllegalArgumentException(LOG_TAG + ": Period(" + start
                    + ", " + end + ")");
        }
        mStartTime = new Time(start);
        mEndTime = new Time(end);
    }

    /**
     * Constructs a new Period with start time and end time given as {@link Time}
     * objects. Start time should be strictly less than end time. None of the
     * arguments can be null.
     *
     * @param start Start time of the period.
     * @param end End time of the period.
     * @throws IllegalArgumentException if start >= end.
     */
    public Period(@NonNull Time start, @NonNull Time end) {

        if (start.compareTo(end) <= 0) {
            Log.e(LOG_TAG, "Period(Time start, Time end) got bad argument");
            throw new IllegalArgumentException(LOG_TAG + ": Period("
                    + start.toString() + ", " + end.toString() + ")");
        }
        mStartTime = start;
        mEndTime = end;
    }

    /**
     * Get the starting time as a {@link Time} object.
     *
     * @return Starting time as a non-null {@link Time} object.
     */
    public Time getStartingTimeT() {

        return mStartTime;
    }

    /**
     * Get the starting time in minutes since midnight.
     *
     * @return Starting time in minutes since 00:00 (0 – 24 * 60 - 1).
     */
    public int getStartingTime() {

        return mStartTime.getTime();
    }

    /**
     * Get the starting time hours since midnight.
     *
     * @return Hours value of the starting time (0 – 23).
     */
    public int getStartingTimeH() {

        return mStartTime.getTimeH();
    }

    /**
     * Get the starting time minutes since the start of an hour.
     *
     * @return Minutes value of the starting time (0 – 59).
     */
    public int getStartingTimeM() {

        return mStartTime.getTimeM();
    }

    /**
     * Get the end time as a {@link Time} object.
     *
     * @return End time as a non-null {@link Time} object.
     */
    public Time getEndTimeT() {

        return mEndTime;
    }

    /**
     * Get the end time in minutes since midnight.
     *
     * @return End time in minutes since 00:00 (0 – 24 * 60).
     */
    public int getEndTime() {

        return mEndTime.getTime();
    }

    /**
     * Get the end time hours since midnight.
     *
     * @return Hours value of the end time (0 – 24).
     */
    public int getEndTimeH() {

        return mEndTime.getTimeH();
    }

    /**
     * Get the end time minutes since the start of an hour.
     *
     * @return Minutes value of the end time (0 – 59).
     */
    public int getEndTimeM() {

        return mEndTime.getTimeM();
    }

    /**
     * Checks if the two time periods intersect. The arguments must be non-null.
     *
     * @param p Period to be compared with {@code this}.
     * @return True if two periods intersect.
     */
    public boolean intersects(@NonNull Period p) {

        int start = getStartingTime();
        int end = getEndTime();
        int pStart = p.getStartingTime();
        int pEnd = p.getEndTime();

        return (start <= pEnd && start >= pStart)
                || (start <= pStart && end >= pStart);
    }

    /**
     * Combines this period with period {@code p} if they intersect.
     * Make sure they DO intersect by calling {@link #intersects(Period p)}
     * before calling this function; p must be non-null.
     *
     * @param p Intersecting period to be combined with.
     * @return Combined period.
     */
    public Period combine(@NonNull Period p) {

        if (!intersects(p)) {
            Log.e(LOG_TAG, "Periods could not be combined: " + toString() + " "
                    + p.toString());
            throw new IllegalArgumentException(LOG_TAG + ": combine failed: "
                    + toString() + ", " + p.toString() + ".");
        }

        int start = getStartingTime();
        int pStart = p.getStartingTime();
        int end = getEndTime();
        int pEnd = p.getEndTime();

        start = (start < pStart ? start : pStart);
        end = (end > pEnd ? end : pEnd);

        return new Period(start, end);
    }

    /**
     * Returns textual representation of the period in format "HH:MM – HH:MM".
     *
     * @return String representation of the period.
     */
    @Override
    public String toString() {

        return mStartTime.toString() + " – " + mEndTime.toString();
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param p The non-null period to compare to this instance.
     * @return A negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}. Comparison is done based on starting time.
     */
    @Override
    public int compareTo(@NonNull Period p) {

        return getStartingTimeT().compareTo(p.getStartingTimeT());
    }
}
