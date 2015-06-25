package nl.tue.hti.g33.thermostat.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;

/**
 * Days of the week enumeration.
 *
 * @author Alex, 17.06.2015, HTI group 33, TU/e.
 */
public enum DAY implements Serializable {

    MON(0, "Monday", "mon"),
    TUE(1, "Tuesday", "tue"),
    WED(2, "Wednesday", "wed"),
    THU(3, "Thursday", "thu"),
    FRI(4, "Friday", "fri"),
    SAT(5, "Saturday", "sat"),
    SUN(6, "Sunday", "sun");

    private final int mId;
    private final String mFullName;
    private final String mShortName;

    private static final String LOG_TAG = "utils.DAY";

    /**
     * Associate a number, short name and full name with the days of the week.
     * Arguments can not be null.
     *
     * @param id Order digit, with 0 for Monday.
     * @param fullName Full name of the weekday (e.g. "Sunday").
     * @param shortName Short name of the weekday (e.g. "sun").
     */
    DAY(int id, @NonNull String fullName, @NonNull String shortName) {

        mId = id;
        mFullName = fullName;
        mShortName = shortName;
    }

    /**
     * Get the order digit for a day of the week (e.g. 0 for Monday).
     *
     * @return Day of the week number.
     */
    public int getId() {

        return mId;
    }

    /**
     * Get the full name for a day of the week (e.g. "Monday").
     *
     * @return Day of the week name.
     */
    public String getFullName() {

        return mFullName;
    }

    /**
     * Get the short name for a day of the week (e.g. "sun").
     *
     * @return Day of the week shortened version.
     */
    public String getShortName() {

        return mShortName;
    }

    /**
     * Return a DAY object based on id value (e.g. DAY.MON for 0).
     *
     * @param id Order digit.
     * @return Corresponding DAY.
     * @throws IllegalArgumentException if id is out of bounds (0 – 6).
     */
    public static DAY getById(int id) {

        switch (id) {
            case 0:
                return DAY.MON;
            case 1:
                return DAY.TUE;
            case 2:
                return DAY.WED;
            case 3:
                return DAY.THU;
            case 4:
                return DAY.FRI;
            case 5:
                return DAY.SAT;
            case 6:
                return DAY.SUN;
            default:
                Log.e(LOG_TAG, "DAY.getById(int id) got wrong argument");
                throw new IllegalArgumentException(LOG_TAG + ": getById(" + id
                        + ")");
        }
    }

    /**
     * Return a DAY object based on the name of the day (e.g. DAY.MON for
     * "Monday"). Argument cannot be null.
     *
     * @param name Day of the week full name.
     * @return Corresponding DAY.
     * @throws IllegalArgumentException if the name is not a name of the day of
     * the week.
     */
    public static DAY getByName(@NonNull String name) {

        name = name.toLowerCase();
        switch (name) {
            case "monday":
                return DAY.MON;
            case "tuesday":
                return DAY.TUE;
            case "wednesday":
                return DAY.WED;
            case "thursday":
                return DAY.THU;
            case "friday":
                return DAY.FRI;
            case "saturday":
                return DAY.SAT;
            case "sunday":
                return DAY.SUN;
            default:
                Log.e(LOG_TAG, "DAY.getByName(String name) got wrong argument");
                throw new IllegalArgumentException(LOG_TAG + ": getByName("
                        + name + ")");
        }
    }

    /**
     * Return a DAY object based on the short name of the day (e.g. DAY.MON for
     * "mon"). Argument can not be null.
     *
     * @param name Day of the week short name.
     * @return Corresponding DAY.
     * @throws IllegalArgumentException if the name is not a name of the day of
     * the week.
     */
    public static DAY getByShortName(@NonNull String name) {

        name = name.toLowerCase();
        switch (name) {
            case "mon":
                return DAY.MON;
            case "tue":
                return DAY.TUE;
            case "wed":
                return DAY.WED;
            case "thu":
                return DAY.THU;
            case "fri":
                return DAY.FRI;
            case "sat":
                return DAY.SAT;
            case "sun":
                return DAY.SUN;
            default:
                Log.e(LOG_TAG, "DAY.getByShortName(String name) got wrong "
                        + "argument");
                throw new IllegalArgumentException(LOG_TAG + ": getByShortName("
                        + name + ")");
        }
    }
}
