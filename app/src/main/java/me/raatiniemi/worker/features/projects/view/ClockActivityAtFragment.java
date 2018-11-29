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

package me.raatiniemi.worker.features.projects.view;

import java.util.Calendar;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import me.raatiniemi.worker.features.shared.view.fragment.DateTimePickerFragment;

public class ClockActivityAtFragment extends DateTimePickerFragment
        implements DateTimePickerFragment.OnDateTimeSetListener {
    private OnClockActivityAtListener onClockActivityAtListener;

    public ClockActivityAtFragment() {
        setOnDateTimeSetListener(this);
    }

    /**
     * Create a new instance for project clock in/out with date and time.
     *
     * @param projectsItem              Project used with the clock activity.
     * @param onClockActivityAtListener Listener for "OnClockActivityAtListener".
     * @return New instance of the clock activity at fragment.
     */
    public static ClockActivityAtFragment newInstance(
            @Nonnull ProjectsItem projectsItem,
            @Nonnull OnClockActivityAtListener onClockActivityAtListener
    ) {
        ClockActivityAtFragment fragment = new ClockActivityAtFragment();
        fragment.onClockActivityAtListener = onClockActivityAtListener;

        if (projectsItem.isActive()) {
            // TODO: Should the calendar be hidden behind a method call?
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(projectsItem.getClockedInSinceInMilliseconds());
            fragment.setMinDate(calendar);
        }

        return fragment;
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
