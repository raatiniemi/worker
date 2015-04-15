package me.raatiniemi.worker.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class ClockActivityAtFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private static final String TAG = "ClockActivityAtFragment";

    private static final String FRAGMENT_CLOCK_ACTIVITY_DATE_TAG = "clock activity date";

    private static final String FRAGMENT_CLOCK_ACTIVITY_TIME_TAG = "clock activity time";

    /**
     * Public interface for the "OnClockActivityAtListener"
     */
    public interface OnClockActivityAtListener
    {
        /**
         * Triggered after the date and time have been selected.
         * @param position Row position from the adapter.
         * @param calendar Calendar with date and time to clock in or out.
         */
        public void onClockActivityAt(int position, Calendar calendar);
    }

    /**
     * Name of the key for the row position argument.
     */
    private static final String ARGUMENT_POSITION = "_position";

    private OnClockActivityAtListener mOnClockActivityAtListener;

    /**
     * Row position from the adapter.
     */
    private int mPosition;

    /**
     * Calendar object with the selected date and time.
     */
    private Calendar mCalendar;

    /**
     * Create a new instance with the project and adapter row position.
     * @param position Row position from the adapter.
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(int position)
    {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_POSITION, position);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        mCalendar = Calendar.getInstance();

        // TODO: Handle if mPosition is -1.
        Bundle arguments = getArguments();
        mPosition = arguments.getInt(ARGUMENT_POSITION, -1);

        // Initialize the "DatePicker"-fragment.
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSetListener(this);
        datePickerFragment.show(getFragmentManager().beginTransaction(), FRAGMENT_CLOCK_ACTIVITY_DATE_TAG);
    }

    /**
     * Callback method from the date picker fragment.
     * @param view Date picker view.
     * @param year Selected year.
     * @param month Selected month.
     * @param day Selected day.
     */
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Change the year, month, and day to the calendar.
        mCalendar.set(year, month, day);

        // Initialize the "TimePicker"-fragment.
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener(this);
        timePickerFragment.show(getFragmentManager().beginTransaction(), FRAGMENT_CLOCK_ACTIVITY_TIME_TAG);
    }

    /**
     * Callback method from the time picker fragment.
     * @param view Time picker view.
     * @param hour Selected hour.
     * @param minute Selected minute.
     */
    public void onTimeSet(TimePicker view, int hour, int minute)
    {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        if (null != getOnClockActivityAtListener()) {
            getOnClockActivityAtListener().onClockActivityAt(mPosition, mCalendar);
        } else {
            Log.e(TAG, "No OnClockActivityAtListener have been supplied");
        }
    }

    public void setOnClockActivityAtListener(OnClockActivityAtListener onClockActivityAtListener)
    {
        mOnClockActivityAtListener = onClockActivityAtListener;
    }

    public OnClockActivityAtListener getOnClockActivityAtListener()
    {
        return mOnClockActivityAtListener;
    }
}
