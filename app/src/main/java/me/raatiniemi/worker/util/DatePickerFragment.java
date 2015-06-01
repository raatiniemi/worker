package me.raatiniemi.worker.util;

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

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "DatePickerFragment";

    /**
     * Minimum date available for the date picker.
     */
    private Calendar mMinDate;

    /**
     * Maximum date available for the date picker.
     */
    private Calendar mMaxDate;

    /**
     * The "OnDateSetListener" for the DatePickerDialog.
     */
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;

    private DialogInterface.OnCancelListener mOnCancelListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check if we have a callback listener.
        if (null == getOnDateSetListener()) {
            Log.e(TAG, "No listener have been supplied for the DatePickerFragment");

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
            getActivity(),
            getOnDateSetListener(),
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // If a minimum date is available we have
        // to set it on the date picker.
        if (null != getMinDate()) {
            dialog.getDatePicker().setMinDate(getMinDate().getTimeInMillis());
        }

        // If a maximum date is available we have
        // to set it on the date picker.
        if (null != getMaxDate()) {
            dialog.getDatePicker().setMaxDate(getMaxDate().getTimeInMillis());
        }

        return dialog;
    }

    /**
     * Retrieve the minimum date available for the date picker.
     *
     * @return Minimum date, or null if none is set.
     */
    public Calendar getMinDate() {
        return mMinDate;
    }

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    public void setMinDate(Calendar minDate) {
        mMinDate = minDate;
    }

    /**
     * Retrieve the maximum date available for the date picker.
     *
     * @return Maximum date, or null if none is set.
     */
    public Calendar getMaxDate() {
        return mMaxDate;
    }

    /**
     * Set the maximum date for the date picker.
     *
     * @param maxDate Maximum date.
     */
    public void setMaxDate(Calendar maxDate) {
        mMaxDate = maxDate;
    }

    /**
     * Get the "OnDateSetListener" for the DatePickerDialog.
     *
     * @return "OnDateSetListener" for the DatePickerDialog.
     */
    public DatePickerDialog.OnDateSetListener getOnDateSetListener() {
        return mOnDateSetListener;
    }

    /**
     * Set the "OnDateSetListener" for the DatePickerDialog.
     *
     * @param listener "OnDateSetListener" for the DatePickerDialog.
     */
    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        mOnDateSetListener = listener;
    }

    public DialogInterface.OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }
}
