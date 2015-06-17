package nl.tue.hti.g33.thermostat.utils;

import android.util.Log;

/**
 * Created by Alex on 17.06.2015.
 */
public class Temperature {

    private double mTemperature;

    private static final String LOG_TAG = "utils.Temperature";

    public Temperature(double temperature, boolean fahrenheit) {

        double celsiusTemperature = roundToTenth(temperature);
        if (fahrenheit) {
            celsiusTemperature = convertToCelsius(temperature);
        }

        if (celsiusTemperature < 5.0 || celsiusTemperature > 30.0) {
            Log.e(LOG_TAG, "Temperature is out of range");
            throw new IllegalArgumentException(LOG_TAG + ": constructor failed");
        }
    }

    public double getTemperature(boolean fahrenheit) {

        if (fahrenheit) {
            return convertToFahrenheit(mTemperature);
        }
        return mTemperature;
    }

    private double convertToFahrenheit(double temperature) {

        return roundToTenth(temperature * 9 / 5 + 32);
    }

    private double convertToCelsius(double temperature) {

        return roundToTenth((temperature - 32) * 5 / 9);
    }

    private double roundToTenth(double number) {

        return ((int) (number * 10.0)) / 10.0;
    }
}
