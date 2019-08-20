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
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.timeReportDay
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import java.util.*

internal class TimeReportRoomRepository(
    private val timeReport: TimeReportDao,
    private val timeIntervals: TimeIntervalDao
) : TimeReportRepository {
    override fun count(project: Project): Int = timeReport.count(project.id.value)

    override fun countNotRegistered(project: Project): Int =
        timeReport.countNotRegistered(project.id.value)

    private fun transform(group: TimeReportQueryGroup): TimeReportDay {
        val timeIntervals = group.mapNotNull { timeIntervals.find(it) }
            .map { it.toTimeInterval() }
            .sortedByDescending { it.start.value }

        return timeReportDay(
            Date(group.dateInMilliseconds),
            timeIntervals
        )
    }

    override fun findAll(project: Project, loadRange: LoadRange): List<TimeReportDay> =
        with(loadRange) {
            return timeReport.findAll(project.id.value, position.value, size.value)
                .map(::transform)
        }

    override fun findNotRegistered(project: Project, loadRange: LoadRange): List<TimeReportDay> =
        with(loadRange) {
            return timeReport.findNotRegistered(project.id.value, position.value, size.value)
                .map(::transform)
        }
}
