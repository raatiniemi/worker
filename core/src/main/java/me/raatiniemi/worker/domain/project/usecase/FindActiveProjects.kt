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

package me.raatiniemi.worker.domain.project.usecase

import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository

class FindActiveProjects(
    private val projects: ProjectRepository,
    private val timeIntervals: TimeIntervalRepository
) {
    suspend operator fun invoke(): List<Project> {
        return projects.findAll()
            .filter { isActive(it) }
    }

    private suspend fun isActive(project: Project): Boolean {
        return timeIntervals.findActiveByProjectId(project.id) != null
    }
}
