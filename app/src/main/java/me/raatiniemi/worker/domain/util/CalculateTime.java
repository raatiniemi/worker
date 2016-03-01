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

public class CalculateTime {
    private static final int sHoursInDay = 24;
    private static final int sMinutesInHour = 60;
    private static final int sSecondsInMinute = 60;
    private static final int sSecondsInHour = sSecondsInMinute * sMinutesInHour;
    private static final int sSecondsInDay = sSecondsInHour * sHoursInDay;

    public static CalculatedTime calculateTime(long milliseconds) {
        long seconds = calculateSeconds(milliseconds);
        long minutes = calculateMinutes(seconds);
        long hours = calculateHours(seconds);

        if (sMinutesInHour == minutes) {
            minutes = 0;
            hours++;
        }

        return new CalculatedTime(hours, minutes);
    }

    private static long calculateSeconds(long milliseconds) {
        return milliseconds / 1000;
    }

    private static long calculateMinutes(long seconds) {
        long minutes = seconds / sSecondsInMinute % sMinutesInHour;

        long secondsRemaining = seconds % sSecondsInMinute;
        if (secondsRemaining >= 30) {
            minutes++;
        }

        return minutes;
    }

    private static long calculateHours(long seconds) {
        long hours = seconds / sSecondsInHour % sHoursInDay;

        long days = seconds / sSecondsInDay;
        if (days > 0) {
            hours += days * sHoursInDay;
        }

        return hours;
    }

    public static class CalculatedTime {
        public final long hours;
        public final long minutes;

        public CalculatedTime(long hours, long minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }
    }
}
