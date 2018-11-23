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

package me.raatiniemi.worker.domain.model

import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator
import me.raatiniemi.worker.domain.util.CalculateTime
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import java.text.SimpleDateFormat
import java.util.*

data class TimesheetItem(private val timeInterval: TimeInterval) : Comparable<TimesheetItem> {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))
    val hoursMinutes: HoursMinutes = CalculateTime.calculateHoursMinutes(timeInterval.interval)

    val id = timeInterval.id

    val title: String
        get() {
            val title = buildTitleFromStartTime()

            if (!timeInterval.isActive) {
                appendStopTimeWithSeparator(title)
            }

            return title.toString()
        }

    val isRegistered = timeInterval.isRegistered

    fun asTimeInterval(): TimeInterval {
        return timeInterval
    }

    private fun buildTitleFromStartTime(): StringBuilder {
        val builder = StringBuilder()
        builder.append(timeFormat.format(buildDateFromStartTime()))

        return builder
    }

    private fun buildDateFromStartTime(): Date {
        return buildDateFromMilliseconds(timeInterval.startInMilliseconds)
    }

    private fun appendStopTimeWithSeparator(title: StringBuilder) {
        title.append(TIME_SEPARATOR)
        title.append(timeFormat.format(buildDateFromStopTime()))
    }

    private fun buildDateFromStopTime(): Date {
        return buildDateFromMilliseconds(timeInterval.stopInMilliseconds)
    }

    fun getTimeSummaryWithFormatter(formatter: HoursMinutesFormat): String {
        return formatter.apply(hoursMinutes)
    }

    override fun compareTo(other: TimesheetItem): Int {
        return comparator.compare(this, other)
    }

    companion object {
        private const val TIME_SEPARATOR = " - "
        private val comparator = TimesheetItemComparator()

        private fun buildDateFromMilliseconds(milliseconds: Long): Date {
            return Date(milliseconds)
        }

        @JvmStatic
        fun with(time: TimeInterval): TimesheetItem {
            return TimesheetItem(time)
        }
    }
}