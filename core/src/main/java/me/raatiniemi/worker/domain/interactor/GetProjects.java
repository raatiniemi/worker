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

import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;

/**
 * Use case for getting projects.
 */
public class GetProjects {
    /**
     * Project repository.
     */
    private final ProjectRepository projectRepository;

    /**
     * Time repository.
     */
    private final TimeRepository timeRepository;

    /**
     * Constructor.
     *
     * @param projectRepository Project repository.
     * @param timeRepository    Time repository.
     */
    public GetProjects(
            ProjectRepository projectRepository,
            TimeRepository timeRepository
    ) {
        this.projectRepository = projectRepository;
        this.timeRepository = timeRepository;
    }

    /**
     * Get the projects.
     *
     * @return Projects.
     * @throws DomainException If domain rules are violated.
     */
    public List<Project> execute() throws DomainException {
        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            // Populate the project with the registered time.
            project.addTime(
                    timeRepository.getProjectTimeSinceBeginningOfMonth(project.getId())
            );
        }

        return projects;
    }
}
