package me.raatiniemi.worker.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class ClockOutAtFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize the "DatePicker"-fragment.
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSetListener(this);
        datePickerFragment.show(getFragmentManager().beginTransaction(), "fragment_clock_out_date_picker");
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // TODO: Handle the input data.

        // Initialize the "TimePicker"-fragment.
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener(this);
        timePickerFragment.show(getFragmentManager().beginTransaction(), "fragment_clock_out_date_picker");
    }

    public void onTimeSet(TimePicker view, int hour, int minute)
    {
        // TODO: Handle the input data.
    }
}
