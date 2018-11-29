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

import me.raatiniemi.worker.domain.model.TimesheetItem
import java.util.*

interface TimesheetRepository {
    /**
     * Get timesheet segment for a project.
     *
     * @param projectId   Id for the project.
     * @param pageRequest Defines the page segment.
     * @return Project timesheet segment.
     */
    fun getTimesheet(projectId: Long, pageRequest: PageRequest): Map<Date, Set<TimesheetItem>>

    /**
     * Get timesheet segment for a project, without registered entries.
     *
     * @param projectId   Id for the project.
     * @param pageRequest Defines the page segment.
     * @return Project timesheet segment.
     */
    fun getTimesheetWithoutRegisteredEntries(projectId: Long, pageRequest: PageRequest): Map<Date, Set<TimesheetItem>>
}
