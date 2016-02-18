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
    private final ProjectRepository mProjectRepository;

    /**
     * Time repository.
     */
    private final TimeRepository mTimeRepository;

    /**
     * Constructor.
     *
     * @param projectRepository Project repository.
     * @param timeRepository    Time repository.
     */
    public ClockActivityChange(
            ProjectRepository projectRepository,
            TimeRepository timeRepository
    ) {
        mProjectRepository = projectRepository;
        mTimeRepository = timeRepository;
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
        // TODO: Migrate clock in and clock out to separate use cases.
        // A lot more logic for clock out is needed to e.g. handle clock in and
        // clock out on different days.
        //
        // Depending on whether the project is active we have
        // to clock in or clock out at the given date.
        if (!project.isActive()) {
            mTimeRepository.add(project.clockInAt(date));
        } else {
            mTimeRepository.update(project.clockOutAt(date));
        }

        // Reload the project and populate it with the registered time.
        // TODO: Migrate populate time to separate use case?
        project = mProjectRepository.get(project.getId());
        project.addTime(
                mTimeRepository.getProjectTimeSinceBeginningOfMonth(
                        project.getId()
                )
        );
        return project;
    }
}
