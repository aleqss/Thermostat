package nl.tue.hti.g33.thermostat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.Period;
import nl.tue.hti.g33.thermostat.utils.Thermostat;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRuleDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRuleDialogFragment extends DialogFragment {

    private static final String ARG_PERIOD = "period";
    private static final String ARG_DAY = "day";
    //private static final String LOG_TAG = "AddRuleDialogFragment";

    private Period mPeriod;
    private DAY mDay;
    private String mTitle;
    private Thermostat mThermostat;

    interface AddRuleDialogListener {

        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);

    }

    private AddRuleDialogListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param p Parameter 1.
     * @return A new instance of fragment AddRuleDialogFragment.
     */
    public static AddRuleDialogFragment newInstance(Period p, DAY day) {
        AddRuleDialogFragment fragment = new AddRuleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PERIOD, p);
        args.putSerializable(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    public AddRuleDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPeriod = (Period) getArguments().getSerializable(ARG_PERIOD);
            mDay = (DAY) getArguments().getSerializable(ARG_DAY);
        }
        if (mPeriod == null) {
            mTitle = getString(R.string.add_rule);
            mPeriod = new Period(13, 0, 15, 0);
        }
        else {
            mTitle = getString(R.string.edit_rule);
        }
        mThermostat = Thermostat.getInstance();
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        try {
            mListener = (AddRuleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddRuleDialogListener");
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_add_rule_dialog, null);
        builder.setView(view).setTitle(mTitle);
        TimePicker tStart = (TimePicker) view.findViewById(R.id.addStartTime);
        TimePicker tEnd = (TimePicker) view.findViewById(R.id.addEndTime);
        tStart.setIs24HourView(true);
        tEnd.setIs24HourView(true);
        tStart.setCurrentHour(mPeriod.getStartingTimeH());
        tStart.setCurrentMinute(mPeriod.getStartingTimeM());
        tEnd.setCurrentHour(mPeriod.getEndTimeH());
        tEnd.setCurrentMinute(mPeriod.getEndTimeM());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TimePicker start = (TimePicker) view.findViewById(R.id.addStartTime);
                TimePicker end = (TimePicker) view.findViewById(R.id.addEndTime);
                CheckBox wholeWeek = (CheckBox) view.findViewById(R.id.whole_week_switch);

                Period nPeriod = new Period(start.getCurrentHour(), start.getCurrentMinute(),
                        end.getCurrentHour(), end.getCurrentMinute());
                if (wholeWeek.isChecked()) {
                    for (int i =0; i < 7; i++) {
                        mThermostat.deleteSwitch(DAY.getById(i), mPeriod);
                        mThermostat.addSwitch(DAY.getById(i), nPeriod);
                        mListener.onDialogPositiveClick(AddRuleDialogFragment.this);
                    }
                }
                else {
                    mThermostat.deleteSwitch(mDay, mPeriod);
                    mThermostat.addSwitch(mDay, nPeriod);
                    mListener.onDialogPositiveClick(AddRuleDialogFragment.this);
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing here - just dismiss
                        mListener.onDialogNegativeClick(AddRuleDialogFragment.this);
                        AddRuleDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
