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

package me.raatiniemi.worker.presentation.project.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import me.raatiniemi.worker.domain.model.HoursMinutes;
import me.raatiniemi.worker.domain.model.HoursMinutesUtil;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.domain.util.HoursMinutesFormat;
import me.raatiniemi.worker.presentation.model.ExpandableItem;

public class TimesheetGroup implements ExpandableItem<TimesheetItem> {
    private static final String LANGUAGE_TAG = "en_US";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE (MMM d)", Locale.forLanguageTag(LANGUAGE_TAG));
    private final Date date;
    private final List<TimesheetItem> items;
    private final long daysSinceUnixEpoch;

    private TimesheetGroup(Date date, List<TimesheetItem> items) {
        this.date = date;
        this.items = items;

        daysSinceUnixEpoch = calculateDaysSinceUnixEpoch(date);
    }

    public static TimesheetGroup build(Date date) {
        return build(date, new TreeSet<>());
    }

    public static TimesheetGroup build(Date date, SortedSet<TimesheetItem> timesheetItems) {
        List<TimesheetItem> items = new ArrayList<>();
        items.addAll(timesheetItems);

        return new TimesheetGroup(date, items);
    }

    private static long calculateDaysSinceUnixEpoch(Date date) {
        long milliseconds = date.getTime();
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return hours / 24;
    }

    private HoursMinutes calculateTimeDifference(HoursMinutes accumulated) {
        return accumulated.minus(new HoursMinutes(8, 0));
    }

    private static String formatTimeDifference(String format, String difference) {
        return String.format(Locale.forLanguageTag(LANGUAGE_TAG), format, difference);
    }

    private static String getTimeDifferenceFormat(HoursMinutes difference) {
        if (difference.isEmpty()) {
            return "";
        }

        if (difference.isPositive()) {
            return " (+%s)";
        }

        return " (%s)";
    }

    public long getId() {
        return daysSinceUnixEpoch;
    }

    public String getTitle() {
        return dateFormat.format(date);
    }

    public String getFirstLetterFromTitle() {
        return String.valueOf(getTitle().charAt(0));
    }

    public boolean isRegistered() {
        boolean registered = true;

        for (TimesheetItem item : items) {
            if (!item.isRegistered()) {
                registered = false;
                break;
            }
        }

        return registered;
    }

    public String getTimeSummaryWithDifference(HoursMinutesFormat formatter) {
        HoursMinutes accumulated = accumulatedHoursMinutes();
        String timeSummary = formatter.apply(accumulated);

        HoursMinutes calculatedDifference = calculateTimeDifference(accumulated);
        String timeDifference = formatTimeDifference(
                getTimeDifferenceFormat(calculatedDifference),
                formatter.apply(calculatedDifference)
        );

        return timeSummary + timeDifference;
    }

    private HoursMinutes accumulatedHoursMinutes() {
        List<HoursMinutes> times = new ArrayList<>();

        for (TimesheetItem item : items) {
            times.add(item.getHoursMinutes());
        }

        return HoursMinutesUtil.accumulated(times);
    }

    public List<TimesheetAdapterResult> buildItemResultsWithGroupIndex(int groupIndex) {
        ArrayList<TimesheetAdapterResult> results = new ArrayList<>();

        int childIndex = 0;

        for (TimesheetItem item : items) {
            results.add(TimesheetAdapterResult.build(groupIndex, childIndex, item));

            childIndex++;
        }

        return results;
    }

    @Override
    public TimesheetItem get(int index) {
        return items.get(index);
    }

    @Override
    public void set(int index, TimesheetItem item) {
        items.set(index, item);
    }

    @Override
    public TimesheetItem remove(int index) {
        return items.remove(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
