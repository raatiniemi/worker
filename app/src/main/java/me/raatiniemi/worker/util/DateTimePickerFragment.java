package me.raatiniemi.worker.util;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimePickerFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
    }
}
