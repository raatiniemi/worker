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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.domain.repository.TimesheetRepository;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class GetTimesheetTest {
    private final PageRequest pageRequest = PageRequest.withOffset(0);

    private TimesheetRepository repository;
    private GetTimesheet useCase;

    private static TimesheetItem buildTimesheetItemWithInterval(long startInMilliseconds, long stopInMilliseconds) {
        return TimesheetItem.with(
                TimeFactory.builder()
                        .startInMilliseconds(startInMilliseconds)
                        .stopInMilliseconds(stopInMilliseconds)
                        .build()
        );
    }

    private static Set<TimesheetItem> getShuffledSet() {
        return new HashSet<TimesheetItem>() {{
            add(buildTimesheetItemWithInterval(2L, 3L));
            add(buildTimesheetItemWithInterval(4L, 0L));
            add(buildTimesheetItemWithInterval(1L, 3L));
            add(buildTimesheetItemWithInterval(1L, 2L));
            add(buildTimesheetItemWithInterval(4L, 5L));
        }};
    }

    private static SortedSet<TimesheetItem> getSortedSet() {
        return new TreeSet<TimesheetItem>() {{
            add(buildTimesheetItemWithInterval(4L, 0L));
            add(buildTimesheetItemWithInterval(4L, 5L));
            add(buildTimesheetItemWithInterval(2L, 3L));
            add(buildTimesheetItemWithInterval(1L, 3L));
            add(buildTimesheetItemWithInterval(1L, 2L));
        }};
    }

    @Before
    public void setUp() {
        repository = mock(TimesheetRepository.class);
        useCase = new GetTimesheet(repository);
    }

    @Test
    public void execute_hideRegisteredTime() throws DomainException {
        useCase.execute(1L, 0, true);

        verify(repository).getTimesheetWithoutRegisteredEntries(eq(1L), eq(pageRequest));
    }

    @Test
    public void execute_withRegisteredTime() throws DomainException {
        useCase.execute(1L, 0, false);

        verify(repository).getTimesheet(eq(1L), eq(pageRequest));
    }

    @Test
    public void execute_withSortedDatesHidingRegisteredTime() throws DomainException {
        when(repository.getTimesheetWithoutRegisteredEntries(1L, pageRequest))
                .thenReturn(new HashMap<Date, Set<TimesheetItem>>() {{
                    put(new Date(1L), getShuffledSet());
                    put(new Date(2L), getShuffledSet());
                    put(new Date(3L), getShuffledSet());
                }});

        SortedMap<Date, SortedSet<TimesheetItem>> actual = useCase.execute(1L, 0, true);

        assertEquals(new TreeMap<Date, SortedSet<TimesheetItem>>() {{
            put(new Date(3L), getSortedSet());
            put(new Date(2L), getSortedSet());
            put(new Date(1L), getSortedSet());
        }}, actual);
    }

    @Test
    public void execute_withSortedDatesWithRegisteredTime() throws DomainException {
        when(repository.getTimesheet(1L, pageRequest))
                .thenReturn(new HashMap<Date, Set<TimesheetItem>>() {{
                    put(new Date(1L), getShuffledSet());
                    put(new Date(2L), getShuffledSet());
                    put(new Date(3L), getShuffledSet());
                }});

        SortedMap<Date, SortedSet<TimesheetItem>> actual = useCase.execute(1L, 0, false);

        assertEquals(new TreeMap<Date, SortedSet<TimesheetItem>>() {{
            put(new Date(3L), getSortedSet());
            put(new Date(2L), getSortedSet());
            put(new Date(1L), getSortedSet());
        }}, actual);
    }
}
