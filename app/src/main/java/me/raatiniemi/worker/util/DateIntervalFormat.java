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

package me.raatiniemi.worker.util;

import me.raatiniemi.worker.domain.model.CalculatedTime;
import me.raatiniemi.worker.domain.util.CalculateTime;

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
        CalculatedTime calculatedTime = CalculateTime.calculateTime(interval);

        // Determined what kind of format type to use.
        switch (type) {
            case FRACTION_HOURS:
                return fractionHours(
                        calculatedTime.hours,
                        calculatedTime.minutes
                );
            case HOURS_MINUTES:
            default:
                return hoursMinutes(
                        calculatedTime.hours,
                        calculatedTime.minutes
                );
        }
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
