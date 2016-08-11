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

import me.raatiniemi.worker.domain.model.CalculatedTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.util.CalculateTime;
import me.raatiniemi.worker.presentation.util.DateIntervalFormat;
import me.raatiniemi.worker.presentation.util.FractionIntervalFormat;

public class TimesheetChildModel {
    private static final String TIME_SEPARATOR = " - ";
    private static final DateIntervalFormat intervalFormat;

    static {
        intervalFormat = new FractionIntervalFormat();
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final Time time;

    public TimesheetChildModel(Time time) {
        this.time = time;
    }

    private static Date buildDateFromMilliseconds(long milliseconds) {
        return new Date(milliseconds);
    }

    public Time asTime() {
        return time;
    }

    public long getId() {
        return time.getId();
    }

    public String getTitle() {
        StringBuilder title = buildTitleFromStartTime();

        if (!time.isActive()) {
            appendStopTimeWithSeparator(title);
        }

        return title.toString();
    }

    private StringBuilder buildTitleFromStartTime() {
        StringBuilder builder = new StringBuilder();
        builder.append(timeFormat.format(buildDateFromStartTime()));

        return builder;
    }

    private Date buildDateFromStartTime() {
        return buildDateFromMilliseconds(time.getStartInMilliseconds());
    }

    private void appendStopTimeWithSeparator(StringBuilder title) {
        title.append(TIME_SEPARATOR);
        title.append(timeFormat.format(buildDateFromStopTime()));
    }

    private Date buildDateFromStopTime() {
        return buildDateFromMilliseconds(time.getStopInMilliseconds());
    }

    public String getTimeSummary() {
        return intervalFormat.format(time.getInterval());
    }

    public boolean isRegistered() {
        return time.isRegistered();
    }

    long calculateIntervalInMilliseconds() {
        CalculatedTime calculatedTime = CalculateTime.calculateTime(time.getInterval());
        return calculatedTime.asMilliseconds();
    }
}
