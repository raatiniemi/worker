/*
 * Copyright (C) 2015 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import me.raatiniemi.worker.base.view.BaseFragment;

public class DateTimePickerFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "DateTimePickerFragment";

    private static final String FRAGMENT_DATE_PICKER_TAG = "date picker";

    private static final String FRAGMENT_TIME_PICKER_TAG = "time picker";

    /**
     * Date and time set by the "DateTimePickerFragment".
     */
    private final Calendar mCalendar = Calendar.getInstance();

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
     * Instance for the date picker.
     */
    private DatePickerFragment mDatePicker;

    /**
     * Instance for the time picker.
     */
    private TimePickerFragment mTimePicker;

    /**
     * Dismiss the DateTimePickerFragment.
     * <p>
     * Triggers the onDetach-method for additional clean up.
     */
    private void dismiss() {
        getFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

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

    /**
     * Setup the fragment, this method is primarily used as a single setup
     * between API versions.
     */
    private void setup() {
        mDatePicker = new DatePickerFragment();
        mDatePicker.setOnDateSetListener(this);

        // The date picker only needs to listen to the "onCancel"-event
        // to initialize fragment clean up.
        //
        // The "onDismiss"-event will run for both set date and cancel.
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });

        // Set the min/max date for the picker.
        mDatePicker.setMaxDate(getMaxDate());
        mDatePicker.setMinDate(getMinDate());

        mDatePicker.show(
                getFragmentManager().beginTransaction(),
                FRAGMENT_DATE_PICKER_TAG
        );
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setup();
    }

    /**
     * TODO: Remove method call when `minSdkVersion` is +23.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // In API +23 the `setup` is called from the `onAttach(Context)`.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setup();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (null != mDatePicker) {
            getFragmentManager().beginTransaction()
                    .remove(mDatePicker)
                    .commit();
        }
        mDatePicker = null;

        if (null != mTimePicker) {
            getFragmentManager().beginTransaction()
                    .remove(mTimePicker)
                    .commit();
        }
        mTimePicker = null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Relay the selected year, month, and day to the stored calendar.
        mCalendar.set(year, month, day);

        mTimePicker = new TimePickerFragment();
        mTimePicker.setOnTimeSetListener(this);

        // The timer picker only needs to listen to the "onDismiss"-event since
        // it will run for both set time and cancel.
        //
        // And, in either case we'd want to clean up the fragment.
        //
        // Also, I was unable to get the TimePickerDialog to trigger the
        // "onCancel"-event to the DialogFragment.
        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dismiss();
            }
        });

        mTimePicker.show(
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
