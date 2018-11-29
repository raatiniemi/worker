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

import java.util.List;

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

/**
 * Use case for getting projects.
 */
public class GetProjects {
    /**
     * Project repository.
     */
    private final ProjectRepository projectRepository;

    /**
     * Constructor.
     *
     * @param projectRepository Project repository.
     */
    public GetProjects(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Get the projects.
     *
     * @return Projects.
     */
    public List<Project> execute() {
        return projectRepository.findAll();
    }
}
