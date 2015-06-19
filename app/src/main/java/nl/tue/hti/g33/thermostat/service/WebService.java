package nl.tue.hti.g33.thermostat.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.tue.hti.g33.thermostat.parser.ParsedThermostat;
import nl.tue.hti.g33.thermostat.parser.XmlParser;

/**
 * Service used to send / fetch data
 * @author Alex on 18.06.2015.
 */
public class WebService extends IntentService {

    private static final String LOG_TAG = "service.WebService";

    private static final String MODE = "mode";

    private static final String BASE_URL = "wwwis.win.tue.nl";
    private static final String BACKUP_URL = "pcwin889.win.tue.nl";
    private static final String COURSE_URL = "2id40-ws";
    private static final String THERMOSTAT_ID = "33"; // TODO: Can be NOT hardcoded
    private static final String SCHEDULE_URL = "weekProgram";
    private static final String SCHEDULE_STATE_URL = "weekProgramState";
    private static final String TARGET_URL = "targetTemperature";
    private static final String CURRENT_URL = "currentTemperature";
    private static final String NIGHT_URL = "nightTemperature";
    private static final String DAY_URL = "dayTemperature";

    private String preferredURL = BASE_URL;
    private Uri.Builder mUriBuilder;
    private XmlParser parser;

    /**
     * Creates an IntentService.
     */
    public WebService() {

        super("WebService");
        mUriBuilder = new Uri.Builder();
        parser = new XmlParser();
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getExtras().getString(MODE);
        switch (action) {
            case "GET":
                getData(intent);
                break;
            case "PUT":
                putData(intent);
                break;
            default:
                Log.w(LOG_TAG, "Wrong mode passed on in intent.");
        }
        mUriBuilder.path("");
    }

    private void getData(Intent intent) {

        Bundle extras = intent.getExtras();
        PendingIntent pendingIntent = extras.getParcelable("receiver");

        mUriBuilder.scheme("http").authority(preferredURL).appendPath(COURSE_URL);
        mUriBuilder.appendPath(THERMOSTAT_ID).appendPath("");
        HttpURLConnection connection = null; // TODO: fix malformed url

        try {
            URL url = new URL(mUriBuilder.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int status = connection.getResponseCode();

            if (status != 200) {
                if (status == 404) {
                    //TODO: create new thermostat
                }
                else {
                    preferredURL = BACKUP_URL;
                    return; //TODO: PendingIntent?
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                preferredURL = BACKUP_URL;
                return; // TODO: what do we do with the PendingIntent here?
            }

            ParsedThermostat root = parser.parse(inputStream);
            try {
                Intent result = new Intent();
                extras = new Bundle();
                extras.putSerializable("root", root);
                result.putExtras(extras);
                pendingIntent.send(getApplicationContext(), 0, result);
            } catch (PendingIntent.CanceledException e) {
                Log.e(LOG_TAG, "Failed to return data");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fetching data failed: " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void putData(Intent intent) {

        String update = intent.getStringExtra("upload");
        ParsedThermostat thermostat = (ParsedThermostat) intent.getSerializableExtra("thermostat");

        mUriBuilder.scheme("http").authority(preferredURL).appendPath(COURSE_URL);
        mUriBuilder.appendPath(THERMOSTAT_ID);
        String toSend;
        switch (update) {
            case "day_temperature":
                toSend = "<day_temperature>"
                        + thermostat.mDayTemperature.getTemperature(false)
                        + "</day_temperature>";
                mUriBuilder.appendPath(DAY_URL);
                break;
            case "night_temperature":
                toSend = "<night_temperature>"
                        + thermostat.mNightTemperature.getTemperature(false)
                        + "</night_temperature>";
                mUriBuilder.appendPath(NIGHT_URL);
                break;
            case "target_temperature":
                // Work around of a stupid bug on the server
                toSend = "<current_temperature>"
                        + thermostat.mTargetTemperature.getTemperature(false)
                        + "</current_temperature>";
                mUriBuilder.appendPath(CURRENT_URL);
                break;
            case "week_program_state":
                toSend = "<week_program_state>"
                        + (thermostat.mWeekScheduleOn ? "on" : "off")
                        + "</week_program_state>";
                mUriBuilder.appendPath(SCHEDULE_STATE_URL);
                break;
            case "week_program":
                toSend = parser.serialize(thermostat);
                mUriBuilder.appendPath(SCHEDULE_URL);
                break;
            default:
                Log.e(LOG_TAG, "Wrong request to WebService sender.");
                throw new IllegalArgumentException(LOG_TAG + "Uploading data impossible.");
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(mUriBuilder.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.connect();
            int status = connection.getResponseCode();

            if (status != 200) {
                if (status == 404) {
                    //TODO: create new thermostat
                }
                else {
                    preferredURL = BACKUP_URL;
                    return;
                }
            }

            OutputStream output = connection.getOutputStream();
            PrintStream out = new PrintStream(output, true);
            out.print(toSend);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Uploading data failed: " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
