/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.presentation.view.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

public class DateTimePickerFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "DateTimePickerFragment";

    private static final String FRAGMENT_DATE_PICKER_TAG = "date picker";

    private static final String FRAGMENT_TIME_PICKER_TAG = "time picker";

    /**
     * Date and time set by the "DateTimePickerFragment".
     */
    private final Calendar calendar = Calendar.getInstance();

    /**
     * Listener for the selected date and time.
     */
    private OnDateTimeSetListener onDateTimeSetListener;

    /**
     * Minimum date available for the date picker.
     */
    private Calendar minDate;

    /**
     * Maximum date available for the date picker.
     */
    private Calendar maxDate;

    /**
     * Instance for the date picker.
     */
    private DatePickerFragment datePicker;

    /**
     * Instance for the time picker.
     */
    private TimePickerFragment timePicker;

    /**
     * Dismiss the DateTimePickerFragment.
     * <p/>
     * Triggers the onDetach-method for additional clean up.
     */
    private void dismiss() {
        getFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    protected void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }

    /**
     * Set the maximum date for the date picker.
     *
     * @param maxDate Maximum date.
     */
    protected void setMaxDate(Calendar maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * Setup the fragment, this method is primarily used as a single setup
     * between API versions.
     */
    private void setup() {
        datePicker = DatePickerFragment.newInstance(this);

        // The date picker only needs to listen to the "onCancel"-event
        // to initialize fragment clean up.
        //
        // The "onDismiss"-event will run for both set date and cancel.
        datePicker.setOnCancelListener(dialogInterface -> dismiss());

        // Set the min/max date for the picker.
        datePicker.setMaxDate(maxDate);
        datePicker.setMinDate(minDate);

        showFragmentWithTag(datePicker, FRAGMENT_DATE_PICKER_TAG);
    }

    private void showFragmentWithTag(Fragment fragment, String tag) {
        getFragmentManager().beginTransaction()
                .add(fragment, tag)
                .commit();
    }

    @TargetApi(Build.VERSION_CODES.M)
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

        if (nonNull(datePicker)) {
            getFragmentManager().beginTransaction()
                    .remove(datePicker)
                    .commit();
        }
        datePicker = null;

        if (nonNull(timePicker)) {
            getFragmentManager().beginTransaction()
                    .remove(timePicker)
                    .commit();
        }
        timePicker = null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Relay the selected year, month, and day to the stored calendar.
        calendar.set(year, month, day);

        timePicker = TimePickerFragment.newInstance(this);

        // The timer picker only needs to listen to the "onDismiss"-event since
        // it will run for both set time and cancel.
        //
        // And, in either case we'd want to clean up the fragment.
        //
        // Also, I was unable to get the TimePickerDialog to trigger the
        // "onCancel"-event to the DialogFragment.
        timePicker.setOnDismissListener(dialogInterface -> dismiss());

        showFragmentWithTag(timePicker, FRAGMENT_TIME_PICKER_TAG);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        // Relay the selected hour and minute to the stored calendar.
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Check that we have been supplied with a listener.
        if (isNull(onDateTimeSetListener)) {
            Log.e(TAG, "No OnDateTimeSetListener have been supplied");
            return;
        }

        // Send the calendar to the listener.
        onDateTimeSetListener.onDateTimeSet(calendar);
    }

    /**
     * Set the "OnDateTimeSetListener".
     *
     * @param onDateTimeSetListener Listener for "OnDateTimeSetListener".
     */
    protected void setOnDateTimeSetListener(OnDateTimeSetListener onDateTimeSetListener) {
        this.onDateTimeSetListener = onDateTimeSetListener;
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
