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

package me.raatiniemi.worker.domain.model;

public class CalculatedTime {
    private static final int sMinutesInHour = 60;
    private static final int sSecondsInMinute = 60;
    private static final int sMillisecondsInSecond = 1000;
    public final long hours;
    private final long mMinutes;

    public CalculatedTime(long hours, long minutes) {
        this.hours = hours;
        mMinutes = minutes;
    }

    public long getMinutes() {
        return mMinutes;
    }

    public long asMilliseconds() {
        return calculateSeconds() * sMillisecondsInSecond;
    }

    private long calculateSeconds() {
        return calculateMinutes() * sSecondsInMinute;
    }

    private long calculateMinutes() {
        final long hoursInMinutes = this.hours * sMinutesInHour;

        return hoursInMinutes + getMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof CalculatedTime)) {
            return false;
        }

        CalculatedTime calculatedTime = (CalculatedTime) o;
        return calculatedTime.hours == hours
                && calculatedTime.getMinutes() == getMinutes();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (hours ^ (hours >>> 32));
        result = 31 * result + (int) (getMinutes() ^ (getMinutes() >>> 32));
        return result;
    }
}
