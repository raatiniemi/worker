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

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InactiveProjectException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ClockOutTest {
    private TimeRepository timeRepository;

    @Before
    public void setUp() {
        timeRepository = mock(TimeRepository.class);
    }

    @Test(expected = InactiveProjectException.class)
    public void execute_withoutActiveTime() throws DomainException {
        when(timeRepository.getActiveTimeForProject(1L))
                .thenReturn(null);

        ClockOut clockOut = new ClockOut(timeRepository);
        clockOut.execute(1L, new Date());
    }

    @Test
    public void execute() throws DomainException {
        when(timeRepository.getActiveTimeForProject(1L))
                .thenReturn(
                        Time.builder(1L)
                                .build()
                );

        ClockOut clockOut = new ClockOut(timeRepository);
        clockOut.execute(1L, new Date());

        verify(timeRepository).update(isA(Time.class));
    }
}
