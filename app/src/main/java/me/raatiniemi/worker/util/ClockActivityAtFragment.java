package me.raatiniemi.worker.util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class ClockActivityAtFragment extends DateTimePickerFragment
    implements DateTimePickerFragment.OnDateTimeSetListener {
    private static final String TAG = "ClockActivityAtFragment";

    /**
     * Name of the key for the row position argument.
     */
    private static final String ARGUMENT_POSITION = "_position";

    private OnClockActivityAtListener mOnClockActivityAtListener;

    /**
     * Retrieve the row position for the project from the adapter.
     *
     * @return Position for the project from the adapter.
     */
    private int getPosition() {
        return getArguments().getInt(ARGUMENT_POSITION, -1);
    }

    /**
     * Create a new instance with the project and adapter row position.
     *
     * @param position Row position from the adapter.
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(int position) {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_POSITION, position);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setOnDateTimeSetListener(this);
    }

    @Override
    public void onDateTimeSet(Calendar calendar) {
        if (null == getOnClockActivityAtListener()) {
            Log.e(TAG, "No OnClockActivityAtListener have been supplied");
            return;
        }

        getOnClockActivityAtListener().onClockActivityAt(getPosition(), calendar);
    }

    public OnClockActivityAtListener getOnClockActivityAtListener() {
        return mOnClockActivityAtListener;
    }

    public void setOnClockActivityAtListener(OnClockActivityAtListener onClockActivityAtListener) {
        mOnClockActivityAtListener = onClockActivityAtListener;
    }

    /**
     * Public interface for the "OnClockActivityAtListener"
     */
    public interface OnClockActivityAtListener {
        /**
         * Triggered after the date and time have been selected.
         *
         * @param position Row position from the adapter.
         * @param calendar Calendar with date and time to clock in or out.
         */
        void onClockActivityAt(int position, Calendar calendar);
    }
}
