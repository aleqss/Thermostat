package nl.tue.hti.g33.thermostat.parser;

import java.io.Serializable;
import java.util.ArrayList;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.DaySchedule;
import nl.tue.hti.g33.thermostat.utils.Temperature;

/**
 * @author Alex on 19.06.2015.
 */
public class ParsedThermostat implements Serializable {

    public ArrayList<DaySchedule> mWeekSchedule;
    public Temperature mDayTemperature;
    public Temperature mNightTemperature;
    public Temperature mCurrentTemperature;
    public Temperature mTargetTemperature;
    public boolean mWeekScheduleOn;
    public int mTime;
    public DAY mDayOfTheWeek;
}
