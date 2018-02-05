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

package me.raatiniemi.worker.domain.util;

import me.raatiniemi.worker.domain.model.HoursMinutes;

public class CalculateTime {
    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY;

    private final long milliseconds;

    private CalculateTime(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public static HoursMinutes calculateHoursMinutes(long milliseconds) {
        CalculateTime calculateTime = new CalculateTime(milliseconds);
        return calculateTime.calculateHoursMinutes();
    }

    private HoursMinutes calculateHoursMinutes() {
        long minutes = calculateMinutes();
        long hours = calculateHours();

        if (MINUTES_IN_HOUR == minutes) {
            minutes = 0;
            hours++;
        }

        return new HoursMinutes(hours, minutes);
    }

    private long calculateSeconds() {
        return milliseconds / 1000;
    }

    private long calculateMinutes() {
        long seconds = calculateSeconds();
        long minutes = seconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR;

        long secondsRemaining = seconds % SECONDS_IN_MINUTE;
        if (secondsRemaining >= 30) {
            minutes++;
        }

        return minutes;
    }

    private long calculateHours() {
        long seconds = calculateSeconds();
        long hours = seconds / SECONDS_IN_HOUR % HOURS_IN_DAY;

        long days = seconds / SECONDS_IN_DAY;
        if (days > 0) {
            hours += days * HOURS_IN_DAY;
        }

        return hours;
    }
}
