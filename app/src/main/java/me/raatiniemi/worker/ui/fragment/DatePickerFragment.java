package me.raatiniemi.worker.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
{
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
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), getOnDateSetListener(), year, month, day);
    }
}
