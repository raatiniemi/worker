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
import java.util.Date;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.NoProjectException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.util.Optional;

/**
 * Use case for project clock in/out.
 */
public class ClockActivityChange {
    /**
     * Project repository.
     */
    private final ProjectRepository projectRepository;

    /**
     * Time interval repository.
     */
    private final TimeIntervalRepository timeIntervalRepository;
    private final ClockIn clockIn;
    private final ClockOut clockOut;

    /**
     * Constructor.
     *
     * @param projectRepository Project repository.
     * @param timeIntervalRepository Time interval repository.
     * @param clockIn           Use case for clocking in projects.
     * @param clockOut          Use case for clocking out projects.
     */
    public ClockActivityChange(
            ProjectRepository projectRepository,
            TimeIntervalRepository timeIntervalRepository,
            ClockIn clockIn,
            ClockOut clockOut
    ) {
        this.projectRepository = projectRepository;
        this.timeIntervalRepository = timeIntervalRepository;
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
    public Project execute(final Project project, final Date date) throws DomainException {
        executeClockActivityChange(project, date);

        return reloadProjectWithRegisteredTime(project);
    }

    private void executeClockActivityChange(Project project, Date date) throws DomainException {
        if (project.isActive()) {
            clockOut.execute(project.getId(), date);
            return;
        }

        clockIn.execute(project.getId(), date);
    }

    private Project reloadProjectWithRegisteredTime(Project project) throws DomainException {
        Optional<Project> value = projectRepository.findById(project.getId());
        if (value.isPresent()) {
            return populateProjectWithRegisteredTime(value.get());
        }

        throw new NoProjectException();
    }

    private Project populateProjectWithRegisteredTime(Project project) throws DomainException {
        // Reset the calendar to retrieve timestamp of the beginning of the month.
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Reload the project and populate it with the registered time.
        // TODO: Migrate populate time to separate use case?
        project.addTime(
                timeIntervalRepository.findAll(
                        project,
                        calendar.getTimeInMillis()
                )
        );

        return project;
    }
}
