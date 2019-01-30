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

package me.raatiniemi.worker.features.project.timereport.model

import me.raatiniemi.worker.domain.model.HoursMinutes
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.accumulated
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import java.util.*

data class TimeReportGroup internal constructor(
        val date: Date,
        private val items: List<TimeReportItem>
) {
    val isRegistered: Boolean
        get() = items.any { it.isRegistered }

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

    fun buildItemResultsWithGroupIndex(groupIndex: Int): List<TimeReportAdapterResult> {
        return items.mapIndexedTo(ArrayList()) { childIndex, item ->
            TimeReportAdapterResult(groupIndex, childIndex, item)
        }
    }

    companion object {
        private const val LANGUAGE_TAG = "en_US"

        fun build(date: Date, timeReportItems: SortedSet<TimeReportItem>): TimeReportGroup {
            val items = ArrayList<TimeReportItem>()
            items.addAll(timeReportItems)

            return TimeReportGroup(date, items)
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
