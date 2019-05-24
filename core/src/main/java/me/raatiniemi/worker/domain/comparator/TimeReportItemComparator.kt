/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.comparator

import java.util.Comparator

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem

class TimeReportItemComparator : Comparator<TimeReportItem> {
    private fun isActive(timeInterval: TimeInterval): Boolean {
        return null == timeInterval.stop
    }

    private fun isBefore(lhs: Long, rhs: Long): Boolean {
        return lhs < rhs
    }

    private fun isAfter(lhs: Long, rhs: Long): Boolean {
        return lhs > rhs
    }

    private fun compare(lhs: TimeInterval, rhs: TimeInterval): Int {
        if (lhs.stop != rhs.stop) {
            if (isActive(lhs)) {
                return -1
            }

            if (isActive(rhs)) {
                return 1
            }
        }

        if (isAfter(lhs.start.value, rhs.start.value)) {
            return -1
        }

        if (isBefore(lhs.start.value, rhs.start.value)) {
            return 1
        }

        val lhsStop = lhs.stop?.value ?: 0
        val rhsStop = rhs.stop?.value ?: 0
        if (isAfter(lhsStop, rhsStop)) {
            return -1
        }

        return if (isBefore(lhsStop, rhsStop)) {
            1
        } else 0
    }

    override fun compare(o1: TimeReportItem, o2: TimeReportItem): Int {
        return compare(o1.asTimeInterval(), o2.asTimeInterval())
    }
}
