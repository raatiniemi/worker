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

import me.raatiniemi.worker.data.room.entity.project.ProjectDao
import me.raatiniemi.worker.data.room.entity.project.ProjectEntity
import me.raatiniemi.worker.data.room.entity.project.project
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.project.model.ProjectName
import me.raatiniemi.worker.domain.project.repository.ProjectRepository

internal class ProjectRoomRepository(val projects: ProjectDao) : ProjectRepository {
    override suspend fun count(): Int {
        return projects.count()
    }

    override suspend fun findAll(loadRange: LoadRange): List<Project> {
        val (position, size) = loadRange
        return projects.findAll(position.value, size.value)
            .map(::project)
    }

    override suspend fun findAll(): List<Project> {
        return projects.findAll()
            .map(::project)
            .toMutableList()
    }

    override suspend fun findByName(projectName: ProjectName): Project? {
        return projects.findByName(projectName.value)
            ?.let(::project)
    }

    override suspend fun findById(id: ProjectId): Project? {
        return projects.findById(id.value)
            ?.let(::project)
    }

    override suspend fun add(newProject: NewProject): Project {
        projects.add(ProjectEntity(name = newProject.name.value))

        return findByName(newProject.name) ?: throw UnableToFindNewProjectException()
    }

    override suspend fun remove(project: Project) {
        val entity = projects.findById(project.id.value) ?: return

        projects.remove(entity)
    }
}
