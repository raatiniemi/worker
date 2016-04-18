/*
 * Copyright (C) 2016 Worker Project
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
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class GetTimesheetTest {
    private TimeRepository mTimeRepository;

    @Before
    public void setUp() {
        mTimeRepository = mock(TimeRepository.class);
    }

    @Test
    public void execute_hideRegisteredTime() throws DomainException {
        GetTimesheet getTimesheet = new GetTimesheet(mTimeRepository);
        getTimesheet.execute(1L, 0, true);

        verify(mTimeRepository).getTimesheet(eq(1L), eq(0), any(Criteria.class));
    }

    @Test
    public void execute_withRegisteredTime() throws DomainException {
        GetTimesheet getTimesheet = new GetTimesheet(mTimeRepository);
        getTimesheet.execute(1L, 0, false);

        verify(mTimeRepository).getTimesheet(eq(1L), eq(0), isNull(Criteria.class));
    }
}
