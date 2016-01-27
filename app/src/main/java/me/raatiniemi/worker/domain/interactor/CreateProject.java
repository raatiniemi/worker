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

import java.util.List;

import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

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
    public Project execute(final Project project) throws ProjectAlreadyExistsException {
        // TODO: Check that the project have a name.

        // TODO: Refactor to remove dependency on the data-package for column name.
        Criteria criteria = Criteria.equalTo(ProjectColumns.NAME, project.getName());
        List<Project> projects = mRepository.matching(criteria);
        if (!projects.isEmpty()) {
            throw new ProjectAlreadyExistsException(
                    "Project '" + project.getName() + "' already exists"
            );
        }

        return mRepository.add(project);
    }
}
