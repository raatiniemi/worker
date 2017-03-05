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

import java.util.Date;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;

/**
 * Use case for project clock in/out.
 */
public class ClockActivityChange {
    /**
     * Project repository.
     */
    private final ProjectRepository projectRepository;

    /**
     * Time repository.
     */
    private final TimeRepository timeRepository;
    private final ClockIn clockIn;
    private final ClockOut clockOut;

    /**
     * Constructor.
     *
     * @param projectRepository Project repository.
     * @param timeRepository    Time repository.
     * @param clockIn           Use case for clocking in projects.
     * @param clockOut          Use case for clocking out projects.
     */
    public ClockActivityChange(
            ProjectRepository projectRepository,
            TimeRepository timeRepository,
            ClockIn clockIn,
            ClockOut clockOut
    ) {
        this.projectRepository = projectRepository;
        this.timeRepository = timeRepository;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    /**
     * Clock in/out project at given date.
     *
     * @param project Project to clock in/out.
     * @param date    Date to clock in/out.
     * @return Clocked in/out project.
     * @throws DomainException If domain rules are violated.
     */
    public Project execute(Project project, final Date date)
            throws DomainException {
        if (!project.isActive()) {
            clockIn.execute(project.getId(), date);
        } else {
            clockOut.execute(project.getId(), date);
        }

        // Reload the project and populate it with the registered time.
        // TODO: Migrate populate time to separate use case?
        project = projectRepository.get(project.getId());
        project.addTime(
                timeRepository.getProjectTimeSinceBeginningOfMonth(
                        project.getId()
                )
        );
        return project;
    }
}
