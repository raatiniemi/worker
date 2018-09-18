/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.features.projects.view;

import android.support.annotation.NonNull;

import java.util.Calendar;

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.shared.view.fragment.DateTimePickerFragment;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

public class ClockActivityAtFragment extends DateTimePickerFragment
        implements DateTimePickerFragment.OnDateTimeSetListener {
    private OnClockActivityAtListener onClockActivityAtListener;

    public ClockActivityAtFragment() {
        setOnDateTimeSetListener(this);
    }

    /**
     * Create a new instance for project clock in/out with date and time.
     *
     * @param project                   Project used with the clock activity.
     * @param onClockActivityAtListener Listener for "OnClockActivityAtListener".
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(
            Project project,
            OnClockActivityAtListener onClockActivityAtListener
    ) {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();
        fragment.onClockActivityAtListener = onClockActivityAtListener;

        // If the project is active we have to set the minimum date for clocking out.
        if (nonNull(project) && nonNull(project.getClockedInSince())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getClockedInSince());
            fragment.setMinDate(calendar);
        }

        return fragment;
    }

    @Override
    protected boolean isStateInvalid() {
        if (isNull(onClockActivityAtListener)) {
            Timber.w("No OnClockActivityAtListener have been supplied");
            return true;
        }

        return super.isStateInvalid();
    }

    @Override
    public void onDateTimeSet(@NonNull Calendar calendar) {
        onClockActivityAtListener.onClockActivityAt(calendar);
    }

    @FunctionalInterface
    public interface OnClockActivityAtListener {
        /**
         * Triggered after the date and time have been selected.
         *
         * @param calendar Calendar with date and time to clock in or out.
         */
        void onClockActivityAt(@NonNull Calendar calendar);
    }
}
