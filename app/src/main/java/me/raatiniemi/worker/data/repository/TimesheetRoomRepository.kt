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

package me.raatiniemi.worker.data.repository

import me.raatiniemi.worker.data.projects.TimeIntervalDao
import me.raatiniemi.worker.data.projects.TimesheetDao
import me.raatiniemi.worker.data.projects.TimesheetDay
import me.raatiniemi.worker.domain.comparator.TimesheetDateComparator
import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.domain.repository.PageRequest
import me.raatiniemi.worker.domain.repository.TimesheetRepository
import java.util.*

class TimesheetRoomRepository(
        private val timesheet: TimesheetDao,
        private val timeIntervals: TimeIntervalDao
) : TimesheetRepository {
    private fun transform(timesheetDay: TimesheetDay): Pair<Date, Set<TimesheetItem>> {
        val map = timesheetDay.timeIntervalIds
                .mapNotNull { timeIntervals.find(it) }
                .map { it.toTimeInterval() }
                .map { TimesheetItem.with(it) }

        return Pair(
                Date(timesheetDay.dateInMilliseconds),
                map.toSortedSet(TimesheetItemComparator())
        )
    }

    override fun getTimesheet(
            projectId: Long,
            pageRequest: PageRequest
    ): Map<Date, Set<TimesheetItem>> {
        return timesheet.findAll(projectId, pageRequest.offset, pageRequest.maxResults)
                .map { transform(it) }
                .toMap()
                .toSortedMap(TimesheetDateComparator())
    }

    override fun getTimesheetWithoutRegisteredEntries(
            projectId: Long,
            pageRequest: PageRequest
    ): Map<Date, Set<TimesheetItem>> {
        return timesheet.findAllUnregistered(projectId, pageRequest.offset, pageRequest.maxResults)
                .map { transform(it) }
                .toMap()
                .toSortedMap(TimesheetDateComparator())
    }
}
