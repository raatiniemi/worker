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

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.domain.repository.TimesheetRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class GetTimesheetTest {
    private TimesheetRepository repository;

    @Before
    public void setUp() {
        repository = mock(TimesheetRepository.class);
    }

    @Test
    public void execute_hideRegisteredTime() throws DomainException {
        GetTimesheet getTimesheet = new GetTimesheet(repository);
        getTimesheet.execute(1L, 0, true);

        verify(repository).getTimesheetWithoutRegisteredEntries(eq(1L), eq(PageRequest.withOffset(0)));
    }

    @Test
    public void execute_withRegisteredTime() throws DomainException {
        GetTimesheet getTimesheet = new GetTimesheet(repository);
        getTimesheet.execute(1L, 0, false);

        verify(repository).getTimesheet(eq(1L), eq(PageRequest.withOffset(0)));
    }
}
