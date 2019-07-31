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

sealed class TimeReportDay {
    abstract val date: Date
    abstract val timeIntervals: List<TimeInterval>

    abstract val isRegistered: Boolean

    abstract val timeSummary: HoursMinutes

    abstract val timeDifference: HoursMinutes

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    data class Default internal constructor(
        override val date: Date,
        override val timeIntervals: List<TimeInterval>
    ) : TimeReportDay() {
        override val isRegistered: Boolean by lazy {
            timeIntervals.all { it is TimeInterval.Registered }
        }

        override val timeSummary: HoursMinutes
            get() = accumulatedHoursMinutes()

        private fun accumulatedHoursMinutes(): HoursMinutes {
            return timeIntervals.map { calculateHoursMinutes(calculateInterval(it)) }
                .accumulated()
        }

        override val timeDifference: HoursMinutes by lazy {
            timeSummary - HoursMinutes(8, 0)
        }
    }
}

fun timeReportDay(date: Date, timeIntervals: List<TimeInterval>): TimeReportDay {
    return TimeReportDay.Default(date, timeIntervals)
}
