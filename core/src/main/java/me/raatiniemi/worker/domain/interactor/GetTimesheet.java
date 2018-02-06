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
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import me.raatiniemi.worker.domain.comparator.TimesheetDateComparator;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.domain.repository.TimesheetRepository;

/**
 * Use case for getting segment from project timesheet.
 */
public class GetTimesheet {
    private final TimesheetRepository repository;

    /**
     * Constructor.
     *
     * @param repository Timesheet repository.
     */
    public GetTimesheet(TimesheetRepository repository) {
        this.repository = repository;
    }

    /**
     * Get segment from project timesheet.
     *
     * @param projectId          Id for project.
     * @param offset             Offset for segment.
     * @param hideRegisteredTime Should registered time be hidden.
     * @return Segment of project timesheet.
     */
    public SortedMap<Date, SortedSet<TimesheetItem>> execute(
            final Long projectId,
            final int offset,
            boolean hideRegisteredTime
    ) {
        PageRequest pageRequest = PageRequest.Companion.withOffset(offset);

        if (hideRegisteredTime) {
            return sortedEntries(repository.getTimesheetWithoutRegisteredEntries(projectId, pageRequest));
        }

        return sortedEntries(repository.getTimesheet(projectId, pageRequest));
    }

    private static SortedMap<Date, SortedSet<TimesheetItem>> sortedEntries(Map<Date, Set<TimesheetItem>> entries) {
        SortedMap<Date, SortedSet<TimesheetItem>> result = new TreeMap<>(new TimesheetDateComparator());

        for (Map.Entry<Date, Set<TimesheetItem>> entry : entries.entrySet()) {
            result.put(entry.getKey(), sortItems(entry.getValue()));
        }

        return result;
    }

    private static SortedSet<TimesheetItem> sortItems(Set<TimesheetItem> items) {
        return new TreeSet<>(items);
    }
}
