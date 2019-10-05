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

package me.raatiniemi.worker.domain.timereport.repository

import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.repository.resetToStartOfDay
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.setToStartOfWeek
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import java.util.*

class TimeReportInMemoryRepository(
    private val timeIntervalRepository: TimeIntervalRepository
) : TimeReportRepository {
    override fun countWeeks(project: Project): Int {
        return timeIntervalRepository.findAll(project, Milliseconds(0))
            .groupBy { setToStartOfWeek(it.start) }
            .count()
    }

    override fun countNotRegisteredWeeks(project: Project): Int {
        return timeIntervalRepository.findAll(project, Milliseconds(0))
            .filter { it !is TimeInterval.Registered }
            .groupBy { setToStartOfWeek(it.start) }
            .count()
    }

    override fun count(project: Project): Int =
        timeIntervalRepository.findAll(project, Milliseconds(0))
            .groupBy { resetToStartOfDay(it.start) }
            .count()

    override fun countNotRegistered(project: Project): Int =
        timeIntervalRepository.findAll(project, Milliseconds(0))
            .filter { it !is TimeInterval.Registered }
            .groupBy { resetToStartOfDay(it.start) }
            .count()

    override fun findAll(project: Project, loadRange: LoadRange): List<TimeReportDay> {
        val timeIntervals = timeIntervalRepository.findAll(project, Milliseconds(0))

        return with(loadRange) {
            groupByDay(timeIntervals)
                .map(::buildTimeReportDay)
                .sortedByDescending { it.date }
                .drop(position.value)
                .take(size.value)
        }
    }

    override fun findNotRegistered(project: Project, loadRange: LoadRange): List<TimeReportDay> {
        val timeIntervals = timeIntervalRepository.findAll(project, Milliseconds(0))
            .filter { it !is TimeInterval.Registered }

        return with(loadRange) {
            groupByDay(timeIntervals)
                .map(::buildTimeReportDay)
                .sortedByDescending { it.date }
                .drop(position.value)
                .take(size.value)
        }
    }

    private fun groupByDay(timeIntervals: List<TimeInterval>): Map<Date, List<TimeInterval>> {
        return timeIntervals.groupBy {
            resetToStartOfDay(it.start)
        }
    }

    private fun buildTimeReportDay(entry: Map.Entry<Date, List<TimeInterval>>) =
        entry.let { (date, timeIntervals) ->
            timeReportDay(date, timeIntervals.sortedByDescending { it.start.value })
        }
}
