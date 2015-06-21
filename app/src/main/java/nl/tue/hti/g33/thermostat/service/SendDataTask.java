package nl.tue.hti.g33.thermostat.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Alex on 21.06.2015.
 */
public class SendDataTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = "service.SendDataTask";

    @Override
    protected Void doInBackground(String... params) {

        String toSend = params[0];
        String urlString = params[1];
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/xml");
            //connection.setRequestProperty("Content-Length", String.valueOf(toSend.length()));
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            connection.connect();

            OutputStream output = connection.getOutputStream();
            PrintStream out = new PrintStream(output, true);
            out.print(toSend);
            out.close();
            output.close();
            int status = connection.getResponseCode();

            if (status != 200) {
                Log.e(LOG_TAG, "Connection gone wrong");
            }
            Log.v(LOG_TAG, "Successfully sent data");
        } catch (IOException e) {
            Log.e("WebService", "Uploading data failed: " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
