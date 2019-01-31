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

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportGroup
import me.raatiniemi.worker.domain.model.TimeReportItem
import java.util.*

class TimeReportInMemoryRepository(private val timeIntervals: List<TimeInterval>) : TimeReportRepository {
    override fun count(projectId: Long) = timeIntervals
            .filter { it.projectId == projectId }
            .groupBy { resetToStartOfDay(it.startInMilliseconds) }
            .count()

    override fun countNotRegistered(projectId: Long) = timeIntervals
            .filter { it.projectId == projectId && !it.isRegistered }
            .groupBy { resetToStartOfDay(it.startInMilliseconds) }
            .count()

    private fun resetToStartOfDay(timeInMilliseconds: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliseconds
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    // TODO: Implement proper support for pagination.
    private fun filterAndBuildResult(predicate: (TimeInterval) -> Boolean): List<TimeReportGroup> {
        return timeIntervals.filter { predicate(it) }
                .groupBy { resetToStartOfDay(it.startInMilliseconds) }
                .map { entry ->
                    TimeReportGroup(
                            entry.key,
                            entry.value.map { TimeReportItem(it) }
                    )
                }
                .sortedByDescending { it.date }
    }

    override fun findAll(projectId: Long, position: Int, pageSize: Int) =
            filterAndBuildResult { it.projectId == projectId }

    override fun findNotRegistered(projectId: Long, position: Int, pageSize: Int) =
            filterAndBuildResult { it.projectId == projectId && !it.isRegistered }
}
