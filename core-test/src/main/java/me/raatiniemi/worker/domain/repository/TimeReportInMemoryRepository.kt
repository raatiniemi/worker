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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.model.*
import java.util.*

class TimeReportInMemoryRepository(private val timeIntervalRepository: TimeIntervalRepository) :
    TimeReportRepository {
    override fun count(project: Project): Int = timeIntervalRepository.findAll(project, 0)
        .groupBy { resetToStartOfDay(it.startInMilliseconds) }
        .count()

    override fun countNotRegistered(project: Project): Int =
        timeIntervalRepository.findAll(project, 0)
            .filter { !it.isRegistered }
            .groupBy { resetToStartOfDay(it.startInMilliseconds) }
            .count()

    override fun findAll(project: Project, loadRange: LoadRange): List<TimeReportDay> {
        val timeIntervals = timeIntervalRepository.findAll(project, 0)

        return with(loadRange) {
            groupByDay(timeIntervals)
                .map(::buildTimeReportDay)
                .sortedByDescending { it.date }
                .drop(position.value)
                .take(size.value)
        }
    }

    override fun findNotRegistered(project: Project, loadRange: LoadRange): List<TimeReportDay> {
        val timeIntervals = timeIntervalRepository.findAll(project, 0)
            .filter { !it.isRegistered }

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
            resetToStartOfDay(it.startInMilliseconds)
        }
    }

    private fun buildTimeReportDay(entry: Map.Entry<Date, List<TimeInterval>>) =
        entry.let { (date, timeIntervals) ->
            val timeReportItems = timeIntervals
                .sortedByDescending { it.startInMilliseconds }
                .map { TimeReportItem(it) }

            TimeReportDay(date, timeReportItems)
        }
}
