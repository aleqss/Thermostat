package nl.tue.hti.g33.thermostat.utils;

import java.util.ArrayList;

/**
 * Represents all the basic functionality of the thermostat and serves as an API
 * between our UI code and server requests.
 * Also invokes all the data update requests and manages local storage; uses an
 * additional service to fetch / upload data from the server.
 * @author Alex, 17.06.2015.
 */
public class Thermostat {

    private static ArrayList<DaySchedule> mWeekSchedule;
    private static Temperature mDayTemperature;
    private static Temperature mNightTemperature;
    private static Temperature mVacationTemperature;
    private static Temperature mCurrentTemperature;
    private static Temperature mTargetTemperature;
    private static boolean mVacationModeOn;
    private static boolean mWeekScheduleOn;
    private static int time;
    // TODO: get time from server

    private static boolean mFahrenheit = false;

    /**
     * Save new vacation mode temperature.
     * @param temperature New vacation mode temperature.
     */
    public static void updateVacationTemperature(double temperature) {

        Temperature vacationTemperature = new Temperature(temperature, mFahrenheit);
        mVacationTemperature = vacationTemperature;
        updateServer();
    }

    /**
     * Save new day temperature for week schedule.
     * @param temperature New day temperature.
     */
    public static void updateDayTemperature(double temperature) {

        Temperature dayTemperature = new Temperature(temperature, mFahrenheit);
        mDayTemperature = dayTemperature;
        updateServer();
    }

    /**
     * Save new night temperature for week schedule.
     * @param temperature
     */
    public static void updateNightTemperature(double temperature) {

        Temperature nightTemperature = new Temperature(temperature, mFahrenheit);
        mNightTemperature = nightTemperature;
        updateServer();
    }

    /**
     * Add a new period of day temperature to the week schedule.
     * @param dayOfTheWeek Day of the week to change.
     * @param dayPeriod Period of day temperature to be added.
     * @see DaySchedule
     */
    public static void addSwitch(DAY dayOfTheWeek, Period dayPeriod) {

        DaySchedule schedule = mWeekSchedule.get(dayOfTheWeek.getId());
        schedule.addDayPeriod(dayPeriod);
    }

    /**
     * Deletes a period of day temperature from the week schedule.
     * @param dayOfTheWeek Day of the week to change.
     * @param dayPeriod Period of day temperature to remove.
     * @see DaySchedule
     */
    public static void deleteSwitch(DAY dayOfTheWeek, Period dayPeriod) {

        DaySchedule schedule = mWeekSchedule.get(dayOfTheWeek.getId());
        schedule.deleteDayPeriod(dayPeriod);
    }

    /**
     * Enable / disable the vacation mode override.
     * @param on State of vacation mode override.
     */
    public static void setVacationMode(boolean on) {

        // TODO: check how this works on the server
        if (on) {
            mTargetTemperature = mVacationTemperature;
        }
        mWeekScheduleOn = !on;
    }

    /**
     * Set target temperature to {@code temperature} temporarily.
     * @param temperature New temperature.
     */
    public static void setTemporaryOverride(Temperature temperature) {

        // TODO: check how this works on the server
        mTargetTemperature = temperature;
    }

    public static double getCurrentTemperature() {

        return mCurrentTemperature.getTemperature(mFahrenheit);
    }

    public static double getDayTemperature() {

        return mDayTemperature.getTemperature(mFahrenheit);
    }

    public static double getNightTemperature() {

        return mNightTemperature.getTemperature(mFahrenheit);
    }

    public static double getVacationTemperature() {

        return mVacationTemperature.getTemperature(mFahrenheit);
    }

    public static boolean getWeekScheduleState() {

        return mWeekScheduleOn;
    }

    public static boolean getVacationModeOn() {

        return mVacationModeOn;
    }

    public static Iterable<DaySchedule> getWeekSchedule() {

        return mWeekSchedule;
    }

    public static Iterable<Period> getDaySchedule(DAY dayOfTheWeek) {

        return mWeekSchedule.get(dayOfTheWeek.getId()).getSchedule();
        // TODO: mWeekSchedule is null here. Initialise the thermostat. Make it non-static maybe?
    }

    /**
     * Makes all the API return and take temperatures in degrees Fahrenheit.
     * Thermostat then performs inner conversions on its own.
     * @param fahrenheit Use Fahrenheit instead of Celsius.
     */
    public static void useFahrenheit(boolean fahrenheit) {

        mFahrenheit = fahrenheit;
    }

    private static void updateServer() {
        // TODO: think about what and how we send and how we do this altogether
        // TODO: maybe use a service to constantly send and fetch information?
        // TODO: or only do this on updates?
    }
}
