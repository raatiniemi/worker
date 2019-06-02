/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.shared.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import me.raatiniemi.worker.R;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

public class DateTimePickerFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String FRAGMENT_DATE_PICKER_TAG = "date picker";

    private static final String FRAGMENT_TIME_PICKER_TAG = "time picker";

    private final Calendar calendar = Calendar.getInstance();

    private OnDateTimeSetListener onDateTimeSetListener;

    @Nullable
    private Calendar minDate;
    @Nullable
    private Calendar maxDate;

    private DatePickerFragment datePicker;
    private TimePickerFragment timePicker;

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    protected void setMinDate(@NonNull Calendar minDate) {
        if (minDate.after(maxDate)) {
            throw new IllegalArgumentException("Minimum date occurs after maximum date");
        }

        this.minDate = minDate;
    }

    /**
     * Set the maximum date for the date picker.
     *
     * @param maxDate Maximum date.
     */
    @SuppressWarnings("WeakerAccess")
    protected void setMaxDate(@NonNull Calendar maxDate) {
        if (maxDate.before(minDate)) {
            throw new IllegalArgumentException("Maximum date occurs before minimum date");
        }

        this.maxDate = maxDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isStateInvalid()) {
            dismissDialogWithInvalidState();
            return;
        }

        datePicker = DatePickerFragment.newInstance(this);

        // The date picker only needs to listen to the "onCancel"-event
        // to initialize fragment clean up.
        //
        // The "onDismiss"-event will run for both set date and cancel.
        datePicker.setOnCancelListener(dialogInterface -> dismiss());

        if (nonNull(maxDate)) {
            datePicker.setMaxDate(maxDate);
        }

        if (nonNull(minDate)) {
            datePicker.setMinDate(minDate);
        }

        showFragmentWithTag(datePicker, FRAGMENT_DATE_PICKER_TAG);
    }

    @CallSuper
    protected boolean isStateInvalid() {
        if (isNull(onDateTimeSetListener)) {
            Timber.w("No OnDateTimeSetListener have been supplied");
            return true;
        }

        return false;
    }

    private void dismissDialogWithInvalidState() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.projects_create_unknown_error_message,
                Snackbar.LENGTH_SHORT
        ).show();

        dismiss();
    }

    /**
     * Dismiss the DateTimePickerFragment.
     * <p/>
     * Triggers the onDestroy-method for additional clean up.
     */
    private void dismiss() {
        getFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    private void showFragmentWithTag(Fragment fragment, String tag) {
        getFragmentManager().beginTransaction()
                .add(fragment, tag)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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

    @FunctionalInterface
    public interface OnDateTimeSetListener {
        /**
         * Listen for the selected date and time.
         *
         * @param calendar Selected date and time.
         */
        void onDateTimeSet(@NonNull Calendar calendar);
    }
}
