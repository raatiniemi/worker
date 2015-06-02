package me.raatiniemi.worker.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.R;

public class TimePickerFragment extends DialogFragment {
    private static final String TAG = "TimePickerFragment";

    /**
     * The "OnTimeSetListener" for the TimePickerDialog.
     */
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;

    private DialogInterface.OnCancelListener mOnCancelListener;

    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check if we have a callback listener.
        if (null == getOnTimeSetListener()) {
            Log.e(TAG, "No listener have been supplied for the TimePickerFragment");

            // We're unable to use the TimePickerFragment
            // since we do not have listener.
            new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.time_picker_fragment_no_listener_title))
                .setMessage(getString(R.string.time_picker_fragment_no_listener_description))
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
        TimePickerDialog dialog = new TimePickerDialog(
            getActivity(),
            getOnTimeSetListener(),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(getActivity())
        );

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        // Delegate the "onCancel" event to the listener, if available.
        if (null != getOnCancelListener()) {
            getOnCancelListener().onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // Delegate the "onDismiss" event to the listener, if available.
        if (null != getOnDismissListener()) {
            getOnDismissListener().onDismiss(dialog);
        }
    }

    /**
     * Get the "OnTimeSetListener" for the TimePickerDialog.
     *
     * @return "OnTimeSetListener" for the TimePickerDialog.
     */
    public TimePickerDialog.OnTimeSetListener getOnTimeSetListener() {
        return mOnTimeSetListener;
    }

    /**
     * Set the "OnTimeSetListener" for the TimePickerDialog.
     *
     * @param listener "OnTimeSetListener" for the TimePickerDialog.
     */
    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        mOnTimeSetListener = listener;
    }

    public DialogInterface.OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
}
