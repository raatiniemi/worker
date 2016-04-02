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

import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

/**
 * Use case for clocking out.
 */
public class ClockOut {
    private final TimeRepository mTimeRepository;

    public ClockOut(TimeRepository timeRepository) {
        mTimeRepository = timeRepository;
    }

    /**
     * Clock out the project at the given date.
     *
     * @param projectId Id for the project to clock out.
     * @param date      Date to clock out.
     * @throws DomainException If domain rules are violated.
     */
    public void execute(long projectId, Date date)
            throws DomainException {
        Time time = mTimeRepository.getActiveTimeForProject(projectId);
        if (null == time) {
            throw new ClockActivityException("Project is not active");
        }

        mTimeRepository.update(
                time.clockOutAt(date)
        );
    }
}
