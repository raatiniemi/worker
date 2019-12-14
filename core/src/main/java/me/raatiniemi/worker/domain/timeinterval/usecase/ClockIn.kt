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

package me.raatiniemi.worker.domain.timeinterval.usecase

import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.NewTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository

/**
 * Use case for clocking in.
 */
class ClockIn(private val timeIntervals: TimeIntervalRepository) {
    suspend operator fun invoke(project: Project, milliseconds: Milliseconds): TimeInterval.Active {
        if (isActive(project.id)) {
            throw ActiveProjectException()
        }

        val newTimeInterval = NewTimeInterval(
            projectId = project.id,
            start = milliseconds
        )

        return timeIntervals.add(newTimeInterval)
    }

    private fun isActive(projectId: ProjectId): Boolean {
        val timeInterval = timeIntervals.findActiveByProjectId(projectId)

        return timeInterval != null
    }
}
