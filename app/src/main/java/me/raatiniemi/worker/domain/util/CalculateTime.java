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
        // Convert milliseconds to seconds.
        long seconds = milliseconds / 1000;

        // Calculate the number of hours and minutes based
        // on the total number of seconds.
        long hours = seconds / SECONDS_IN_HOUR % HOURS_IN_DAY;
        long minutes = seconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR;

        // Check if the interval has passed 24 hours.
        long days = seconds / SECONDS_IN_DAY;
        if (days > 0) {
            hours += days * HOURS_IN_DAY;
        }

        // If the number of seconds is at >= 30 we should add an extra minute
        // to the minutes, i.e. round up the minutes if they have passed 50%.
        //
        // Otherwise, total time of 49 seconds will still display 0m and not 1m.
        long secondsRemaining = seconds % SECONDS_IN_MINUTE;
        if (secondsRemaining >= 30) {
            minutes++;
        }

        // If the minutes reaches 60, we have to reset
        // the minutes and increment the hours.
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
