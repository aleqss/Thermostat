package nl.tue.hti.g33.thermostat.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Represents a schedule of day/night switches for a day in a convenient way.
 * Also controls the amount of added switches.
 *
 * @author Alex, 17.06.2015, HTI group 33, TU/e.
 */
public class DaySchedule implements Serializable {

    private TreeSet<Period> mDayPeriods;

    private static final String LOG_TAG = "utils.DaySchedule";

    /**
     * Construct new instance of DaySchedule.
     */
    public DaySchedule() {

        mDayPeriods = new TreeSet<>();
    }

    /**
     * Add a period of day temperature after merging some periods if possible.
     * Make sure that the newly inserted period will not exceed the limit of 5.
     * This can be ensured if the user is only allowed to set 5 day periods.
     * After that he can only edit one of the rules. Editing should be
     * implemented as deletion -> adding a new interval. Hence, after editing
     * there will be at most 5 day periods again. The period may not be null.
     *
     * @param dayPeriod Period of day temperature to be added to the day
     *                  schedule.
     * @throws IllegalArgumentException if more than 5 day periods are being
     * added.
     */
    public void addDayPeriod(@NonNull Period dayPeriod) {

        Period prev = mDayPeriods.floor(dayPeriod);
        Period next = mDayPeriods.higher(dayPeriod);
        Period toAdd = dayPeriod;

        if (prev != null && prev.intersects(toAdd)) {
            mDayPeriods.remove(prev);
            toAdd = prev.combine(toAdd);
        }
        if (next != null && next.intersects(toAdd)) {
            mDayPeriods.remove(next);
            toAdd = next.combine(toAdd);
        }

        if (mDayPeriods.size() > 4) {
            Log.e(LOG_TAG, "addDayPeriod(dayPeriod): Too many switches—only 5 "
                    + "are allowed!");
            throw new IllegalArgumentException(LOG_TAG + "addDayPeriod("
                    + dayPeriod.toString() + ")");
        }
        mDayPeriods.add(toAdd);
    }

    /**
     * Delete an existing day period. It must correspond exactly to one of the
     * entries, otherwise no action will be taken. Removed period must be
     * non-null.
     *
     * @param dayPeriod Day period to be removed from the schedule.
     */
    public void deleteDayPeriod(@NonNull Period dayPeriod) {

        mDayPeriods.remove(dayPeriod);
    }

    /**
     * Returns the schedule as an iterable of periods.
     *
     * @return Schedule for the day in from of a list of periods.
     */
    public Iterable<Period> getSchedule() {

        return mDayPeriods;
    }

    /**
     * Get the string representation of the schedule with each period written on
     * one line.
     *
     * @return String representation of the day schedule.
     */
    @Override
    public String toString() {

        String printOut = "";
        for (Period p : mDayPeriods) {
            printOut += p.toString() + "\n";
        }
        return  printOut;
    }
}
