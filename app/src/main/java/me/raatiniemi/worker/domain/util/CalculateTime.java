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
    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY;

    public static CalculatedTime calculateTime(long milliseconds) {
        long seconds = milliseconds / 1000;

        long hours = seconds / SECONDS_IN_HOUR % HOURS_IN_DAY;
        long minutes = seconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR;

        long days = seconds / SECONDS_IN_DAY;
        if (days > 0) {
            hours += days * HOURS_IN_DAY;
        }

        long secondsRemaining = seconds % SECONDS_IN_MINUTE;
        if (secondsRemaining >= 30) {
            minutes++;
        }

        if (MINUTES_IN_HOUR == minutes) {
            minutes = 0;
            hours++;
        }

        return new CalculatedTime(hours, minutes);
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
