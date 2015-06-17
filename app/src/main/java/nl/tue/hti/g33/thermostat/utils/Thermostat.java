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

    private ArrayList<DaySchedule> mWeekSchedule;
    private Temperature mDayTemperature;
    private Temperature mNightTemperature;
    private Temperature mVacationTemperature;
    private Temperature mCurrentTemperature;

    private boolean mFahrenheit = false;

    public void updateVacationTemperature(double temperature) {

        Temperature vacationTemperature = new Temperature(temperature, mFahrenheit);
        mVacationTemperature = vacationTemperature;
        updateServer();
    }

    public void updateDayTemperature(double temperature) {

        Temperature dayTemperature = new Temperature(temperature, mFahrenheit);
        mDayTemperature = dayTemperature;
        updateServer();
    }

    public void updateNightTemperature(double temperature) {

        Temperature nightTemperature = new Temperature(temperature, mFahrenheit);
        mNightTemperature = nightTemperature;
        updateServer();
    }

    public void addSwitch(DAY dayOfTheWeek, Period dayPeriod) {
        DaySchedule schedule = mWeekSchedule.get(dayOfTheWeek.getId());
        schedule.addDayPeriod(dayPeriod);
    }

    public void deleteSwitch(DAY dayOfTheWeek, Period dayPeriod) {
        DaySchedule schedule = mWeekSchedule.get(dayOfTheWeek.getId());
        schedule.deleteDayPeriod(dayPeriod);
    }

    public double getCurrentTemperature() {

        return mCurrentTemperature.getTemperature(mFahrenheit);
    }

    public double getDayTemperature() {

        return mDayTemperature.getTemperature(mFahrenheit);
    }

    public double getNightTemperature() {

        return mNightTemperature.getTemperature(mFahrenheit);
    }

    public double getVacationTemperature() {

        return mVacationTemperature.getTemperature(mFahrenheit);
    }

    /**
     * Makes all the API return and take temperatures in degrees Fahrenheit.
     * Thermostat then performs inner conversions on its own.
     * @param fahrenheit Use Fahrenheit instead of Celsius.
     */
    public void useFahrenheit(boolean fahrenheit) {

        mFahrenheit = fahrenheit;
    }

    private void updateServer() {
        // TODO: think about what and how we send and how we do this altogether
        // TODO: maybe use a service to constantly send and fetch information?
        // TODO: or only do this on updates?
    }
}
