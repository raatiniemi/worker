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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.exception.NoProjectException
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectRepository

/**
 * Use case for creating a project.
 */
class CreateProject(val findProject: FindProject, private val repository: ProjectRepository) {
    operator fun invoke(projectName: String): Project {
        if (isProjectNameInUse(projectName)) {
            throw ProjectAlreadyExistsException("Project '${projectName}' already exists")
        }

        val project = Project.from(projectName)
        val value = repository.add(project)
        if (value.isPresent) {
            return value.get()
        }

        throw NoProjectException()
    }

    private fun isProjectNameInUse(projectName: String): Boolean {
        return null != findProject(projectName)
    }
}