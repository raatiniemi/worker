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

package me.raatiniemi.worker.domain.model

import me.raatiniemi.worker.domain.comparator.TimeReportItemComparator

sealed class TimeReportItem : Comparable<TimeReportItem> {
    abstract fun asTimeInterval(): TimeInterval

    override fun compareTo(other: TimeReportItem): Int {
        return comparator.compare(this, other)
    }

    data class Active internal constructor(
        private val timeInterval: TimeInterval.Active
    ) : TimeReportItem() {
        override fun asTimeInterval() = timeInterval
    }

    data class Inactive internal constructor(
        private val timeInterval: TimeInterval.Inactive
    ) : TimeReportItem() {
        override fun asTimeInterval() = timeInterval
    }

    data class Registered internal constructor(
        private val timeInterval: TimeInterval.Registered
    ) : TimeReportItem() {
        override fun asTimeInterval() = timeInterval
    }

    companion object {
        private val comparator = TimeReportItemComparator()

        @JvmStatic
        fun with(time: TimeInterval): TimeReportItem = when (time) {
            is TimeInterval.Active -> Active(time)
            is TimeInterval.Inactive -> Inactive(time)
            is TimeInterval.Registered -> Registered(time)
        }
    }
}
