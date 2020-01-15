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

package me.raatiniemi.worker.domain.timereport.usecase

import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.setToStartOfDay
import me.raatiniemi.worker.domain.time.setToStartOfWeek
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek

/**
 * Group time intervals by the week in which they have been registered.
 *
 * @param timeIntervals Time intervals to group.
 *
 * @return Weeks of time intervals.
 */
fun groupByWeek(timeIntervals: List<TimeInterval>): List<TimeReportWeek> {
    return timeIntervals.groupBy { setToStartOfWeek(it.start) }
        .mapNotNull(week())
        .sortedByDescending { it.start }
}

private fun week(): (Map.Entry<Milliseconds, List<TimeInterval>>) -> TimeReportWeek? {
    return { (_, timeIntervals) ->
        val days = groupByDay(timeIntervals)
        days.minBy { it.milliseconds }
            ?.let { earliestDay ->
                timeReportWeek(earliestDay.milliseconds, days)
            }
    }
}

/**
 * Group time interval by the week in which it have been registered.
 *
 * @param timeInterval Time interval to group.
 *
 * @return Weeks of time interval.
 *
 * @see [groupByWeek]
 */
fun groupByWeek(timeInterval: TimeInterval): List<TimeReportWeek> {
    return groupByWeek(
        listOf(
            timeInterval
        )
    )
}

/**
 * Group time intervals by the day in which they have been registered.
 *
 * @param timeIntervals Time intervals to group.
 *
 * @return Days of time intervals.
 */
fun groupByDay(timeIntervals: List<TimeInterval>): List<TimeReportDay> {
    return timeIntervals.groupBy { setToStartOfDay(it.start) }
        .mapNotNull(day())
        .sortedByDescending { it.milliseconds }
}

private fun day(): (Map.Entry<Milliseconds, List<TimeInterval>>) -> TimeReportDay? {
    return { (_, timeIntervals) ->
        val sortedTimeIntervals = timeIntervals.sortedByDescending { it.start.value }
        sortedTimeIntervals.minBy { it.start.value }
            ?.let { earliestTimeInterval ->
                timeReportDay(earliestTimeInterval.start, sortedTimeIntervals)
            }
    }
}
