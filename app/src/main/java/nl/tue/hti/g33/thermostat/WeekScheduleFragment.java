package nl.tue.hti.g33.thermostat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.tue.hti.g33.thermostat.utils.Thermostat;
import nl.tue.hti.g33.thermostat.utils.ThermostatProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeekScheduleFragment extends Fragment implements ThermostatProvider, View.OnClickListener{

    private Thermostat mThermostat;

    private static final String LOG_TAG = "WeekScheduleFragment";

    public WeekScheduleFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_week_schedule, container, false);
        root.findViewById(R.id.frag_mon).setOnClickListener(this);
        root.findViewById(R.id.frag_tue).setOnClickListener(this);
        root.findViewById(R.id.frag_wed).setOnClickListener(this);
        root.findViewById(R.id.frag_thu).setOnClickListener(this);
        root.findViewById(R.id.frag_fri).setOnClickListener(this);
        root.findViewById(R.id.frag_sat).setOnClickListener(this);
        root.findViewById(R.id.frag_sun).setOnClickListener(this);
        return root;
    }

    @Override
    public Thermostat provideThermostat() {

        return mThermostat;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.v(LOG_TAG, "There was a click.");
        String day;
        switch (v.getId()) {
            case R.id.frag_mon:
                day = "mon";
                break;
            case R.id.frag_tue:
                day = "tue";
                break;
            case R.id.frag_wed:
                day = "wed";
                break;
            case R.id.frag_thu:
                day = "thu";
                break;
            case R.id.frag_fri:
                day = "fri";
                break;
            case R.id.frag_sat:
                day = "sat";
                break;
            case R.id.frag_sun:
                day = "sun";
                break;
            default:
                Log.e(LOG_TAG, "OnClick() failed");
                throw new IllegalArgumentException(LOG_TAG + "OnCLick went wrong");
        }
        Intent intent = new Intent(getActivity(), DetailDayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("day", day);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }
}
