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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.domain.repository.TimesheetRepository;

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
                .thenReturn(new HashMap<Date, List<Time>>() {{
                    put(new Date(1L), Collections.emptyList());
                    put(new Date(2L), Collections.emptyList());
                    put(new Date(3L), Collections.emptyList());
                }});

        SortedMap<Date, List<Time>> actual = useCase.execute(1L, 0, true);

        assertEquals(new TreeMap<Date, List<Time>>() {{
            put(new Date(3L), Collections.emptyList());
            put(new Date(2L), Collections.emptyList());
            put(new Date(1L), Collections.emptyList());
        }}, actual);
    }

    @Test
    public void execute_withSortedDatesWithRegisteredTime() throws DomainException {
        when(repository.getTimesheet(1L, pageRequest))
                .thenReturn(new HashMap<Date, List<Time>>() {{
                    put(new Date(1L), Collections.emptyList());
                    put(new Date(2L), Collections.emptyList());
                    put(new Date(3L), Collections.emptyList());
                }});

        SortedMap<Date, List<Time>> actual = useCase.execute(1L, 0, false);

        assertEquals(new TreeMap<Date, List<Time>>() {{
            put(new Date(3L), Collections.emptyList());
            put(new Date(2L), Collections.emptyList());
            put(new Date(1L), Collections.emptyList());
        }}, actual);
    }
}
