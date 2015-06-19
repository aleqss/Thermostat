package nl.tue.hti.g33.thermostat.parser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.DaySchedule;
import nl.tue.hti.g33.thermostat.utils.Period;
import nl.tue.hti.g33.thermostat.utils.Temperature;

/**
 * @author Alex, 19.06.2015.
 */
public class XmlParser {

    private static final String LOG_TAG = "parser.XmlParser";

    private XmlPullParser parser;
    private XmlSerializer serializer;

    public XmlParser() {

        parser = Xml.newPullParser();
        serializer = Xml.newSerializer();
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

    public String serialize(ParsedThermostat thermostat) {

        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startTag(null, "week_program");
            serializer.attribute(null, "state", (thermostat.mWeekScheduleOn ? "on" : "off"));
            int dayId = 0;
            for (DaySchedule d : thermostat.mWeekSchedule) {
                serializer.startTag(null, "day");
                serializer.attribute(null, "name", DAY.getById(dayId).getFullName());
                int switchCnt = 10;
                for (Period p : d.getSchedule()) {
                    String day = Integer.toString(p.getStartingTimeH()) + ":"
                            + Integer.toString(p.getStartingTimeM());
                    serializer.startTag(null, "switch");
                    serializer.attribute(null, "type", "day");
                    serializer.attribute(null, "state", "on");
                    serializer.text(day);
                    serializer.endTag(null, "switch");
                    switchCnt--;
                    String night = Integer.toString(p.getEndTimeH()) + ":"
                            + Integer.toString(p.getEndTimeM());
                    if (!night.equals("24:00")) {
                        serializer.startTag(null, "switch");
                        serializer.attribute(null, "type", "night");
                        serializer.attribute(null, "state", "on");
                        serializer.text(night);
                        serializer.endTag(null, "switch");
                        switchCnt--;
                    }
                }
                while (switchCnt > 0) {
                    serializer.startTag(null, "switch");
                    serializer.attribute(null, "type", (switchCnt % 2 == 0 ? "day" : "night"));
                    serializer.attribute(null, "state", "off");
                    serializer.text("23:59");
                    serializer.endTag(null, "switch");
                }
                serializer.endTag(null, "day");
                dayId++;
            }
            serializer.endTag(null, "week_program");
            return writer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Week Program serialization failed.");
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
            switch (name) {
                case "current_day":
                    thermostat.mDayOfTheWeek = readDay(parser);
                    break;
                case "time":
                    thermostat.mTime = readTime(parser);
                    break;
                case "current_temperature":
                    thermostat.mCurrentTemperature = readCurrentTemperature(parser);
                    break;
                case "target_temperature":
                    thermostat.mTargetTemperature = readTargetTemperature(parser);
                    break;
                case "day_temperature":
                    thermostat.mDayTemperature = readDayTemperature(parser);
                    break;
                case "night_temperature":
                    thermostat.mNightTemperature = readNightTemperature(parser);
                    break;
                case "week_program_state":
                    thermostat.mWeekScheduleOn = readWeekProgramState(parser);
                    break;
                case "week_program":
                    thermostat.mWeekSchedule = readWeekProgram(parser);
                    break;
                default:
                    throw new XmlPullParserException("Ill-formed XML given to parse");
            }
        }
        return thermostat;
    }

    private DAY readDay(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "current_day");
        String day = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "current_day");
        return DAY.getByName(day);
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
                //noinspection StatementWithEmptyBody
                while (parser.next() != XmlPullParser.END_TAG);
            }
            parser.require(XmlPullParser.END_TAG, null, "switch");
        }
        parser.require(XmlPullParser.END_TAG, null, "day");
        int l = switches.size();
        for (int i = 0; i < l; i += 2) {
            String s = switches.get(i);
            int startH = Integer.getInteger(s.substring(0, 2));
            int startM = Integer.getInteger(s.substring(3, 5));
            int endH, endM;
            s = switches.get(i+1);
            if (s != null) {
                endH = Integer.getInteger(s.substring(0, 2));
                endM = Integer.getInteger(s.substring(3, 5));
            }
            else {
                endH = 24;
                endM = 0;
            }
            Period dayPeriod = new Period(startH, startM, endH, endM);
            schedule.addDayPeriod(dayPeriod);
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

    private boolean onOffToBoolean(String s) {

        return s.equals("on");
    }
}
