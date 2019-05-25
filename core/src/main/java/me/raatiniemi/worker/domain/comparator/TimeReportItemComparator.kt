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

import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import java.util.*

class TimeReportItemComparator : Comparator<TimeReportItem> {
    private fun compare(lhs: TimeInterval, rhs: TimeInterval): Int {
        if (lhs.stop != rhs.stop) {
            if (null == lhs.stop) {
                return -1
            }

            if (null == rhs.stop) {
                return 1
            }
        }

        if (lhs.start > rhs.start) {
            return -1
        }

        if (lhs.start < rhs.start) {
            return 1
        }

        val lhsStop = lhs.stop ?: Milliseconds.empty
        val rhsStop = rhs.stop ?: Milliseconds.empty
        if (lhsStop > rhsStop) {
            return -1
        }

        return if (lhsStop < rhsStop) {
            1
        } else 0
    }

    override fun compare(o1: TimeReportItem, o2: TimeReportItem): Int {
        return compare(o1.asTimeInterval(), o2.asTimeInterval())
    }
}
