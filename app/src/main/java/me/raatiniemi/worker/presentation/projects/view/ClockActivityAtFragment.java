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

import android.util.Log;

import java.util.Calendar;

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.view.fragment.DateTimePickerFragment;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

public class ClockActivityAtFragment extends DateTimePickerFragment
        implements DateTimePickerFragment.OnDateTimeSetListener {
    private static final String TAG = "ClockActivityAtFragment";

    /**
     * Listener for "OnClockActivityAtListener".
     */
    private OnClockActivityAtListener onClockActivityAtListener;

    public ClockActivityAtFragment() {
        setOnDateTimeSetListener(this);
    }

    /**
     * Create a new instance for project clock in/out with date and time.
     *
     * @param project Project used with the clock activity.
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(Project project) {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();

        // If the project is active we have to set the minimum date for clocking out.
        if (nonNull(project) && nonNull(project.getClockedInSince())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getClockedInSince());
            fragment.setMinDate(calendar);
        }

        return fragment;
    }

    @Override
    public void onDateTimeSet(Calendar calendar) {
        if (isNull(onClockActivityAtListener)) {
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
