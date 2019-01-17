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

import me.raatiniemi.worker.data.projects.ProjectDao
import me.raatiniemi.worker.data.projects.toEntity
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectRepository
import me.raatiniemi.worker.util.Optional

internal class ProjectRoomRepository(val projects: ProjectDao) : ProjectRepository {
    override fun count() = projects.count()

    override fun findAll(position: Int, pageSize: Int): List<Project> {
        return projects.findAll(position, pageSize)
                .map { it.toProject() }
    }

    override fun findAll(): List<Project> {
        return projects.findAll()
                .map { it.toProject() }
                .toMutableList()
    }

    override fun findByName(projectName: String): Optional<Project> {
        val entity = projects.findByName(projectName) ?: return Optional.empty()

        return Optional.of(entity.toProject())
    }

    override fun findById(id: Long): Optional<Project> {
        val entity = projects.findById(id) ?: return Optional.empty()

        return Optional.of(entity.toProject())
    }

    override fun add(project: Project): Optional<Project> {
        projects.add(project.toEntity())

        return findByName(projectName = project.name)
    }

    override fun remove(id: Long) {
        val entity = projects.findById(id) ?: return

        projects.remove(entity)
    }
}
