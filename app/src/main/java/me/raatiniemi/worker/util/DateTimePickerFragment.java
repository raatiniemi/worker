package me.raatiniemi.worker.util;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimePickerFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    /**
     * Minimum date available for the date picker.
     */
    private Calendar mMinDate;

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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
    }
}
