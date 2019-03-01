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

import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import java.util.concurrent.atomic.AtomicLong

class ProjectInMemoryRepository : ProjectRepository {
    private val incrementedId = AtomicLong()
    private val projects = mutableSetOf<Project>()

    override fun count() = projects.count()

    override fun findAll(position: Int, pageSize: Int): List<Project> {
        val fromIndex = indexWithCountCap(position, count())
        val toIndex = indexWithCountCap(position + pageSize, count())

        return projects.sortedBy { it.name }
                .subList(fromIndex, toIndex)
    }

    override fun findAll(): List<Project> = projects.sortedBy { it.name }

    override fun findByName(projectName: String): Project? {
        return projects.firstOrNull { it.name.equals(projectName, true) }
    }

    override fun findById(id: Long): Project? {
        return projects.firstOrNull { it.id == id }
    }

    override fun add(newProject: NewProject): Project {
        val project = Project(
                id = incrementedId.incrementAndGet(),
                name = newProject.name
        )
        projects.add(project)

        return project
    }

    override fun remove(id: Long) {
        projects.removeIf { it.id == id }
    }
}
