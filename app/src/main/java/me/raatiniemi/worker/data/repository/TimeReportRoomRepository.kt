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

package me.raatiniemi.worker.data.repository

import me.raatiniemi.worker.data.projects.TimeIntervalDao
import me.raatiniemi.worker.data.projects.TimeReportDao
import me.raatiniemi.worker.data.projects.TimeReportQueryGroup
import me.raatiniemi.worker.data.projects.timeInterval
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import me.raatiniemi.worker.domain.timereport.usecase.groupByWeek
import java.util.*

internal class TimeReportRoomRepository(
    private val timeReport: TimeReportDao,
    private val timeIntervals: TimeIntervalDao
) : TimeReportRepository {
    override fun countWeeks(project: Project): Int {
        return timeReport.countWeeks(project.id.value)
    }

    override fun countNotRegisteredWeeks(project: Project): Int {
        return timeReport.countNotRegisteredWeeks(project.id.value)
    }

    override fun count(project: Project): Int = timeReport.count(project.id.value)

    override fun countNotRegistered(project: Project): Int =
        timeReport.countNotRegistered(project.id.value)

    private fun transform(group: TimeReportQueryGroup): TimeReportDay {
        val timeIntervals = group.mapNotNull { timeIntervals.find(it) }
            .map(::timeInterval)
            .sortedByDescending { it.start.value }

        return timeReportDay(
            Date(group.dateInMilliseconds),
            timeIntervals
        )
    }

    override fun findWeeks(project: Project, loadRange: LoadRange): List<TimeReportWeek> {
        val (position, size) = loadRange

        val groups = timeReport.findWeeks(project.id.value, position.value, size.value)
        val timeIntervals = timeIntervals(groups)
        return groupByWeek(timeIntervals)
    }

    override fun findNotRegisteredWeeks(
        project: Project,
        loadRange: LoadRange
    ): List<TimeReportWeek> {
        val (position, size) = loadRange

        val groups = timeReport.findNotRegisteredWeeks(project.id.value, position.value, size.value)
        val timeIntervals = timeIntervals(groups)
        return groupByWeek(timeIntervals)
    }

    private fun timeIntervals(entities: List<TimeReportQueryGroup>): List<TimeInterval> {
        return entities.flatMap { group ->
            group.mapNotNull { timeIntervals.find(it) }
                .map(::timeInterval)
        }
    }

    override fun findAll(project: Project, loadRange: LoadRange): List<TimeReportDay> {
        val (position, size) = loadRange

        return timeReport.findAll(project.id.value, position.value, size.value)
            .map(::transform)
    }

    override fun findNotRegistered(project: Project, loadRange: LoadRange): List<TimeReportDay> {
        val (position, size) = loadRange

        return timeReport.findNotRegistered(project.id.value, position.value, size.value)
            .map(::transform)
    }
}
