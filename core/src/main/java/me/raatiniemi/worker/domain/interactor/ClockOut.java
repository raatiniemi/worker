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

import java.util.Date;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InactiveProjectException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.util.Optional;

/**
 * Use case for clocking out.
 */
public class ClockOut {
    private final TimeRepository timeRepository;

    public ClockOut(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    /**
     * Clock out the project at the given date.
     *
     * @param projectId Id for the project to clock out.
     * @param date      Date to clock out.
     * @throws InactiveProjectException If project is inactive.
     */
    public void execute(long projectId, Date date) throws DomainException {
        Optional<Time> value = timeRepository.getActiveTimeForProject(projectId);
        if (!value.isPresent()) {
            throw new InactiveProjectException();
        }

        Time time = value.get();
        timeRepository.update(
                time.clockOutAt(date)
        );
    }
}
