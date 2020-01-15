/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.timereport.model

import me.raatiniemi.worker.domain.time.HoursMinutes
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.accumulated
import me.raatiniemi.worker.domain.time.calculateHoursMinutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.calculateInterval
import me.raatiniemi.worker.domain.timeinterval.model.isActive

sealed class TimeReportDay {
    abstract val milliseconds: Milliseconds
    abstract val timeIntervals: List<TimeInterval>

    abstract val isRegistered: Boolean

    abstract val timeSummary: HoursMinutes

    abstract val timeDifference: HoursMinutes

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    data class Active internal constructor(
        override val milliseconds: Milliseconds,
        override val timeIntervals: List<TimeInterval>
    ) : TimeReportDay() {
        override val isRegistered: Boolean by lazy {
            timeIntervals.all { it is TimeInterval.Registered }
        }

        override val timeSummary: HoursMinutes
            get() = timeIntervals.map { calculateHoursMinutes(calculateInterval(it)) }
                .accumulated()

        override val timeDifference: HoursMinutes
            get() = timeSummary - HoursMinutes(8, 0)
    }

    data class Inactive internal constructor(
        override val milliseconds: Milliseconds,
        override val timeIntervals: List<TimeInterval>
    ) : TimeReportDay() {
        override val isRegistered: Boolean by lazy {
            timeIntervals.all { it is TimeInterval.Registered }
        }

        override val timeSummary: HoursMinutes by lazy {
            timeIntervals.map { calculateHoursMinutes(calculateInterval(it)) }
                .accumulated()
        }

        override val timeDifference: HoursMinutes by lazy {
            timeSummary - HoursMinutes(8, 0)
        }
    }
}

fun timeReportDay(milliseconds: Milliseconds, timeIntervals: List<TimeInterval>): TimeReportDay {
    val isActive = timeIntervals.any(::isActive)
    if (isActive) {
        return TimeReportDay.Active(milliseconds, timeIntervals)
    }

    return TimeReportDay.Inactive(milliseconds, timeIntervals)
}
