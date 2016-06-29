/*
 * Copyright (C) 2015-2016 Worker Project
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.query.Criteria;

/**
 * Represent a unified interface for working with time intervals using different data sources.
 */
public interface TimeRepository {
    /**
     * Get time by id.
     *
     * @param id Id for the time.
     * @return Time, or null if none was found.
     * @throws DomainException If domain rules are violated.
     */
    Time get(long id) throws DomainException;

    /**
     * Add time.
     *
     * @param time Time to add.
     * @return Added time.
     * @throws DomainException If domain rules are violated.
     */
    Time add(Time time) throws DomainException;

    /**
     * Update time.
     *
     * @param time Time to update.
     * @return Updated time.
     * @throws DomainException If domain rules are violated.
     */
    Time update(Time time) throws DomainException;

    /**
     * Update items.
     *
     * @param times Items to update.
     * @return Updated items.
     * @throws DomainException If domain rules are violated.
     */
    List<Time> update(List<Time> times) throws DomainException;

    /**
     * Remove time by id.
     *
     * @param id Id of the time to remove.
     */
    void remove(long id);

    /**
     * Remove multiple items.
     *
     * @param times Items to remove.
     */
    void remove(List<Time> times);

    /**
     * Get the time registered for a project since the beginning of the current month.
     *
     * @param projectId Id for the project.
     * @return Registered time for project.
     * @throws DomainException If domain rules are violated.
     * TODO: Replace with query/criteria method.
     */
    List<Time> getProjectTimeSinceBeginningOfMonth(long projectId) throws DomainException;

    /**
     * Get timesheet segment for a project.
     * <p>
     * TODO: Move to separate repository?
     *
     * @param projectId Id for the project.
     * @param offset    Offset for segment.
     * @param criteria  Criteria for matching timesheet items.
     * @return Project timesheet segment.
     */
    Map<Date, List<Time>> getTimesheet(
            final long projectId,
            final int offset,
            final Criteria criteria
    );

    /**
     * Get active time for project.
     *
     * @param projectId Id for project.
     * @return Active time for project, or null if project is inactive.
     * @throws DomainException If domain rules are violated.
     * TODO: Replace with query/criteria method.
     */
    Time getActiveTimeForProject(long projectId) throws DomainException;
}
