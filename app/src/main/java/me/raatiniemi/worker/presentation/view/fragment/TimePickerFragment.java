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
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.R;

public class TimePickerFragment extends DialogFragment {
    private static final String TAG = "TimePickerFragment";

    /**
     * The "OnTimeSetListener" for the TimePickerDialog.
     */
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;

    private DialogInterface.OnCancelListener mOnCancelListener;

    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check that we actually have a listener available.
        if (null == mOnTimeSetListener) {
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

            Log.w(TAG, "No OnTimeSetListener have been supplied");
            dismiss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        return new TimePickerDialog(
                getActivity(),
                mOnTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity())
        );
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        // Delegate the "onCancel" event to the listener, if available.
        if (null != mOnCancelListener) {
            mOnCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // Delegate the "onDismiss" event to the listener, if available.
        if (null != mOnDismissListener) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    /**
     * Set the "OnTimeSetListener" for the TimePickerDialog.
     *
     * @param listener "OnTimeSetListener" for the TimePickerDialog.
     */
    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        mOnTimeSetListener = listener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
}
