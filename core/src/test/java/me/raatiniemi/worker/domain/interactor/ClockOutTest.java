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

import me.raatiniemi.worker.domain.exception.InactiveProjectException;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.util.Optional;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ClockOutTest {
    private TimeIntervalRepository timeIntervalRepository;

    @Before
    public void setUp() {
        timeIntervalRepository = mock(TimeIntervalRepository.class);
    }

    @Test(expected = InactiveProjectException.class)
    public void execute_withoutActiveTime() {
        when(timeIntervalRepository.findActiveByProjectId(1L))
                .thenReturn(Optional.empty());

        ClockOut clockOut = new ClockOut(timeIntervalRepository);
        clockOut.execute(1L, new Date());
    }

    @Test
    public void execute() {
        TimeInterval timeInterval = TimeInterval.builder(1L)
                .stopInMilliseconds(0L)
                .build();
        when(timeIntervalRepository.findActiveByProjectId(1L))
                .thenReturn(Optional.of(timeInterval));

        ClockOut clockOut = new ClockOut(timeIntervalRepository);
        clockOut.execute(1L, new Date());

        verify(timeIntervalRepository).update(isA(TimeInterval.class));
    }
}
