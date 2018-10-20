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

package me.raatiniemi.worker.domain.comparator;

import java.util.Comparator;

import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimesheetItem;

public class TimesheetItemComparator implements Comparator<TimesheetItem> {
    private static boolean isActive(TimeInterval timeInterval) {
        return 0 == timeInterval.getStopInMilliseconds();
    }

    private static boolean isBefore(long lhs, long rhs) {
        return lhs < rhs;
    }

    private static boolean isAfter(long lhs, long rhs) {
        return lhs > rhs;
    }

    private static int compare(TimeInterval lhs, TimeInterval rhs) {
        if (lhs.getStopInMilliseconds() != rhs.getStopInMilliseconds()) {
            if (isActive(lhs)) {
                return -1;
            }

            if (isActive(rhs)) {
                return 1;
            }
        }

        if (isAfter(lhs.getStartInMilliseconds(), rhs.getStartInMilliseconds())) {
            return -1;
        }

        if (isBefore(lhs.getStartInMilliseconds(), rhs.getStartInMilliseconds())) {
            return 1;
        }

        if (isAfter(lhs.getStopInMilliseconds(), rhs.getStopInMilliseconds())) {
            return -1;
        }

        if (isBefore(lhs.getStopInMilliseconds(), rhs.getStopInMilliseconds())) {
            return 1;
        }

        return 0;
    }

    @Override
    public int compare(TimesheetItem o1, TimesheetItem o2) {
        return compare(o1.asTimeInterval(), o2.asTimeInterval());
    }
}
