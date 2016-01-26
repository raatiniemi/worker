/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.domain.repository;

import java.util.List;

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.query.Criteria;

/**
 * Represent a unified interface for working with projects using different data sources.
 */
public interface ProjectRepository {
    /**
     * Find projects matching a criteria.
     *
     * @param criteria Criteria for matching projects
     * @return Projects matching the criteria.
     */
    List<Project> matching(Criteria criteria);

    /**
     * Get projects.
     *
     * @return Projects.
     */
    List<Project> get();

    /**
     * Get project by id.
     *
     * @param id Id for the project.
     * @return Project, or null if none was found.
     */
    Project get(long id);

    /**
     * Add a new project.
     *
     * @param project Project to add.
     * @return Added project.
     */
    Project add(Project project);

    /**
     * Remove project by id.
     * <p/>
     * The operation also removes time registered to the project.
     *
     * @param id Id of the project to remove.
     */
    void remove(long id);
}
