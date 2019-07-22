/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.model

class TimeReportTimeIntervalComparator : Comparator<TimeInterval> {
    override fun compare(lhs: TimeInterval, rhs: TimeInterval): Int {
        if (isActive(lhs) && !isActive(rhs)) {
            return -1
        }

        if (!isActive(lhs) && isActive(rhs)) {
            return 1
        }

        if (lhs.start > rhs.start) {
            return -1
        }

        if (lhs.start < rhs.start) {
            return 1
        }

        val lhsStop = when (lhs) {
            is TimeInterval.Inactive -> lhs.stop
            is TimeInterval.Registered -> lhs.stop
            else -> Milliseconds.empty
        }
        val rhsStop = when (rhs) {
            is TimeInterval.Inactive -> rhs.stop
            is TimeInterval.Registered -> rhs.stop
            else -> Milliseconds.empty
        }

        return when {
            lhsStop > rhsStop -> -1
            lhsStop < rhsStop -> 1
            else -> 0
        }
    }
}
