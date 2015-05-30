package me.raatiniemi.worker.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimePickerFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "DateTimePickerFragment";

    private static final String FRAGMENT_DATE_PICKER_TAG = "date picker";

    private static final String FRAGMENT_TIME_PICKER_TAG = "time picker";

    /**
     * Date and time set by the "DateTimePickerFragment".
     */
    private Calendar mCalendar = Calendar.getInstance();

    /**
     * Listener for the selected date and time.
     */
    private OnDateTimeSetListener mOnDateTimeSetListener;

    /**
     * Minimum date available for the date picker.
     */
    private Calendar mMinDate;

    /**
     * Maximum date available for the date picker.
     */
    private Calendar mMaxDate;

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

    /**
     * Retrieve the maximum date available for the date picker.
     *
     * @return Maximum date, or null if none is set.
     */
    public Calendar getMaxDate() {
        return mMaxDate;
    }

    /**
     * Set the maximum date for the date picker.
     *
     * @param maxDate Maximum date.
     */
    public void setMaxDate(Calendar maxDate) {
        mMaxDate = maxDate;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setOnDateSetListener(this);
        datePicker.show(
            getFragmentManager().beginTransaction(),
            FRAGMENT_DATE_PICKER_TAG
        );
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Relay the selected year, month, and day to the stored calendar.
        mCalendar.set(year, month, day);

        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.setOnTimeSetListener(this);
        timePicker.show(
            getFragmentManager().beginTransaction(),
            FRAGMENT_TIME_PICKER_TAG
        );
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        // Relay the selected hour and minute to the stored calendar.
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        // Check that we have been supplied with a listener.
        if (null == getOnDateTimeSetListener()) {
            Log.e(TAG, "No OnDateTimeSetListener have been supplied");
            return;
        }

        // Send the calendar to the listener.
        getOnDateTimeSetListener().onDateTimeSet(mCalendar);
    }

    /**
     * Get the "OnDateTimeSetListener", or null if none have been supplied.
     *
     * @return Listener for "OnDateTimeSetListener".
     */
    public OnDateTimeSetListener getOnDateTimeSetListener() {
        return mOnDateTimeSetListener;
    }

    /**
     * Set the "OnDateTimeSetListener".
     *
     * @param onDateTimeSetListener Listener for "OnDateTimeSetListener".
     */
    public void setOnDateTimeSetListener(OnDateTimeSetListener onDateTimeSetListener) {
        mOnDateTimeSetListener = onDateTimeSetListener;
    }

    /**
     * Interface for listening to the selected date and time.
     */
    public interface OnDateTimeSetListener {
        /**
         * Listen for the selected date and time.
         *
         * @param calendar Selected date and time.
         */
        void onDateTimeSet(Calendar calendar);
    }
}
