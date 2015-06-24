package nl.tue.hti.g33.thermostat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import nl.tue.hti.g33.thermostat.utils.Thermostat;
import nl.tue.hti.g33.thermostat.utils.ThermostatListener;
import nl.tue.hti.g33.thermostat.utils.ThermostatProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class TemperaturesFragment extends Fragment
        implements ThermostatListener, NumberPicker.OnValueChangeListener{

    private static final String LOG_TAG = "TemperaturesFragment";

    private Thermostat mThermostat;

    public TemperaturesFragment() {

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
        View root =  inflater.inflate(R.layout.fragment_temperatures, container, false);
        NumberPicker dayTemp = (NumberPicker) root.findViewById(R.id.setDayTemperature);
        NumberPicker dayTempD = (NumberPicker) root.findViewById(R.id.setDayTemperatureD);
        NumberPicker nightTemp = (NumberPicker) root.findViewById(R.id.setNightTemperature);
        NumberPicker nightTempD = (NumberPicker) root.findViewById(R.id.setNightTemperatureD);
        dayTemp.setMaxValue(29);
        nightTemp.setMaxValue(29);
        dayTemp.setMinValue(5);
        nightTemp.setMinValue(5);
        dayTempD.setMaxValue(9);
        nightTempD.setMaxValue(9);
        dayTempD.setMinValue(0);
        nightTempD.setMinValue(0);
        dayTemp.setOnValueChangedListener(this);
        dayTempD.setOnValueChangedListener(this);
        nightTemp.setOnValueChangedListener(this);
        nightTempD.setOnValueChangedListener(this);
        return root;
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
            case R.id.setDayTemperatureD:
                if (oldVal == 9 && newVal == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NumberPicker temp =
                                    (NumberPicker) getActivity().findViewById(R.id.setDayTemperature);
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
                                    (NumberPicker) getActivity().findViewById(R.id.setDayTemperature);
                            if (temp.getValue() > 5) {
                                temp.setValue(temp.getValue() - 1);
                            }
                        }
                    });
                }
                temp = ((NumberPicker) getView().findViewById(R.id.setDayTemperature)).getValue();
                tempD = picker.getValue();

                mThermostat.updateDayTemperature(temp + tempD * 0.1);
                break;
            case R.id.setNightTemperature:
                tempD = ((NumberPicker) getView().findViewById(R.id.setNightTemperatureD)).getValue();
                temp = picker.getValue();

                mThermostat.updateNightTemperature(temp + tempD * 0.1);
                break;
            case R.id.setNightTemperatureD:
                if (oldVal == 9 && newVal == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NumberPicker temp =
                                    (NumberPicker) getActivity().findViewById(R.id.setNightTemperature);
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
                                    (NumberPicker) getActivity().findViewById(R.id.setNightTemperature);
                            if (temp.getValue() > 5) {
                                temp.setValue(temp.getValue() - 1);
                            }
                        }
                    });
                }
                temp = ((NumberPicker) getView().findViewById(R.id.setNightTemperature)).getValue();
                tempD = picker.getValue();

                mThermostat.updateNightTemperature(temp + tempD * 0.1);
                break;
            case R.id.setDayTemperature:
                tempD = ((NumberPicker) getView().findViewById(R.id.setDayTemperatureD)).getValue();
                temp = picker.getValue();

                mThermostat.updateDayTemperature(temp + tempD * 0.1);
                break;
            default:
                Log.e(LOG_TAG, "Wrong number picker");
        }
    }

    @Override
    public void onThermostatUpdate(Thermostat thermostat) {

        if (!getUserVisibleHint()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NumberPicker dayTemp =
                            (NumberPicker) getView().findViewById(R.id.setDayTemperature);
                    NumberPicker dayTempD =
                            (NumberPicker) getView().findViewById(R.id.setDayTemperatureD);
                    NumberPicker nightTemp =
                            (NumberPicker) getView().findViewById(R.id.setNightTemperature);
                    NumberPicker nightTempD =
                            (NumberPicker) getView().findViewById(R.id.setNightTemperatureD);
                    dayTemp.setValue((int) mThermostat.getDayTemperature());
                    dayTempD.setValue((int) ((mThermostat.getDayTemperature()
                            - (int) mThermostat.getDayTemperature()) * 10));
                    nightTemp.setValue((int) mThermostat.getNightTemperature());
                    nightTempD.setValue((int) ((mThermostat.getNightTemperature()
                            - (int) mThermostat.getNightTemperature()) * 10));
                }
            });
        }
    }
}
