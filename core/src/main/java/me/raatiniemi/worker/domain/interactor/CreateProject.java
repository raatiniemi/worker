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

package me.raatiniemi.worker.domain.interactor;

import me.raatiniemi.worker.domain.exception.NoProjectException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.util.Optional;

/**
 * Use case for creating a project.
 */
public class CreateProject {
    /**
     * Project repository.
     */
    private final ProjectRepository repository;

    /**
     * Constructor.
     *
     * @param repository Project repository.
     */
    public CreateProject(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * Create the project.
     *
     * @param project Project to create.
     * @return Created project.
     */
    public Project execute(final Project project) {
        if (isProjectNameInUse(project.getName())) {
            throw new ProjectAlreadyExistsException(
                    "Project '" + project.getName() + "' already exists"
            );
        }

        Optional<Project> value = repository.add(project);
        if (value.isPresent()) {
            return value.get();
        }

        throw new NoProjectException();
    }

    private boolean isProjectNameInUse(String projectName) {
        Optional<Project> value = repository.findByName(projectName);

        return value.isPresent();
    }
}
