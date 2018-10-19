/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.domain.interactor;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.NoProjectException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.util.Optional;

/**
 * Use case for getting a project.
 */
public class GetProject {
    private final ProjectRepository projectRepository;

    public GetProject(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Get project.
     *
     * @param projectId Id of the project to get.
     * @return Project with id, or null if not found.
     * @throws DomainException If domain rules are violated.
     */
    public Project execute(long projectId) throws DomainException {
        Optional<Project> value = projectRepository.findById(projectId);
        if (value.isPresent()) {
            return value.get();
        }

        throw new NoProjectException();
    }
}
