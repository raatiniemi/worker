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

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.raatiniemi.worker.domain.model.Time;

public interface TimesheetRepository {
    /**
     * Get timesheet segment for a project.
     *
     * @param projectId Id for the project.
     * @param offset    Offset for segment.
     * @return Project timesheet segment.
     */
    Map<Date, List<Time>> getTimesheet(final long projectId, final int offset);

    /**
     * Get timesheet segment for a project, without registered entries.
     *
     * @param projectId Id for the project.
     * @param offset    Offset for segment.
     * @return Project timesheet segment.
     */
    Map<Date, List<Time>> getTimesheetWithoutRegisteredEntries(final long projectId, final int offset);
}
