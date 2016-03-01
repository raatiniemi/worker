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

package me.raatiniemi.worker.domain.util;

import me.raatiniemi.worker.domain.model.CalculatedTime;

public class CalculateTime {
    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY;

    public static CalculatedTime calculateTime(long milliseconds) {
        long seconds = calculateSeconds(milliseconds);
        long minutes = calculateMinutes(seconds);
        long hours = calculateHours(seconds);

        if (MINUTES_IN_HOUR == minutes) {
            minutes = 0;
            hours++;
        }

        return new CalculatedTime(hours, minutes);
    }

    private static long calculateSeconds(long milliseconds) {
        return milliseconds / 1000;
    }

    private static long calculateMinutes(long seconds) {
        long minutes = seconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR;

        long secondsRemaining = seconds % SECONDS_IN_MINUTE;
        if (secondsRemaining >= 30) {
            minutes++;
        }

        return minutes;
    }

    private static long calculateHours(long seconds) {
        long hours = seconds / SECONDS_IN_HOUR % HOURS_IN_DAY;

        long days = seconds / SECONDS_IN_DAY;
        if (days > 0) {
            hours += days * HOURS_IN_DAY;
        }

        return hours;
    }
}
