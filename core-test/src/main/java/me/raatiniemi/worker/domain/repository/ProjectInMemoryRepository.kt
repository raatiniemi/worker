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

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.util.Optional
import java.util.concurrent.atomic.AtomicLong

class ProjectInMemoryRepository : ProjectRepository {
    private val incrementedId = AtomicLong()
    private val projects = mutableSetOf<Project>()

    override fun findAll(): List<Project> = projects.sortedBy { it.id }

    override fun findByName(projectName: String): Optional<Project> {
        val project = projects.firstOrNull { it.name == projectName }

        return Optional.ofNullable(project)
    }

    override fun findById(id: Long): Optional<Project> {
        val project = projects.firstOrNull { it.id == id }

        return Optional.ofNullable(project)
    }

    override fun add(project: Project): Optional<Project> {
        val id = incrementedId.incrementAndGet()
        projects.add(project.copy(id = id))

        return findById(id)
    }

    override fun remove(id: Long) {
        projects.removeIf { it.id == id }
    }
}
