package nl.tue.hti.g33.thermostat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import nl.tue.hti.g33.thermostat.utils.Temperature;
import nl.tue.hti.g33.thermostat.utils.Thermostat;
import nl.tue.hti.g33.thermostat.utils.ThermostatListener;
import nl.tue.hti.g33.thermostat.utils.ThermostatProvider;
import nl.tue.hti.g33.thermostat.utils.Time;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
        implements ThermostatListener, NumberPicker.OnValueChangeListener,
        Switch.OnCheckedChangeListener {

    private static final String LOG_TAG = "HomeFragment";

    private Thermostat mThermostat;

    public HomeFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            mThermostat = ((ThermostatProvider) getActivity()).provideThermostat();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Context must implement ThermostatProvider interface!");
            throw new IllegalArgumentException(LOG_TAG + "Initialisation failed due to" +
                    "context not implementing ThermostatProvider.");
        }
        mThermostat.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_home, container, false);
        NumberPicker temp = (NumberPicker) root.findViewById(R.id.temperature);
        NumberPicker tempD = (NumberPicker) root.findViewById(R.id.temperatureD);
        Switch vacationMode = (Switch) root.findViewById(R.id.vacation_mode_on);
        temp.setOnValueChangedListener(this);
        tempD.setOnValueChangedListener(this);
        vacationMode.setOnCheckedChangeListener(this);
        temp.setMaxValue(29);
        temp.setMinValue(5);
        tempD.setMaxValue(9);
        tempD.setMinValue(0);
        return root;
    }

    @Override
    public void onThermostatUpdate(Thermostat thermostat) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView dateTime = (TextView) getActivity().findViewById(R.id.date_time);
                dateTime.setText("Today is " + mThermostat.getDayOfTheWeek().getFullName()
                        + ", " + new Time(mThermostat.getCurrentTime()).toString());

                TextView curTemp = (TextView) getActivity().findViewById(R.id.current_temperature);
                curTemp.setText("Current temperature is " + mThermostat.getCurrentTemperature());

                TextView targTemp = (TextView) getActivity().findViewById(R.id.target_temperature);
                targTemp.setText("Target temperature is " + mThermostat.getTargetTemperature());

                TextView dayTemp = (TextView) getActivity().findViewById(R.id.day_temperature);
                dayTemp.setText("Day temperature is " + mThermostat.getDayTemperature());

                TextView nightTemp = (TextView) getActivity().findViewById(R.id.night_temperature);
                nightTemp.setText("Night temperature is " + mThermostat.getNightTemperature());

                Switch vacationMode = (Switch) getActivity().findViewById(R.id.vacation_mode_on);
                vacationMode.setChecked(!mThermostat.getWeekScheduleOn());
            }
        });
    }

    /**
     * Called upon a change of the current value.
     *
     * @param picker The NumberPicker associated with this listener.
     * @param oldVal The previous value.
     * @param newVal The new value.
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        int temp, tempD;
        switch (picker.getId()) {
            case R.id.temperatureD:
                if (oldVal == 9 && newVal == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NumberPicker temp =
                                    (NumberPicker) getActivity().findViewById(R.id.temperature);
                            if (temp.getValue() < 29) {
                                temp.setValue(temp.getValue() + 1);
                            }
                        }
                    });
                }
                if (oldVal == 0 && newVal == 9) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NumberPicker temp =
                                    (NumberPicker) getActivity().findViewById(R.id.temperature);
                            if (temp.getValue() > 5) {
                                temp.setValue(temp.getValue() - 1);
                            }
                        }
                    });
                }
                temp = ((NumberPicker) getView().findViewById(R.id.temperature)).getValue();
                tempD = picker.getValue();

                if (mThermostat.getWeekScheduleOn()) {
                    mThermostat.setTemporaryOverride(new Temperature(temp + tempD * 0.1, false));
                }
                else {
                    mThermostat.setVacationMode(true, new Temperature(temp + tempD * 0.1, false));
                }
                break;
            case R.id.temperature:
                tempD = ((NumberPicker) getView().findViewById(R.id.temperatureD)).getValue();
                temp = picker.getValue();

                if (mThermostat.getWeekScheduleOn()) {
                    mThermostat.setTemporaryOverride(new Temperature(temp + tempD * 0.1, false));
                }
                else {
                    mThermostat.setVacationMode(true, new Temperature(temp + tempD * 0.1, false));
                }
                break;
            default:
                Log.e(LOG_TAG, "Wrong number picker");
        }
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int temp = ((NumberPicker) getView().findViewById(R.id.temperature)).getValue();
        int tempD = ((NumberPicker) getView().findViewById(R.id.temperatureD)).getValue();
        if (isChecked) {
            mThermostat.setVacationMode(true, new Temperature(temp + tempD * 0.1, false));
        }
        else {
            mThermostat.setVacationMode(false, null);
        }
    }
}
