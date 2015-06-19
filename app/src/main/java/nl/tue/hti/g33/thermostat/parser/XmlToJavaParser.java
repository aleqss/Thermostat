package nl.tue.hti.g33.thermostat.parser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.DaySchedule;
import nl.tue.hti.g33.thermostat.utils.Period;
import nl.tue.hti.g33.thermostat.utils.Temperature;

/**
 * Make use of JAXB annotated classes generated from schema.xsd.
 * Parse the xml as an input stream according to that schema.
 * Return result as a packed object.
 * @author Alex, 18.06.2015.
 */
public class XmlToJavaParser {

    private static final String LOG_TAG = "parser.XmlToJavaParser";

    private XmlPullParser parser;

    public XmlToJavaParser() {

        parser = Xml.newPullParser();
    }

    public ParsedThermostat parse(InputStream inputStream) {

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readThermostat(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "XML parsing failed");
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException while parsing XML");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to close input stream while parsing XML");
            }
        }
        return null;
    }

    private ParsedThermostat readThermostat(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        ParsedThermostat thermostat = new ParsedThermostat();

        parser.require(XmlPullParser.START_TAG, null, "thermostat");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("current_day")) {
                thermostat.mDayOfTheWeek = readDay(parser);
            }
            else if (name.equals("time")) {
                thermostat.mTime = readTime(parser);
            }
            else if (name.equals("current_temperature")) {
                thermostat.mCurrentTemperature = readCurrentTemperature(parser);
            }
            else if (name.equals("target_temperature")) {
                thermostat.mTargetTemperature = readTargetTemperature(parser);
            }
            else if (name.equals("day_temperature")) {
                thermostat.mDayTemperature = readDayTemperature(parser);
            }
            else if (name.equals("night_temperature")) {
                thermostat.mNightTemperature = readNightTemperature(parser);
            }
            else if (name.equals("week_program_state")) {
                thermostat.mWeekScheduleOn = readWeekProgramState(parser);
            }
            else if (name.equals("week_program")) {
                thermostat.mWeekSchedule = readWeekProgram(parser);
            }
            else {
                throw new XmlPullParserException("Ill-formed XML given to parse");
            }
        }
        return thermostat;
    }

    private DAY readDay(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "current_day");
        String day = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "current_day");
        return nameToDay(day);
    }

    private int readTime(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "time");
        String time = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "time");
        return Integer.getInteger(time.substring(0, 2)) *60
                + Integer.getInteger(time.substring(3, 5));
    }

    private Temperature readCurrentTemperature(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "current_temperature");
        String currentTemperature = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "current_temperature");
        return new Temperature(Double.parseDouble(currentTemperature), false);
    }

    private Temperature readTargetTemperature(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "target_temperature");
        String targetTemperature = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "target_temperature");
        return new Temperature(Double.parseDouble(targetTemperature), false);
    }

    private Temperature readDayTemperature(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "day_temperature");
        String dayTemperature = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "day_temperature");
        return new Temperature(Double.parseDouble(dayTemperature), false);
    }

    private Temperature readNightTemperature(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "night_temperature");
        String nightTemperature = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "night_temperature");
        return new Temperature(Double.parseDouble(nightTemperature), false);
    }

    private boolean readWeekProgramState(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "week_program_state");
        String state = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "week_program_state");
        return onOffToBoolean(state);
    }

    private ArrayList<DaySchedule> readWeekProgram(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        ArrayList<DaySchedule> weekSchedule = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "week_program");
        for (int i = 0; i < 7; i++) {
            parser.nextTag();
            DaySchedule schedule = readDayProgram(parser);
            weekSchedule.add(schedule);
        }
        parser.require(XmlPullParser.END_TAG, null, "week_program");
        return weekSchedule;
    }

    private DaySchedule readDayProgram(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        DaySchedule schedule = new DaySchedule();
        ArrayList<String> switches = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "day");
        for (int i = 0; i < 10; i++) {
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "switch");
            if (onOffToBoolean(parser.getAttributeValue(null, "state"))) {
                String dayNightSwitch = readText(parser);
                switches.add(dayNightSwitch);
            }
            else {
                while (parser.next() != XmlPullParser.END_TAG);
            }
            parser.require(XmlPullParser.END_TAG, null, "switch");
        }
        parser.require(XmlPullParser.END_TAG, null, "day");
        int l = switches.size();
        for (int i = 0; i < l; i += 2) {

            Period dayPeriod = new Period(startH, startM, endH, endM);
        }
        return schedule;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private DAY nameToDay(String day) {
        switch (day) {
            case "Monday":
                return DAY.MON;
            case "Tuesday":
                return DAY.TUE;
            case "Wednesday":
                return DAY.WED;
            case "Thursday":
                return DAY.THU;
            case "Friday":
                return DAY.FRI;
            case "Saturday":
                return DAY.SAT;
            case "Sunday":
                return DAY.SUN;
            default:
                Log.e(LOG_TAG, "Days of the week went wrong");
                throw new IllegalArgumentException(LOG_TAG + day);
        }
    }

    private boolean onOffToBoolean(String s) {

        return s.equals("on");
    }

    /*public ThermostatType parse(InputStream inputStream) {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<ThermostatType> unmarshalledObject =
                    (JAXBElement<ThermostatType>) unmarshaller.unmarshal(inputStream);
            return unmarshalledObject.getValue();
        } catch (JAXBException e) {
            Log.e(LOG_TAG, "Parser error: something wrong with xml");
        }
        return null;
    }*/
}
