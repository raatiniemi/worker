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
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;

/**
 * Get the registered time for a project since a defined starting point, i.e.
 * {@link TimeIntervalStartingPoint#DAY}, {@link TimeIntervalStartingPoint#WEEK}, or
 * {@link TimeIntervalStartingPoint#MONTH}.
 */
public class GetProjectTimeSince {
    private final TimeIntervalRepository timeIntervalRepository;

    public GetProjectTimeSince(TimeIntervalRepository timeIntervalRepository) {
        this.timeIntervalRepository = timeIntervalRepository;
    }

    /**
     * Get the registered time for a project since the starting point.
     *
     * @param project       Project for which to get the registered time.
     * @param startingPoint Starting point, i.e. {@link TimeIntervalStartingPoint#DAY},
     *                      {@link TimeIntervalStartingPoint#WEEK},
     *                      or {@link TimeIntervalStartingPoint#MONTH}.
     * @return Registered time for the project since the starting point.
     */
    public List<TimeInterval> execute(Project project, TimeIntervalStartingPoint startingPoint) {
        return timeIntervalRepository.findAll(
                project,
                startingPoint.calculateMilliseconds()
        );
    }
}
