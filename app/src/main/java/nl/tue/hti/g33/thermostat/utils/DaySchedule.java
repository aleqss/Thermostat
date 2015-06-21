package nl.tue.hti.g33.thermostat.utils;

import android.util.Log;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Represents a schedule of day/night switches for a day in a convenient way.
 * Also controls the amount of added switches.
 * @author Alex, 17.06.2015.
 */
public class DaySchedule implements Serializable {

    private TreeSet<Period> mDayPeriods;

    private static final String LOG_TAG = "utils.DaySchedule";

    public DaySchedule() {

        mDayPeriods = new TreeSet<>();
    }

    /**
     * Add a period of day temperature after merging some periods if possible.
     * Make sure that the newly inserted period will not exceed the limit of 5.
     * This can be ensured if the user is only allowed to set 5 day periods.
     * After that he can only edit one of the rules. Editing should be implemented
     * as deletion -> adding a new interval. Hence, after editing there will be at
     * most 5 day periods again.
     * @param dayPeriod Period of day temperature to be added to the day schedule.
     * @throws IllegalArgumentException
     */
    public void addDayPeriod(Period dayPeriod) {

        Period prev = mDayPeriods.lower(dayPeriod);
        Period next = mDayPeriods.ceiling(dayPeriod);
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
            Log.e(LOG_TAG, "Too many switches—only 5 are allowed!");
            throw new IllegalArgumentException(LOG_TAG + "Too many switches");
        }
        mDayPeriods.add(toAdd);
    }

    /**
     * Delete an existing day period. It must correspond exactly to one of the
     * entries, otherwise no action will be taken.
     * @param dayPeriod Day period to be removed from the schedule.
     */
    public void deleteDayPeriod(Period dayPeriod) {

        mDayPeriods.remove(dayPeriod);
    }

    /**
     * Returns the schedule as an iterable of periods.
     * @return Schedule for the day in from of a list of periods.
     */
    public Iterable<Period> getSchedule() {

        return mDayPeriods;
    }
}
