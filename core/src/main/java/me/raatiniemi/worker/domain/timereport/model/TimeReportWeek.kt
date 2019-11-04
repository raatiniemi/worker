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

package me.raatiniemi.worker.domain.timereport.model

import me.raatiniemi.worker.domain.time.HoursMinutes
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.accumulated

data class TimeReportWeek internal constructor(
    val start: Milliseconds,
    val days: List<TimeReportDay>
)

/**
 * Calculates time summary for week.
 *
 * @param week Week for which to calculate the time summary.
 *
 * @return Calculated time summary for the week.
 */
fun timeSummary(week: TimeReportWeek): HoursMinutes {
    return week.days
        .map { it.timeSummary }
        .accumulated()
}

fun timeReportWeek(start: Milliseconds, days: List<TimeReportDay>): TimeReportWeek {
    return TimeReportWeek(start, days)
}
