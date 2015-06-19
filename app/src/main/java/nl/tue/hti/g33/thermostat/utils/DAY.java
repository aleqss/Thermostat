package nl.tue.hti.g33.thermostat.utils;

/**
 * Days of the week enumeration.
 * @author Alex, 17.06.2015.
 */
public enum DAY {

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

    /**
     * Associate a number with the days of the week.
     * @param id Order digit.
     */
    DAY(int id, String fullName, String shortName) {

        mId = id;
        mFullName = fullName;
        mShortName = shortName;
    }

    /**
     * Get the order digit for a day of the week.
     * @return Day of the week number.
     */
    public int getId() {

        return mId;
    }

    public String getFullName() {

        return mFullName;
    }

    public String getShortName() {

        return mShortName;
    }

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
                throw new IllegalArgumentException("DAY: Wrong id");
        }
    }

    public static DAY getByName(String name) {

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
                throw new IllegalArgumentException("DAY: wrong name");
        }
    }

    public static DAY getByShortName(String name) {

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
                throw new IllegalArgumentException("DAY: wrong short name");
        }
    }
}
