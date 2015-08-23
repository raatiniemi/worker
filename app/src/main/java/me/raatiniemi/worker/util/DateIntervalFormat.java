/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.util;

/**
 * Format time interval.
 */
public class DateIntervalFormat {
    /**
     * Private constructor, preventing instance creation.
     */
    private DateIntervalFormat() {
    }

    /**
     * Format the interval with fractal hours, e.g. "8.25" for 8 hours and 15 minutes.
     *
     * @param hours   Hours to apply to the format.
     * @param minutes Minutes to apply to the format.
     * @return Interval formatted with fractal hours.
     */
    private static String fractionHours(long hours, long minutes) {
        // Calculate the fractal value from the minutes.
        float fractal = (float) minutes / (float) 60.0;

        double interval = hours + fractal;
        return String.format("%.2f", interval);
    }

    /**
     * Format interval with hours and minutes.
     *
     * @param hours   Hours to apply to the format.
     * @param minutes Minutes to apply to the format.
     * @return Interval formatted with hours and minutes.
     */
    private static String hoursMinutes(long hours, long minutes) {
        String format = "%1$dh %2$dm";

        // If no hours is available, remove it from the format.
        if (0 == hours) {
            format = "%2$dm";
        }

        return String.format(format, hours, minutes);
    }

    /**
     * Format the interval with given format type.
     *
     * @param interval Interval in milliseconds to format.
     * @param type     Type of format to use.
     * @return Interval formatted with given type.
     */
    public static String format(long interval, Type type) {
        String format;

        // Convert milliseconds to seconds.
        interval = interval / 1000;

        // Calculate the number of hours and minutes based
        // on the total number of seconds.
        long hours = (interval / (60 * 60) % 24);
        long minutes = (interval / 60 % 60);

        // Check if the interval has passed 24 hours.
        long days = (interval / (60 * 60 * 24));
        if (days > 0) {
            hours += (days * 24);
        }

        // If the number of seconds is at >= 30 we should add an extra minute
        // to the minutes, i.e. round up the minutes if they have passed 50%.
        //
        // Otherwise, total time of 49 seconds will still display 0m and not 1m.
        long seconds = (interval % 60);
        if (seconds >= 30) {
            minutes++;
        }

        // If the minutes reaches 60, we have to reset
        // the minutes and increment the hours.
        if (minutes == 60) {
            minutes = 0;
            hours++;
        }

        // Determined what kind of format type to use.
        switch (type) {
            case FRACTION_HOURS:
                format = fractionHours(hours, minutes);
                break;
            case HOURS_MINUTES:
            default:
                format = hoursMinutes(hours, minutes);
        }

        return format;
    }

    /**
     * Format the interval with the default format type.
     *
     * @param interval Interval in milliseconds to format.
     * @return Interval formatted with the default format type.
     */
    public static String format(long interval) {
        return format(interval, Type.HOURS_MINUTES);
    }

    /**
     * Available format types.
     */
    public enum Type {
        /**
         * Format the interval with fraction hours, e.g. "8.25" for 8 hours and 15 minutes.
         */
        FRACTION_HOURS,

        /**
         * Format the interval with hours and minutes, e.g. "5h 12m".
         */
        HOURS_MINUTES
    }
}
