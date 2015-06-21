package nl.tue.hti.g33.thermostat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.Thermostat;
import nl.tue.hti.g33.thermostat.utils.ThermostatProvider;
import nl.tue.hti.g33.thermostat.widgets.DayTimelineView;


public class DayScheduleFragment extends Fragment implements ThermostatProvider {

    private static final String ARG_DAY = "dayOfTheWeek";
    private static final String LOG_TAG = "DayScheduleFragment";

    private DAY mDay;
    private Thermostat mThermostat;
    private DayPeriodAdapter mViewAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dayOfTheWeek Day of the week for which the schedule is displayed.
     * @return A new instance of fragment DayScheduleFragment.
     */
    public static DayScheduleFragment newInstance(DAY dayOfTheWeek) {

        DayScheduleFragment fragment = new DayScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DAY, dayOfTheWeek);
        fragment.setArguments(args);
        return fragment;
    }

    public DayScheduleFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDay = (DAY) getArguments().getSerializable(ARG_DAY);
        }

        try {
            mThermostat = ((ThermostatProvider) getActivity()).provideThermostat();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Context must implement ThermostatProvider interface!");
            throw new IllegalArgumentException(LOG_TAG + "Initialisation failed due to" +
                    "context not implementing ThermostatProvider.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "Creating view");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_day_schedule, container, false);
        DayTimelineView timeline = (DayTimelineView) root.findViewById(R.id.timeline_day);
        timeline.setDayOfTheWeek(mDay);
        mViewAdapter = new DayPeriodAdapter(getActivity(),
                mThermostat.getDaySchedule(mDay));
        ListView switchesList = (ListView) root.findViewById(R.id.switches_list);
        switchesList.setAdapter(mViewAdapter);
        return root;
    }


    @Override
    public Thermostat provideThermostat() {

        return mThermostat;
    }
}
