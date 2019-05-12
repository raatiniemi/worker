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

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem

class TimeReportInMemoryRepository(private val timeIntervals: List<TimeInterval>) :
    TimeReportRepository {
    override fun count(project: Project): Int = timeIntervals
        .filter { it.projectId == project.id }
        .groupBy { resetToStartOfDay(it.startInMilliseconds) }
        .count()

    override fun countNotRegistered(project: Project): Int = timeIntervals
        .filter { it.projectId == project.id && !it.isRegistered }
        .groupBy { resetToStartOfDay(it.startInMilliseconds) }
        .count()

    // TODO: Implement proper support for pagination.
    private fun filterAndBuildResult(predicate: (TimeInterval) -> Boolean): List<TimeReportDay> {
        return timeIntervals.filter { predicate(it) }
            .groupBy { resetToStartOfDay(it.startInMilliseconds) }
            .map { entry ->
                TimeReportDay(
                    entry.key,
                    entry.value.sortedByDescending { it.startInMilliseconds }
                        .map { TimeReportItem(it) }
                )
            }
            .sortedByDescending { it.date }
    }

    override fun findAll(project: Project, position: Int, pageSize: Int): List<TimeReportDay> =
        filterAndBuildResult { it.projectId == project.id }

    override fun findNotRegistered(
        project: Project,
        position: Int,
        pageSize: Int
    ): List<TimeReportDay> =
        filterAndBuildResult { it.projectId == project.id && !it.isRegistered }
}
