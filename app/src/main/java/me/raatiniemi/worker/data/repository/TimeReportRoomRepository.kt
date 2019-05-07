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
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import java.util.*

internal class TimeReportRoomRepository(
    private val timeReport: TimeReportDao,
    private val timeIntervals: TimeIntervalDao
) : TimeReportRepository {
    override fun count(projectId: Long) = timeReport.count(projectId)

    override fun countNotRegistered(projectId: Long) = timeReport.countNotRegistered(projectId)

    private fun transform(group: TimeReportQueryGroup): TimeReportDay {
        val map = group.mapNotNull { timeIntervals.find(it) }
            .map { it.toTimeInterval() }
            .sortedByDescending { it.startInMilliseconds }
            .map { TimeReportItem.with(it) }

        return TimeReportDay(
            Date(group.dateInMilliseconds),
            map
        )
    }

    override fun findAll(projectId: Long, position: Int, pageSize: Int): List<TimeReportDay> {
        return timeReport.findAll(projectId, position, pageSize)
            .map { transform(it) }
    }

    override fun findNotRegistered(
        projectId: Long,
        position: Int,
        pageSize: Int
    ): List<TimeReportDay> {
        return timeReport.findNotRegistered(projectId, position, pageSize)
            .map { transform(it) }
    }
}
