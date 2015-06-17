package nl.tue.hti.g33.thermostat.utils;

import java.util.ArrayList;

/**
 * Represents a schedule of day/night switches for a day in a convenient way.
 * Also controls the amount of added switches.
 * @author Alex, 17.06.2015.
 */
public class DaySchedule {

    private ArrayList<Period> mDayPeriods;

    public DaySchedule() {

        mDayPeriods = new ArrayList<>();
    }
}
