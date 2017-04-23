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

import me.raatiniemi.worker.presentation.model.ExpandableItem;
import me.raatiniemi.worker.presentation.util.DateIntervalFormat;
import me.raatiniemi.worker.presentation.util.FractionIntervalFormat;

public class TimesheetGroupItem extends ExpandableItem<TimesheetChildItem> {
    private static final String LANGUAGE_TAG = "en_US";
    private static final DateIntervalFormat intervalFormat;

    static {
        intervalFormat = new FractionIntervalFormat();
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE (MMM d)", Locale.forLanguageTag(LANGUAGE_TAG));
    private final Date date;
    private final long daysSinceUnixEpoch;

    private TimesheetGroupItem(Date date) {
        this.date = date;
        daysSinceUnixEpoch = calculateDaysSinceUnixEpoch(date);
    }

    public static TimesheetGroupItem build(Date date) {
        return new TimesheetGroupItem(date);
    }

    private static long calculateDaysSinceUnixEpoch(Date date) {
        long milliseconds = date.getTime();
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return hours / 24;
    }

    private static float calculateFractionFromMilliseconds(long intervalInMilliseconds) {
        String fraction = intervalFormat.format(intervalInMilliseconds);

        return Float.parseFloat(fraction);
    }

    private static float calculateTimeDifference(String timeSummary) {
        return Float.parseFloat(timeSummary) - 8;
    }

    private static String getFormattedTimeDifference(float difference) {
        return String.format(
                Locale.forLanguageTag(LANGUAGE_TAG),
                getTimeDifferenceFormat(difference),
                difference
        );
    }

    private static String getTimeDifferenceFormat(float difference) {
        if (0 == Float.compare(0, difference)) {
            return "";
        }

        if (0 < difference) {
            return " (+%.2f)";
        }

        return " (%.2f)";
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

        for (TimesheetChildItem childItem : getItems()) {
            if (!childItem.isRegistered()) {
                registered = false;
                break;
            }
        }

        return registered;
    }

    public String getTimeSummaryWithDifference() {
        String timeSummary = getTimeSummary();

        float difference = calculateTimeDifference(timeSummary);
        return timeSummary + getFormattedTimeDifference(difference);
    }

    private String getTimeSummary() {
        return String.format(
                Locale.forLanguageTag(LANGUAGE_TAG),
                "%.2f",
                calculateTimeIntervalSummary()
        );
    }

    private float calculateTimeIntervalSummary() {
        float interval = 0;

        for (TimesheetChildItem childItem : getItems()) {
            interval += calculateFractionFromMilliseconds(
                    childItem.calculateIntervalInMilliseconds()
            );
        }

        return interval;
    }

    public List<TimeInAdapterResult> buildItemResultsWithGroupIndex(int groupIndex) {
        ArrayList<TimeInAdapterResult> results = new ArrayList<>();

        int childIndex = 0;

        for (TimesheetChildItem childItem : getItems()) {
            results.add(
                    TimeInAdapterResult.build(
                            groupIndex,
                            childIndex,
                            childItem.asTime()
                    )
            );

            childIndex++;
        }

        return results;
    }
}
