package nl.tue.hti.g33.thermostat.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class Thermostat extends BroadcastReceiver {

    private static final String LOG_TAG = "utils.Thermostat";

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
    private Timer timer;

    public Thermostat() {

        Log.e(LOG_TAG, "This should not be called.");
        // TODO: This IS called. Do something about BroadcastReceiver.
    }

    public Thermostat(Context context) {

        mListener = new ArrayList<>();
        mContext = context;
        mWeekSchedule = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            mWeekSchedule.add(new DaySchedule());
        }
        timer = new Timer("WebFetcher", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                downloadServer();
            }
        }, 0, 2000);
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

        Intent broadcastReceiver = new Intent(mContext, Thermostat.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, broadcastReceiver, 0);
        Bundle bundle = new Bundle();
        bundle.putParcelable("receiver", pendingIntent);
        bundle.putString("mode", "GET");
        Intent serviceIntent = new Intent(mContext, WebService.class);
        serviceIntent.putExtras(bundle);
        mContext.startService(serviceIntent);
    }

    private void uploadServer(String uploadOption) {

        ParsedThermostat copy = new ParsedThermostat();
        copy.mWeekSchedule = mWeekSchedule;
        copy.mWeekScheduleOn = mWeekScheduleOn;
        copy.mNightTemperature = mNightTemperature;
        copy.mDayTemperature = mDayTemperature;
        copy.mTargetTemperature = mTargetTemperature;

        Intent serviceIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("mode", "PUT");
        bundle.putString("upload", uploadOption);
        bundle.putSerializable("thermostat", copy);
        serviceIntent.putExtras(bundle);
        mContext.startService(serviceIntent);
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        ParsedThermostat root = (ParsedThermostat) extras.getSerializable("root");

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
