package nl.tue.hti.g33.thermostat.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
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
public class WebService extends Service {

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

    private final IBinder mBinder;

    public class LocalBinder extends Binder {

        public WebService getService() {

            return WebService.this;
        }
    }

    /**
     * Creates an IntentService.
     */
    public WebService() {

        super();
        mUriBuilder = new Uri.Builder();
        parser = new XmlParser();
        mBinder = new LocalBinder();
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public ParsedThermostat getData() {

        Log.v(LOG_TAG, "Getting data");
        mUriBuilder.scheme("http").authority(preferredURL).path(COURSE_URL);
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
                    Log.w(LOG_TAG, "Did not get status 200/404—some error here");
                    return null;
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                Log.w(LOG_TAG, "Something is wrong with the website");
                preferredURL = BACKUP_URL;
                return null;
            }
            Log.v(LOG_TAG, "About to parse data, should return soon");
            return parser.parse(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fetching data failed: " + e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void putData(String update, ParsedThermostat thermostat) {

        mUriBuilder.scheme("http").authority(preferredURL).path(COURSE_URL);
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
