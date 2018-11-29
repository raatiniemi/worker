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

import me.raatiniemi.worker.domain.comparator.TimesheetDateComparator
import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem
import java.util.*

class TimesheetInMemoryRepository(private val timeIntervals: List<TimeInterval>) : TimesheetRepository {
    private fun resetToStartOfDay(timeInMilliseconds: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliseconds
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    // TODO: Implement proper support for pagination.
    private fun filterAndBuildResult(predicate: (TimeInterval) -> Boolean)
            : TreeMap<Date, Set<TimesheetItem>> {
        val matchingTimeIntervals = timeIntervals.filter { predicate(it) }
                .groupBy { resetToStartOfDay(it.startInMilliseconds) }

        val timeIntervals = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator())
        matchingTimeIntervals.forEach {
            timeIntervals[it.key] = it.value
                    .map { timeInterval -> TimesheetItem(timeInterval) }
                    .toSortedSet(TimesheetItemComparator())
        }

        return timeIntervals
    }

    override fun getTimesheet(
            projectId: Long,
            pageRequest: PageRequest
    ): Map<Date, Set<TimesheetItem>> {
        return filterAndBuildResult { it.projectId == projectId }
    }

    override fun getTimesheetWithoutRegisteredEntries(
            projectId: Long,
            pageRequest: PageRequest
    ): Map<Date, Set<TimesheetItem>> {
        return filterAndBuildResult { it.projectId == projectId && !it.isRegistered }
    }
}
