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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.R;

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "DatePickerFragment";

    /**
     * Minimum date available for the date picker.
     */
    private Calendar minDate;

    /**
     * Maximum date available for the date picker.
     */
    private Calendar maxDate;

    /**
     * The "OnDateSetListener" for the DatePickerDialog.
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private DialogInterface.OnCancelListener onCancelListener;

    private DialogInterface.OnDismissListener onDismissListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check that we actually have a listener available.
        if (null == onDateSetListener) {
            // The real reason for failure is to technical to display to the
            // user, hence the unknown error message.
            //
            // And, the listener should always be available in the production
            // version, i.e. this should just be seen as developer feedback.
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_unknown,
                    Snackbar.LENGTH_SHORT
            ).show();

            Log.w(TAG, "No OnDateSetListener have been supplied");
            dismiss();
        }
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
        if (null != minDate) {
            dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }

        // If a maximum date is available we have
        // to set it on the date picker.
        if (null != maxDate) {
            dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        }

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        // Delegate the "onCancel" event to the listener, if available.
        if (null != onCancelListener) {
            onCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // Delegate the "onDismiss" event to the listener, if available.
        if (null != onDismissListener) {
            onDismissListener.onDismiss(dialog);
        }
    }

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    public void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }

    /**
     * Set the maximum date for the date picker.
     *
     * @param maxDate Maximum date.
     */
    public void setMaxDate(Calendar maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * Set the "OnDateSetListener" for the DatePickerDialog.
     *
     * @param listener "OnDateSetListener" for the DatePickerDialog.
     */
    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        onDateSetListener = listener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
