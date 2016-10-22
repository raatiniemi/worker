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
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.R;
import static me.raatiniemi.util.NullUtil.isNull;

import static me.raatiniemi.util.NullUtil.nonNull;

public class TimePickerFragment extends DialogFragment {
    private static final String TAG = "TimePickerFragment";

    /**
     * The "OnTimeSetListener" for the TimePickerDialog.
     */
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private DialogInterface.OnCancelListener onCancelListener;

    private DialogInterface.OnDismissListener onDismissListener;

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setup();
        }
    }

    private void setup() {
        // Check that we actually have a listener available.
        if (isNull(onTimeSetListener)) {
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
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity())
        );
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        // Delegate the "onCancel" event to the listener, if available.
        if (nonNull(onCancelListener)) {
            onCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // Delegate the "onDismiss" event to the listener, if available.
        if (nonNull(onDismissListener)) {
            onDismissListener.onDismiss(dialog);
        }
    }

    /**
     * Set the "OnTimeSetListener" for the TimePickerDialog.
     *
     * @param listener "OnTimeSetListener" for the TimePickerDialog.
     */
    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        onTimeSetListener = listener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
