package nl.tue.hti.g33.thermostat.utils;

/**
 * Days of the week enumeration.
 * @author Alex, 17.06.2015.
 */
public enum DAY {

    MON(0),
    TUE(1),
    WED(2),
    THU(3),
    FRI(4),
    SAT(5),
    SUN(6);

    private final int mId;

    /**
     * Associate a number with the days of the week.
     * @param id Order digit.
     */
    DAY(int id) {

        mId = id;
    }

    /**
     * Get the order digit for a day of the week.
     * @return Day of the week number.
     */
    public int getId() {

        return mId;
    }
}
