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

package me.raatiniemi.worker.domain.usecase

import me.raatiniemi.worker.domain.exception.ActiveProjectException
import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.model.NewTimeInterval
import me.raatiniemi.worker.domain.model.ProjectId
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import java.util.*

/**
 * Use case for clocking in.
 */
class ClockIn(private val timeIntervalRepository: TimeIntervalRepository) {
    operator fun invoke(projectId: Long, date: Date) {
        val timeInterval = timeIntervalRepository.findActiveByProjectId(ProjectId(projectId))
        if (timeInterval != null) {
            throw ActiveProjectException()
        }

        val newTimeInterval = NewTimeInterval(
            projectId = ProjectId(projectId),
            start = Milliseconds(date.time)
        )

        timeIntervalRepository.add(newTimeInterval)
    }
}
