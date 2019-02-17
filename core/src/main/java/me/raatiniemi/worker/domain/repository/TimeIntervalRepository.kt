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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.model.NewTimeInterval
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.util.Optional

/**
 * Represent a unified interface for working with time intervals using different data sources.
 */
interface TimeIntervalRepository {
    /**
     * Find time intervals for project since starting point, or active time interval.
     *
     * @param project      Project for which to use as filter.
     * @param milliseconds Starting point in milliseconds.
     * @return Time intervals, or active time interval, for project since starting point.
     */
    fun findAll(project: Project, milliseconds: Long): List<TimeInterval>

    /**
     * Get time by id.
     *
     * @param id Id for the time.
     * @return Time, or null if none was found.
     */
    fun findById(id: Long): Optional<TimeInterval>

    /**
     * Get active time for project.
     *
     * @param projectId Id for project.
     * @return Active time for project, or null if project is inactive.
     */
    fun findActiveByProjectId(projectId: Long): Optional<TimeInterval>

    /**
     * Add new time interval to the repository.
     *
     * @param newTimeInterval New time interval to add.
     * @return Added time interval.
     */
    fun add(newTimeInterval: NewTimeInterval): Optional<TimeInterval>

    /**
     * Update time.
     *
     * @param timeInterval Time to update.
     * @return Updated time.
     */
    fun update(timeInterval: TimeInterval): Optional<TimeInterval>

    /**
     * Update items.
     *
     * @param timeIntervals Items to update.
     * @return Updated items.
     */
    fun update(timeIntervals: List<TimeInterval>): List<TimeInterval>

    /**
     * Remove time by id.
     *
     * @param id Id of the time to remove.
     */
    fun remove(id: Long)

    /**
     * Remove multiple items.
     *
     * @param timeIntervals Items to remove.
     */
    fun remove(timeIntervals: List<TimeInterval>)
}
