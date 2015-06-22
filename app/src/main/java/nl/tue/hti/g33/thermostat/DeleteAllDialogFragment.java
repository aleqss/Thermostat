package nl.tue.hti.g33.thermostat;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.Period;
import nl.tue.hti.g33.thermostat.utils.Thermostat;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteAllDialogFragment extends DialogFragment {

    private Thermostat mThermostat;
    private DAY mDay;

    private static final String ARG_DAY = "day";

    interface DeleteDialogListener {

        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);

    }

    private DeleteDialogListener mListener;

    public static DeleteAllDialogFragment newInstance(DAY day) {
        DeleteAllDialogFragment fragment = new DeleteAllDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    public DeleteAllDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDay = (DAY) getArguments().getSerializable(ARG_DAY);
        }

        mThermostat = Thermostat.getInstance();
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        try {
            mListener = (DeleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete all switches")
                .setMessage("This action will irreversibly delete all switches between day " +
                        "and night for current day")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Period> toDel = new ArrayList<>();
                        for (Period p : mThermostat.getDaySchedule(mDay)) {
                            toDel.add(p);
                        }
                        for (Period p : toDel) {
                            mThermostat.deleteSwitch(mDay, p);
                        }
                        mListener.onDialogPositiveClick(DeleteAllDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(DeleteAllDialogFragment.this);
                        DeleteAllDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
