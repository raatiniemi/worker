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

package me.raatiniemi.worker.domain.project.repository

import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.project.model.ProjectName

/**
 * Represent a unified interface for working with projects using different data sources.
 */
interface ProjectRepository {
    suspend fun count(): Int

    suspend fun findAll(loadRange: LoadRange): List<Project>

    /**
     * Get projects.
     *
     * @return Projects.
     */
    suspend fun findAll(): List<Project>

    /**
     * Find project by name.
     *
     * @param projectName Project name to search for.
     * @return Project with name, or null.
     */
    suspend fun findByName(projectName: ProjectName): Project?

    /**
     * Get project by id.
     *
     * @param id Id for the project.
     * @return Project, or null if none was found.
     */
    suspend fun findById(id: ProjectId): Project?

    /**
     * Add new project to the repository.
     *
     * @param newProject Project to add.
     * @return Added project.
     */
    suspend fun add(newProject: NewProject): Project

    /**
     * Remove project.
     *
     * The operation also removes time registered to the project.
     *
     * @param project Project to remove.
     */
    fun remove(project: Project)
}
