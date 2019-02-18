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
import me.raatiniemi.worker.util.Optional

/**
 * Represent a unified interface for working with projects using different data sources.
 */
interface ProjectRepository {
    fun count(): Int

    fun findAll(position: Int, pageSize: Int): List<Project>

    /**
     * Get projects.
     *
     * @return Projects.
     */
    fun findAll(): List<Project>

    /**
     * Find project by name.
     *
     * @param projectName Project name to search for.
     * @return Project with name, or null.
     */
    fun findByName(projectName: String): Optional<Project>

    /**
     * Get project by id.
     *
     * @param id Id for the project.
     * @return Project, or null if none was found.
     */
    fun findById(id: Long): Optional<Project>

    /**
     * Add new project to the repository.
     *
     * @param newProject Project to add.
     * @return Added project.
     */
    fun add(newProject: NewProject): Optional<Project>

    /**
     * Remove project by id.
     *
     *
     * The operation also removes time registered to the project.
     *
     * @param id Id of the project to remove.
     */
    fun remove(id: Long)
}
