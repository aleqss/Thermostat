package nl.tue.hti.g33.thermostat.utils;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Represents temperature for the thermostat in a convenient form.
 *
 * @author Alex, 17.06.2015.
 */
public class Temperature implements Comparable<Temperature> {

    private double mTemperature;

    private static final String LOG_TAG = "utils.Temperature";

    /**
     * Create a new temperature between 5.0 °C and 30.0 °C.
     *
     * @param temperature Temperature value to be assigned after checks.
     * @param fahrenheit Set to true if the value is in degrees Fahrenheit.
     * @throws IllegalArgumentException if resulting temperature in °C is out of
     * specified bounds.
     */
    public Temperature(double temperature, boolean fahrenheit) {

        double celsiusTemperature = temperature;
        if (fahrenheit) {
            celsiusTemperature = convertToCelsius(temperature);
        }

        celsiusTemperature = roundToTenth(celsiusTemperature);

        if (celsiusTemperature < 5.0 || celsiusTemperature > 30.0) {
            Log.e(LOG_TAG, "Temperature(double temperature, boolean fahrenheit)"
                    + " got bad argument");
            throw new IllegalArgumentException(LOG_TAG + ": Temperature("
                    + temperature + ", " + fahrenheit + ")");
        }

        mTemperature = celsiusTemperature;
    }

    /**
     * Get the stored temperature.
     *
     * @param fahrenheit Set to true if you want the value in degrees Fahrenheit.
     * @return Temperature in the desired format, round to tenth.
     */
    public double getTemperature(boolean fahrenheit) {

        if (fahrenheit) {
            return roundToTenth(convertToFahrenheit(mTemperature));
        }
        return mTemperature;
    }

    /**
     * Converts temperature from degrees Celsius to degrees Fahrenheit.
     *
     * @param temperature Temperature in degrees Celsius.
     * @return Temperature in degrees Fahrenheit.
     */
    private double convertToFahrenheit(double temperature) {

        return temperature * 9 / 5 + 32;
    }

    /**
     * Converts temperature from degrees Fahrenheit to degrees Celsius.
     *
     * @param temperature Temperature in degrees Fahrenheit.
     * @return Temperature in degrees Celsius.
     */
    private double convertToCelsius(double temperature) {

        return (temperature - 32) * 5 / 9;
    }

    /**
     * Trick to truncate a number to tenth.
     *
     * @param number Any double to truncate.
     * @return The same number truncated to tenth.
     */
    private double roundToTenth(double number) {

        return ((int) (number * 10.0)) / 10.0;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another The object to compare to this instance.
     * @return A negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     */
    @Override
    public int compareTo(@NonNull Temperature another) {

        if (getTemperature(false) - another.getTemperature(false) < 0) {
            return -1;
        }
        else if (getTemperature(false) - another.getTemperature(false) > 0) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
