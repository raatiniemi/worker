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

import me.raatiniemi.worker.domain.util.calculateHoursMinutes
import java.util.*

data class TimeReportDay(val date: Date, val timeIntervals: List<TimeInterval>) {
    val isRegistered: Boolean by lazy {
        timeIntervals.all { it is TimeInterval.Registered }
    }

    val timeSummary: HoursMinutes
        get() = accumulatedHoursMinutes()

    private fun accumulatedHoursMinutes(): HoursMinutes {
        return timeIntervals.map { calculateHoursMinutes(calculateInterval(it)) }
            .accumulated()
    }

    val timeDifference: HoursMinutes by lazy {
        timeSummary - HoursMinutes(8, 0)
    }
}
