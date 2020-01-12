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
import me.raatiniemi.worker.domain.repository.paginate
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.setToStartOfWeek
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.usecase.groupByWeek

class TimeReportInMemoryRepository(
    private val timeIntervalRepository: TimeIntervalRepository
) : TimeReportRepository {
    override suspend fun countWeeks(project: Project): Int {
        return timeIntervalRepository.findAll(project, Milliseconds(0))
            .groupBy { setToStartOfWeek(it.start) }
            .count()
    }

    override suspend fun countNotRegisteredWeeks(project: Project): Int {
        return timeIntervalRepository.findAll(project, Milliseconds(0))
            .filter { it !is TimeInterval.Registered }
            .groupBy { setToStartOfWeek(it.start) }
            .count()
    }

    override suspend fun findWeeks(project: Project, loadRange: LoadRange): List<TimeReportWeek> {
        val timeIntervals = timeIntervalRepository.findAll(project, Milliseconds.empty)

        return paginate(loadRange, groupByWeek(timeIntervals))
    }

    override suspend fun findNotRegisteredWeeks(
        project: Project,
        loadRange: LoadRange
    ): List<TimeReportWeek> {
        val timeIntervals = timeIntervalRepository.findAll(project, Milliseconds.empty)
            .filter { it !is TimeInterval.Registered }

        return paginate(loadRange, groupByWeek(timeIntervals))
    }
}
