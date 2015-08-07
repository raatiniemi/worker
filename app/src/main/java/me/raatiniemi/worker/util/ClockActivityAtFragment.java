package me.raatiniemi.worker.util;

import android.app.Activity;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.model.domain.project.Project;

public class ClockActivityAtFragment extends DateTimePickerFragment
    implements DateTimePickerFragment.OnDateTimeSetListener {
    private static final String TAG = "ClockActivityAtFragment";

    /**
     * Listener for "OnClockActivityAtListener".
     */
    private OnClockActivityAtListener mOnClockActivityAtListener;

    /**
     * Create a new instance for project clock in/out with date and time.
     *
     * @param project Project used with the clock activity.
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(Project project) {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();

        // If the project is active we have to set the minimum date for clocking out.
        if (null != project && project.isActive()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getClockedInSince());
            fragment.setMinDate(calendar);
        }

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

        // Send the project row position with the selected
        // date and time to the listener.
        getOnClockActivityAtListener().onClockActivityAt(calendar);
    }

    /**
     * Get the "OnClockActivityAtListener", or null if none has been supplied.
     *
     * @return Listener for "OnClockActivityAtListener".
     */
    public OnClockActivityAtListener getOnClockActivityAtListener() {
        return mOnClockActivityAtListener;
    }

    /**
     * Set the "OnClockActivityAtListener".
     *
     * @param onClockActivityAtListener Listener for "OnClockActivityAtListener".
     */
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
         * @param calendar Calendar with date and time to clock in or out.
         */
        void onClockActivityAt(Calendar calendar);
    }
}
