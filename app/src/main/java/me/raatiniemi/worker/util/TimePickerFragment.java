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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check if we have a callback listener.
        if (null == getOnTimeSetListener()) {
            Log.e("onAttach", "No listener have been supplied for the TimePickerFragment");

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
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // TODO: If project is clocked in, set minimum time.
        return new TimePickerDialog(getActivity(), getOnTimeSetListener(), hour, minute, DateFormat.is24HourFormat(getActivity()));
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
}
