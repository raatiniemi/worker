package me.raatiniemi.worker.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class ClockOutAtFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private OnClockOutAtListener mCallback;

    private Calendar mCalendar;

    public interface OnClockOutAtListener
    {
        public void onClockOutAt(Calendar calendar);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try {
            mCallback = (OnClockOutAtListener) activity;
        } catch (ClassCastException e) {
            Log.e("onAttach", activity.toString() +" do not implement OnClockOutAtListener");

            // TODO: Error message to the user, and dismiss the fragment.
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Only show the date picker dialog if we have a valid callback.
        if (mCallback != null) {
            // Retrieve an instance for the calendar.
            mCalendar = Calendar.getInstance();

            // Initialize the "DatePicker"-fragment.
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setOnDateSetListener(this);
            datePickerFragment.show(getFragmentManager().beginTransaction(), "fragment_clock_out_date_picker");
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Change the year, month, and day to the calendar.
        mCalendar.set(year, month, day);

        // Initialize the "TimePicker"-fragment.
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener(this);
        timePickerFragment.show(getFragmentManager().beginTransaction(), "fragment_clock_out_date_picker");
    }

    public void onTimeSet(TimePicker view, int hour, int minute)
    {
        mCalendar.set(Calendar.HOUR, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        // Send the calendar back to the activity.
        mCallback.onClockOutAt(mCalendar);
    }
}
