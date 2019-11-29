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

package me.raatiniemi.worker.domain.project.repository

import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.project.model.ProjectName
import me.raatiniemi.worker.domain.repository.paginate
import java.util.concurrent.atomic.AtomicLong

class ProjectInMemoryRepository : ProjectRepository {
    private val incrementedId = AtomicLong()
    private val projects = mutableSetOf<Project>()

    override fun count() = projects.count()

    override fun findAll(loadRange: LoadRange): List<Project> {
        return paginate(loadRange, projects.sortedBy { it.name.value })
    }

    override fun findAll(): List<Project> = projects.sortedBy { it.name.value }

    override suspend fun findByName(projectName: ProjectName): Project? {
        return projects.firstOrNull { it.name.value.equals(projectName.value, true) }
    }

    override fun findById(id: ProjectId): Project? {
        return projects.firstOrNull { it.id == id }
    }

    override suspend fun add(newProject: NewProject): Project {
        val project = Project(
            id = ProjectId(incrementedId.incrementAndGet()),
            name = newProject.name
        )
        projects.add(project)

        return project
    }

    override fun remove(project: Project) {
        projects.removeIf { it.id == project.id }
    }
}
