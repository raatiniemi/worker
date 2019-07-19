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
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.domain.util.calculateHoursMinutes
import java.text.SimpleDateFormat
import java.util.*

sealed class TimeReportItem : Comparable<TimeReportItem> {
    abstract val hoursMinutes: HoursMinutes
    abstract val title: String

    abstract fun asTimeInterval(): TimeInterval

    abstract fun getTimeSummaryWithFormatter(formatter: HoursMinutesFormat): String

    override fun compareTo(other: TimeReportItem): Int {
        return comparator.compare(this, other)
    }

    data class Active internal constructor(
        private val timeInterval: TimeInterval.Active
    ) : TimeReportItem() {
        override val hoursMinutes: HoursMinutes
            get() = calculateHoursMinutes(calculateInterval(timeInterval))

        override val title: String
            get() {
                val values = listOf(timeInterval.start)

                return values.map(::buildDateFromMilliseconds)
                    .joinToString(separator = TIME_SEPARATOR) {
                        timeFormat.format(it)
                    }
            }

        override fun asTimeInterval() = timeInterval

        override fun getTimeSummaryWithFormatter(formatter: HoursMinutesFormat): String {
            return formatter.apply(hoursMinutes)
        }
    }

    data class Inactive internal constructor(
        private val timeInterval: TimeInterval.Inactive
    ) : TimeReportItem() {
        override val hoursMinutes: HoursMinutes
            get() = calculateHoursMinutes(calculateInterval(timeInterval))

        override val title: String
            get() {
                val values = listOf(timeInterval.start, timeInterval.stop)
                return values.map(::buildDateFromMilliseconds)
                    .joinToString(separator = TIME_SEPARATOR) {
                        timeFormat.format(it)
                    }
            }

        override fun asTimeInterval() = timeInterval

        override fun getTimeSummaryWithFormatter(formatter: HoursMinutesFormat): String {
            return formatter.apply(hoursMinutes)
        }
    }

    data class Registered internal constructor(
        private val timeInterval: TimeInterval.Registered
    ) : TimeReportItem() {
        override val hoursMinutes: HoursMinutes
            get() = calculateHoursMinutes(calculateInterval(timeInterval))

        override val title: String
            get() {
                val values = listOf(timeInterval.start, timeInterval.stop)
                return values.map(::buildDateFromMilliseconds)
                    .joinToString(separator = TIME_SEPARATOR) {
                        timeFormat.format(it)
                    }
            }

        override fun asTimeInterval() = timeInterval

        override fun getTimeSummaryWithFormatter(formatter: HoursMinutesFormat): String {
            return formatter.apply(hoursMinutes)
        }
    }

    companion object {
        private const val TIME_SEPARATOR = " - "
        private val comparator = TimeReportItemComparator()
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))

        private fun buildDateFromMilliseconds(milliseconds: Milliseconds): Date {
            return Date(milliseconds.value)
        }

        @JvmStatic
        fun with(time: TimeInterval): TimeReportItem = when (time) {
            is TimeInterval.Active -> Active(time)
            is TimeInterval.Inactive -> Inactive(time)
            is TimeInterval.Registered -> Registered(time)
        }
    }
}
