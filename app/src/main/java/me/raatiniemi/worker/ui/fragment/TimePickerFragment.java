package me.raatiniemi.worker.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
{
    /**
     * The "OnTimeSetListener" for the TimePickerDialog.
     */
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;

    /**
     * Set the "OnTimeSetListener" for the TimePickerDialog.
     * @param listener "OnTimeSetListener" for the TimePickerDialog.
     */
    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener)
    {
        mOnTimeSetListener = listener;
    }

    /**
     * Get the "OnTimeSetListener" for the TimePickerDialog.
     * @return "OnTimeSetListener" for the TimePickerDialog.
     */
    public TimePickerDialog.OnTimeSetListener getOnTimeSetListener()
    {
        return mOnTimeSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // TODO: Handle if "getOnTimeSetListener" returns null.
        return new TimePickerDialog(getActivity(), getOnTimeSetListener(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}
