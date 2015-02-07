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

import me.raatiniemi.worker.domain.Project;

public class ClockActivityAtFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    /**
     * Name of the key for the project argument.
     */
    private static final String ARGUMENT_PROJECT = "_project";

    /**
     * Name of the key for the row index argument.
     */
    private static final String ARGUMENT_INDEX = "_index";

    private OnClockActivityAtListener mCallback;

    /**
     * Project to clock in or out.
     */
    private Project mProject;

    /**
     * Row index from the adapter.
     */
    private int mIndex;

    private Calendar mCalendar;

    public interface OnClockActivityAtListener
    {
        public void onClockActivityAt(Project project, Calendar calendar, int index);
    }

    /**
     * Create a new instance with the project and adapter row index.
     * @param project Project to clock in or out.
     * @param index Row index from the adapter.
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(Project project, int index)
    {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();

        // Set the project and adapter row index as arguments to the fragment.
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_PROJECT, project);
        arguments.putInt(ARGUMENT_INDEX, index);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try {
            mCallback = (OnClockActivityAtListener) activity;
            mCalendar = Calendar.getInstance();

            // Retrieve the project and row index from the arguments.
            Bundle arguments = getArguments();
            if (!arguments.containsKey(ARGUMENT_PROJECT)) {
                // TODO: Handle ClockActivityAtFragment without project.
            }
            mProject = (Project) arguments.getSerializable(ARGUMENT_PROJECT);
            mIndex = arguments.getInt(ARGUMENT_INDEX, -1);

            // Initialize the "DatePicker"-fragment.
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setOnDateSetListener(this);
            datePickerFragment.show(getFragmentManager().beginTransaction(), "fragment_clock_activity_date_picker");
        } catch (ClassCastException e) {
            // TODO: Correct message depending on which type cast failed.
            Log.e("onAttach", activity.toString() +" do not implement OnClockActivityAtListener");

            // TODO: Error message to the user, and dismiss the fragment.
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Change the year, month, and day to the calendar.
        mCalendar.set(year, month, day);

        // Initialize the "TimePicker"-fragment.
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener(this);
        timePickerFragment.show(getFragmentManager().beginTransaction(), "fragment_clock_activity_time_picker");
    }

    public void onTimeSet(TimePicker view, int hour, int minute)
    {
        mCalendar.set(Calendar.HOUR, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        // Send the project, calendar, and row index back to the activity.
        mCallback.onClockActivityAt(mProject, mCalendar, mIndex);
    }
}
