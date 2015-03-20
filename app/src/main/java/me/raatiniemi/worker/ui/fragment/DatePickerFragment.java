package me.raatiniemi.worker.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.R;

public class DatePickerFragment extends DialogFragment
{
    private static final String TAG = "DatePickerFragment";

    /**
     * The "OnDateSetListener" for the DatePickerDialog.
     */
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;

    /**
     * Set the "OnDateSetListener" for the DatePickerDialog.
     * @param listener "OnDateSetListener" for the DatePickerDialog.
     */
    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener)
    {
        mOnDateSetListener = listener;
    }

    /**
     * Get the "OnDateSetListener" for the DatePickerDialog.
     * @return "OnDateSetListener" for the DatePickerDialog.
     */
    public DatePickerDialog.OnDateSetListener getOnDateSetListener()
    {
        return mOnDateSetListener;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // Check if we have a callback listener.
        if (getOnDateSetListener() == null) {
            Log.e("onAttach", "No listener have been supplied for the DatePickerFragment");

            // We're unable to use the DatePickerFragment
            // since we do not have listener.
            new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.date_picker_fragment_no_listener_title))
                .setMessage(getString(R.string.date_picker_fragment_no_listener_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing...
                    }
                })
                .show();

            // Dismiss the dialog since we are unable
            // to properly handle events with it.
            dismiss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // TODO: If project is clocked in, set minimum date.
        return new DatePickerDialog(getActivity(), getOnDateSetListener(), year, month, day);
    }
}
