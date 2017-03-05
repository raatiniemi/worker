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

import me.raatiniemi.worker.domain.exception.ActiveProjectException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

import static me.raatiniemi.util.NullUtil.nonNull;

/**
 * Use case for clocking in.
 */
public class ClockIn {
    private final TimeRepository timeRepository;

    public ClockIn(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    /**
     * Clock in the project at the given date.
     *
     * @param projectId Id for the project to clock in.
     * @param date      Date to clock in.
     * @throws ActiveProjectException If project is active.
     */
    public void execute(long projectId, Date date) throws DomainException {
        Time time = timeRepository.getActiveTimeForProject(projectId);
        if (nonNull(time)) {
            throw new ActiveProjectException("Project is active");
        }

        timeRepository.add(
                Time.builder(projectId)
                        .startInMilliseconds(date.getTime())
                        .build()
        );
    }
}
