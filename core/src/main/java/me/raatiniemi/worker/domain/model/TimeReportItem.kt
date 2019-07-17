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
import me.raatiniemi.worker.domain.util.CalculateTime
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import java.text.SimpleDateFormat
import java.util.*

data class TimeReportItem(private val timeInterval: TimeInterval) : Comparable<TimeReportItem> {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))
    val hoursMinutes: HoursMinutes
        get() = CalculateTime.calculateHoursMinutes(calculateInterval(timeInterval))

    val title: String
        get() {
            val values = when (timeInterval) {
                is TimeInterval.Active -> listOf(timeInterval.start)
                is TimeInterval.Inactive -> listOf(timeInterval.start, timeInterval.stop)
                is TimeInterval.Registered -> listOf(timeInterval.start, timeInterval.stop)
            }

            return values.map(::buildDateFromMilliseconds)
                .joinToString(separator = TIME_SEPARATOR) {
                    timeFormat.format(it)
                }
        }

    val isRegistered = timeInterval is TimeInterval.Registered

    fun asTimeInterval(): TimeInterval {
        return timeInterval
    }

    fun getTimeSummaryWithFormatter(formatter: HoursMinutesFormat): String {
        return formatter.apply(hoursMinutes)
    }

    override fun compareTo(other: TimeReportItem): Int {
        return comparator.compare(this, other)
    }

    companion object {
        private const val TIME_SEPARATOR = " - "
        private val comparator = TimeReportItemComparator()

        private fun buildDateFromMilliseconds(milliseconds: Milliseconds): Date {
            return Date(milliseconds.value)
        }

        @JvmStatic
        fun with(time: TimeInterval): TimeReportItem {
            return TimeReportItem(time)
        }
    }
}
