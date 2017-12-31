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

package me.raatiniemi.worker.domain.model;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator;
import me.raatiniemi.worker.domain.util.CalculateTime;
import me.raatiniemi.worker.domain.util.DateIntervalFormat;
import me.raatiniemi.worker.domain.util.FractionIntervalFormat;

public final class TimesheetItem implements Comparable<TimesheetItem> {
    private static final String TIME_SEPARATOR = " - ";
    private static final Comparator<TimesheetItem> comparator;
    private static final DateIntervalFormat intervalFormat;

    static {
        comparator = new TimesheetItemComparator();
        intervalFormat = new FractionIntervalFormat();
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"));
    private final Time time;
    private final CalculatedTime calculatedTime;

    private TimesheetItem(Time time) {
        this.time = time;

        calculatedTime = CalculateTime.calculateTime(time.getInterval());
    }

    private static Date buildDateFromMilliseconds(long milliseconds) {
        return new Date(milliseconds);
    }

    public static TimesheetItem with(Time time) {
        return new TimesheetItem(time);
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

    public CalculatedTime getCalculatedTime() {
        return calculatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TimesheetItem)) {
            return false;
        }

        TimesheetItem that = (TimesheetItem) o;
        return time.equals(that.time);
    }

    @Override
    public int hashCode() {
        return time.hashCode();
    }

    @Override
    public int compareTo(TimesheetItem o) {
        return comparator.compare(this, o);
    }
}
