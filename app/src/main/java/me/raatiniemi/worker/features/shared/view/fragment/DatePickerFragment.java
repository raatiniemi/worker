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
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import androidx.annotation.NonNull;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

public class DatePickerFragment extends BaseDialogFragment {
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private Calendar minDate;
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
            Timber.w("No OnDateSetListener have been supplied");
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
