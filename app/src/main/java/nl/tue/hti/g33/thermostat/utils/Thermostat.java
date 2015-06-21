package nl.tue.hti.g33.thermostat.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nl.tue.hti.g33.thermostat.parser.ParsedThermostat;
import nl.tue.hti.g33.thermostat.service.WebService;

/**
 * Represents all the basic functionality of the thermostat and serves as an API
 * between our UI code and server requests.
 * Also invokes all the data update requests and manages local storage; uses an
 * additional service to fetch / upload data from the server.
 * @author Alex, 17.06.2015.
 */
public class Thermostat {

    private static final String LOG_TAG = "utils.Thermostat";

    private static Thermostat instance;

    private ArrayList<DaySchedule> mWeekSchedule;
    private Temperature mDayTemperature;
    private Temperature mNightTemperature;
    private Temperature mCurrentTemperature;
    private Temperature mTargetTemperature;
    private boolean mWeekScheduleOn;
    private int mTime;
    private DAY mDayOfTheWeek;

    private boolean mFahrenheit = false;
    
    private ArrayList<ThermostatListener> mListener;
    private Context mContext;
    private Timer mTimer;

    private ServiceConnection mConnection;
    private WebService mService;
    private boolean mBound;

    public Thermostat(Context context) {

        mListener = new ArrayList<>();
        mContext = context;
        mWeekSchedule = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            mWeekSchedule.add(new DaySchedule());
        }

        mBound = false;
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                WebService.LocalBinder binder = (WebService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

                mBound = false;
            }
        };
        Intent serviceIntent = new Intent(mContext, WebService.class);
        mContext.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        mTimer = new Timer("WebFetcher", true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                downloadServer();
            }
        }, 0, 2000);

        instance = this;
    }

    public static Thermostat getInstance() {

        return instance;
    }

    public void addListener(ThermostatListener listener) {

        mListener.add(listener);
    }

    /**
     * Save new day temperature for week schedule.
     * @param temperature New day temperature.
     */
    public void updateDayTemperature(double temperature) {

        mDayTemperature = new Temperature(temperature, mFahrenheit);
        uploadServer("day_temperature");
    }

    /**
     * Save new night temperature for week schedule.
     * @param temperature New night temperature.
     */
    public void updateNightTemperature(double temperature) {

        mNightTemperature = new Temperature(temperature, mFahrenheit);
        uploadServer("night_temperature");
    }

    /**
     * Add a new period of day temperature to the week schedule.
     * @param dayOfTheWeek Day of the week to change.
     * @param dayPeriod Period of day temperature to be added.
     * @see DaySchedule
     */
    public void addSwitch(DAY dayOfTheWeek, Period dayPeriod) {

        DaySchedule schedule = mWeekSchedule.get(dayOfTheWeek.getId());
        schedule.addDayPeriod(dayPeriod);
        uploadServer("week_program");
    }

    /**
     * Deletes a period of day temperature from the week schedule.
     * @param dayOfTheWeek Day of the week to change.
     * @param dayPeriod Period of day temperature to remove.
     * @see DaySchedule
     */
    public void deleteSwitch(DAY dayOfTheWeek, Period dayPeriod) {

        DaySchedule schedule = mWeekSchedule.get(dayOfTheWeek.getId());
        schedule.deleteDayPeriod(dayPeriod);
        uploadServer("week_program");
    }

    /**
     * Enable / disable the vacation mode override.
     * @param on State of vacation mode override.
     */
    public void setVacationMode(boolean on, Temperature temperature) {

        if (on) {
            mTargetTemperature = temperature;
        }
        mWeekScheduleOn = !on;
        uploadServer("week_program_state");
    }

    /**
     * Set target temperature to {@code temperature} temporarily.
     * @param temperature New temperature.
     */
    public void setTemporaryOverride(Temperature temperature) {

        mTargetTemperature = temperature;
        uploadServer("target_temperature");
    }

    public int getCurrentTime() {

        return mTime;
    }

    public DAY getDayOfTheWeek() {

        return mDayOfTheWeek;
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

    public double getTargetTemperature() {

        return mTargetTemperature.getTemperature(mFahrenheit);
    }

    public boolean getWeekScheduleOn() {

        return mWeekScheduleOn;
    }

    public Iterable<DaySchedule> getWeekSchedule() {

        return mWeekSchedule;
    }

    public Iterable<Period> getDaySchedule(DAY dayOfTheWeek) {

        return mWeekSchedule.get(dayOfTheWeek.getId()).getSchedule();
    }

    /**
     * Makes all the API return and take temperatures in degrees Fahrenheit.
     * Thermostat then performs inner conversions on its own.
     * @param fahrenheit Use Fahrenheit instead of Celsius.
     */
    public void useFahrenheit(boolean fahrenheit) {

        mFahrenheit = fahrenheit;
    }

    private void downloadServer() {

        if (mBound) {
            // TODO: use asynctask?
            ParsedThermostat root = mService.getData();
            if (root == null) {
                Log.w(LOG_TAG, "Oops, Parsed Thermostat is null :(");
            }

            mCurrentTemperature = root.mCurrentTemperature;
            mTargetTemperature = root.mTargetTemperature;
            mDayTemperature = root.mDayTemperature;
            mNightTemperature = root.mNightTemperature;
            mDayOfTheWeek = root.mDayOfTheWeek;
            mTime = root.mTime;
            mWeekScheduleOn = root.mWeekScheduleOn;
            mWeekSchedule = root.mWeekSchedule;

            for (ThermostatListener listener : mListener) {
                listener.onThermostatUpdate(this);
            }
        }
    }

    private void uploadServer(String uploadOption) {

        if (mBound) {
            // TODO: use asynctask!
            ParsedThermostat copy = new ParsedThermostat();
            copy.mWeekSchedule = mWeekSchedule;
            copy.mWeekScheduleOn = mWeekScheduleOn;
            copy.mNightTemperature = mNightTemperature;
            copy.mDayTemperature = mDayTemperature;
            copy.mTargetTemperature = mTargetTemperature;

            mService.putData(uploadOption, copy);
        }
    }
}
