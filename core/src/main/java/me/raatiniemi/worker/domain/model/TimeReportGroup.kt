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

import java.util.*

data class TimeReportGroup internal constructor(
        val date: Date,
        val items: List<TimeReportItem>
) {
    val isRegistered: Boolean
        get() = items.any { it.isRegistered }

    val timeSummary: HoursMinutes by lazy {
        accumulatedHoursMinutes()
    }

    private fun accumulatedHoursMinutes(): HoursMinutes {
        return items.map { it.hoursMinutes }
                .accumulated()
    }

    val timeDifference: HoursMinutes by lazy {
        timeSummary - HoursMinutes(8, 0)
    }

    companion object {
        fun build(date: Date, timeReportItems: SortedSet<TimeReportItem>): TimeReportGroup {
            val items = ArrayList<TimeReportItem>()
            items.addAll(timeReportItems)

            return TimeReportGroup(date, items)
        }
    }
}
