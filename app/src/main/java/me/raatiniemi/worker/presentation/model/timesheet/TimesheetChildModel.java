/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.model.timesheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetChildModel {
    private static final SimpleDateFormat sTimeFormat;

    static {
        sTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    private final Time mTime;

    public TimesheetChildModel(Time time) {
        mTime = time;
    }

    public Time asTime() {
        return mTime;
    }

    public String getTitle() {
        String title = sTimeFormat.format(new Date(mTime.getStartInMilliseconds()));

        if (!mTime.isActive()) {
            title += " - " + sTimeFormat.format(new Date(mTime.getStopInMilliseconds()));
        }

        return title;
    }

    public String getTimeSummary() {
        return DateIntervalFormat.format(
                mTime.getInterval(),
                DateIntervalFormat.Type.FRACTION_HOURS
        );
    }

    public boolean isRegistered() {
        return mTime.isRegistered();
    }
}
