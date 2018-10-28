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

import java.util.Calendar;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;

/**
 * Use case for getting projects.
 */
public class GetProjects {
    /**
     * Project repository.
     */
    private final ProjectRepository projectRepository;

    /**
     * Time interval repository.
     */
    private final TimeIntervalRepository timeIntervalRepository;

    /**
     * Constructor.
     *
     * @param projectRepository Project repository.
     * @param timeIntervalRepository    Time interval repository.
     */
    public GetProjects(
            ProjectRepository projectRepository,
            TimeIntervalRepository timeIntervalRepository
    ) {
        this.projectRepository = projectRepository;
        this.timeIntervalRepository = timeIntervalRepository;
    }

    /**
     * Get the projects.
     *
     * @return Projects.
     * @throws DomainException If domain rules are violated.
     */
    public List<Project> execute() throws DomainException {
        List<Project> projects = projectRepository.findAll();

        // Reset the calendar to retrieve timestamp of the beginning of the month.
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        for (Project project : projects) {
            // Populate the project with the registered time.
            project.addTime(
                    timeIntervalRepository.findAll(
                            project,
                            calendar.getTimeInMillis()
                    )
            );
        }

        return projects;
    }
}
