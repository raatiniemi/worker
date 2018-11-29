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

import java.util.Date;

import me.raatiniemi.worker.domain.exception.ActiveProjectException;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.util.Optional;

/**
 * Use case for clocking in.
 */
public class ClockIn {
    private final TimeIntervalRepository timeIntervalRepository;

    public ClockIn(TimeIntervalRepository timeIntervalRepository) {
        this.timeIntervalRepository = timeIntervalRepository;
    }

    /**
     * Clock in the project at the given date.
     *
     * @param projectId Id for the project to clock in.
     * @param date      Date to clock in.
     */
    public void execute(long projectId, Date date) {
        Optional<TimeInterval> value = timeIntervalRepository.findActiveByProjectId(projectId);
        if (value.isPresent()) {
            throw new ActiveProjectException();
        }

        timeIntervalRepository.add(
                TimeInterval.builder(projectId)
                        .startInMilliseconds(date.getTime())
                        .build()
        );
    }
}
