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

package me.raatiniemi.worker.presentation.projects.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.view.fragment.DateTimePickerFragment;

public class ClockActivityAtFragment extends DateTimePickerFragment
        implements DateTimePickerFragment.OnDateTimeSetListener {
    private static final String TAG = "ClockActivityAtFragment";

    /**
     * Listener for "OnClockActivityAtListener".
     */
    private OnClockActivityAtListener onClockActivityAtListener;

    /**
     * Create a new instance for project clock in/out with date and time.
     *
     * @param project Project used with the clock activity.
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(Project project) {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();

        // If the project is active we have to set the minimum date for clocking out.
        if (null != project && null != project.getClockedInSince()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getClockedInSince());
            fragment.setMinDate(calendar);
        }

        return fragment;
    }

    /**
     * Setup the fragment, this method is primarily used as a single setup
     * between API versions.
     */
    private void setup() {
        setOnDateTimeSetListener(this);
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
    public void onDateTimeSet(Calendar calendar) {
        if (null == onClockActivityAtListener) {
            Log.e(TAG, "No OnClockActivityAtListener have been supplied");
            return;
        }

        // Send the project row position with the selected
        // date and time to the listener.
        onClockActivityAtListener.onClockActivityAt(calendar);
    }

    /**
     * Set the "OnClockActivityAtListener".
     *
     * @param onClockActivityAtListener Listener for "OnClockActivityAtListener".
     */
    public void setOnClockActivityAtListener(OnClockActivityAtListener onClockActivityAtListener) {
        this.onClockActivityAtListener = onClockActivityAtListener;
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
