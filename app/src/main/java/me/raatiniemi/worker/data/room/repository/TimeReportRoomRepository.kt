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

package me.raatiniemi.worker.data.room.repository

import me.raatiniemi.worker.data.room.entity.timeinterval.TimeIntervalDao
import me.raatiniemi.worker.data.room.entity.timereport.TimeReportDao
import me.raatiniemi.worker.data.room.entity.timereport.TimeReportQueryGroup
import me.raatiniemi.worker.data.room.entity.timeinterval.timeInterval
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import me.raatiniemi.worker.domain.timereport.usecase.groupByWeek

internal class TimeReportRoomRepository(
    private val timeReport: TimeReportDao,
    private val timeIntervals: TimeIntervalDao
) : TimeReportRepository {
    override suspend fun countWeeks(project: Project): Int {
        return timeReport.countWeeks(project.id.value)
    }

    override suspend fun countNotRegisteredWeeks(project: Project): Int {
        return timeReport.countNotRegisteredWeeks(project.id.value)
    }

    override suspend fun findWeeks(project: Project, loadRange: LoadRange): List<TimeReportWeek> {
        val (position, size) = loadRange

        val groups = timeReport.findWeeks(project.id.value, position.value, size.value)
        val timeIntervals = timeIntervals(groups)
        return groupByWeek(timeIntervals)
    }

    override suspend fun findNotRegisteredWeeks(
        project: Project,
        loadRange: LoadRange
    ): List<TimeReportWeek> {
        val (position, size) = loadRange

        val groups = timeReport.findNotRegisteredWeeks(project.id.value, position.value, size.value)
        val timeIntervals = timeIntervals(groups)
        return groupByWeek(timeIntervals)
    }

    private suspend fun timeIntervals(entities: List<TimeReportQueryGroup>): List<TimeInterval> {
        return entities.flatMap { group ->
            group.mapNotNull { timeIntervals.find(it) }
                .map(::timeInterval)
        }
    }
}
