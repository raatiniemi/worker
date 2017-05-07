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
import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator;
import me.raatiniemi.worker.domain.model.Time;
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
    public SortedMap<Date, SortedSet<Time>> execute(
            final Long projectId,
            final int offset,
            boolean hideRegisteredTime
    ) {
        PageRequest pageRequest = PageRequest.withOffset(offset);

        if (hideRegisteredTime) {
            return sortedEntries(repository.getTimesheetWithoutRegisteredEntries(projectId, pageRequest));
        }

        return sortedEntries(repository.getTimesheet(projectId, pageRequest));
    }

    private static SortedMap<Date, SortedSet<Time>> sortedEntries(Map<Date, Set<Time>> entries) {
        SortedMap<Date, SortedSet<Time>> result = new TreeMap<>(new TimesheetDateComparator());

        for (Map.Entry<Date, Set<Time>> entry : entries.entrySet()) {
            result.put(entry.getKey(), sortItems(entry.getValue()));
        }

        return result;
    }

    private static SortedSet<Time> sortItems(Set<Time> items) {
        SortedSet<Time> result = new TreeSet<>(new TimesheetItemComparator());
        result.addAll(items);

        return result;
    }
}
