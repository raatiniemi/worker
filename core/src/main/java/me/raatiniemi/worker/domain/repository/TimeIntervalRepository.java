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

package me.raatiniemi.worker.domain.repository;

import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.util.Optional;

/**
 * Represent a unified interface for working with time intervals using different data sources.
 */
public interface TimeIntervalRepository {
    /**
     * Find time intervals for project since starting point, or active time interval.
     *
     * @param project Project for which to use as filter.
     * @param milliseconds Starting point in milliseconds.
     * @return Time intervals, or active time interval, for project since starting point.
     * @throws DomainException If domain rules are violated.
     */
    List<TimeInterval> findAll(Project project, long milliseconds) throws DomainException;

    /**
     * Get time by id.
     *
     * @param id Id for the time.
     * @return Time, or null if none was found.
     * @throws DomainException If domain rules are violated.
     */
    Optional<TimeInterval> findById(long id) throws DomainException;

    /**
     * Get active time for project.
     *
     * @param projectId Id for project.
     * @return Active time for project, or null if project is inactive.
     * @throws DomainException If domain rules are violated.
     */
    Optional<TimeInterval> getActiveTimeIntervalForProject(long projectId) throws DomainException;

    /**
     * Add time.
     *
     * @param timeInterval Time to add.
     * @return Added time.
     * @throws DomainException If domain rules are violated.
     */
    Optional<TimeInterval> add(TimeInterval timeInterval) throws DomainException;

    /**
     * Update time.
     *
     * @param timeInterval Time to update.
     * @return Updated time.
     * @throws DomainException If domain rules are violated.
     */
    Optional<TimeInterval> update(TimeInterval timeInterval) throws DomainException;

    /**
     * Update items.
     *
     * @param timeIntervals Items to update.
     * @return Updated items.
     * @throws DomainException If domain rules are violated.
     */
    List<TimeInterval> update(List<TimeInterval> timeIntervals) throws DomainException;

    /**
     * Remove time by id.
     *
     * @param id Id of the time to remove.
     */
    void remove(long id);

    /**
     * Remove multiple items.
     *
     * @param timeIntervals Items to remove.
     */
    void remove(List<TimeInterval> timeIntervals);
}
