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

package me.raatiniemi.worker.features.project.timesheet.model

import me.raatiniemi.worker.domain.model.HoursMinutes
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.domain.model.accumulated
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.shared.model.ExpandableItem
import java.text.SimpleDateFormat
import java.util.*

class TimesheetGroup private constructor(
        private val date: Date,
        private val items: MutableList<TimesheetItem>
) : ExpandableItem<TimesheetItem> {
    private val dateFormat = SimpleDateFormat("EEE (MMM d)", Locale.forLanguageTag(LANGUAGE_TAG))
    val id: Long

    val title: String
        get() = dateFormat.format(date)

    val firstLetterFromTitle: String
        get() = title.first().toString()

    val isRegistered: Boolean
        get() {
            return items.any { it.isRegistered }
        }

    init {
        id = calculateDaysSinceUnixEpoch(date)
    }

    private fun calculateTimeDifference(accumulated: HoursMinutes): HoursMinutes {
        return accumulated.minus(HoursMinutes(8, 0))
    }

    fun getTimeSummaryWithDifference(formatter: HoursMinutesFormat): String {
        val accumulated = accumulatedHoursMinutes()
        val timeSummary = formatter.apply(accumulated)

        val calculatedDifference = calculateTimeDifference(accumulated)
        val timeDifference = formatTimeDifference(
                getTimeDifferenceFormat(calculatedDifference),
                formatter.apply(calculatedDifference)
        )

        return timeSummary + timeDifference
    }

    private fun accumulatedHoursMinutes(): HoursMinutes {
        return items.map { it.hoursMinutes }
                .accumulated()
    }

    fun buildItemResultsWithGroupIndex(groupIndex: Int): List<TimesheetAdapterResult> {
        return items.mapIndexedTo(ArrayList()) { childIndex, item ->
            TimesheetAdapterResult(groupIndex, childIndex, item)
        }
    }

    override fun get(index: Int): TimesheetItem {
        return items[index]
    }

    override fun set(index: Int, item: TimesheetItem) {
        items[index] = item
    }

    override fun remove(index: Int): TimesheetItem {
        return items.removeAt(index)
    }

    override fun size(): Int {
        return items.size
    }

    companion object {
        private const val LANGUAGE_TAG = "en_US"

        fun build(date: Date, timesheetItems: SortedSet<TimesheetItem>): TimesheetGroup {
            val items = ArrayList<TimesheetItem>()
            items.addAll(timesheetItems)

            return TimesheetGroup(date, items)
        }

        private fun calculateDaysSinceUnixEpoch(date: Date): Long {
            val milliseconds = date.time
            val seconds = milliseconds / 1000
            val minutes = seconds / 60
            val hours = minutes / 60

            return hours / 24
        }

        private fun formatTimeDifference(format: String, difference: String): String {
            return String.format(Locale.forLanguageTag(LANGUAGE_TAG), format, difference)
        }

        private fun getTimeDifferenceFormat(difference: HoursMinutes): String {
            if (difference.empty) {
                return ""
            }

            if (difference.positive) {
                return " (+%s)"
            }
            return " (%s)"
        }
    }
}