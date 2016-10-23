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

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

public class DatePickerFragment extends BaseDialogFragment {
    private static final String TAG = "DatePickerFragment";

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    /**
     * Minimum date available for the date picker.
     */
    private Calendar minDate;

    /**
     * Maximum date available for the date picker.
     */
    private Calendar maxDate;

    public static DatePickerFragment newInstance(
            DatePickerDialog.OnDateSetListener onTimeSetListener
    ) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.onDateSetListener = onTimeSetListener;

        return fragment;
    }

    @Override
    protected boolean isStateValid() {
        if (isNull(onDateSetListener)) {
            Log.w(TAG, "No OnDateSetListener have been supplied");
            return false;
        }

        return true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // If a minimum date is available we have
        // to set it on the date picker.
        if (nonNull(minDate)) {
            dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }

        // If a maximum date is available we have
        // to set it on the date picker.
        if (nonNull(maxDate)) {
            dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        }

        return dialog;
    }

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    public void setMinDate(@NonNull Calendar minDate) {
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
    public void setMaxDate(@NonNull Calendar maxDate) {
        if (maxDate.before(minDate)) {
            throw new IllegalArgumentException("Maximum date occurs before minimum date");
        }

        this.maxDate = maxDate;
    }
}
