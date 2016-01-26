/*
 * Copyright (C) 2016 Worker Project
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

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

/**
 * Use case for creating a project.
 */
public class CreateProject {
    /**
     * Project repository.
     */
    private final ProjectRepository mRepository;

    /**
     * Constructor.
     *
     * @param repository Project repository.
     */
    public CreateProject(ProjectRepository repository) {
        mRepository = repository;
    }

    /**
     * Create the project.
     *
     * @param project Project to create.
     * @return Created project.
     */
    public Project execute(final Project project) {
        // TODO: Check that the project have a name.
        // TODO: Check if project name already exists.
        return mRepository.add(project);
    }
}
