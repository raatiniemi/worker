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

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class MarkRegisteredTimeTest {
    private TimeRepository timeRepository;

    private static Time buildTime() throws ClockOutBeforeClockInException {
        return Time.builder(1L)
                .build();
    }

    private static Time buildRegisteredTime() throws ClockOutBeforeClockInException {
        return Time.builder(1L)
                .register()
                .build();
    }

    @Before
    public void setUp() {
        timeRepository = mock(TimeRepository.class);
    }

    @Test
    public void execute_withMultipleUnregisteredItems() throws DomainException {
        List<Time> timeToUpdate = new ArrayList<>();
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildTime());

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeRepository);
        markRegisteredTime.execute(timeToUpdate);

        List<Time> expectedTime = new ArrayList<>();
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());

        verify(timeRepository, times(1)).update(expectedTime);
    }

    @Test
    public void execute_withMultipleRegisteredItems() throws DomainException {
        List<Time> timeToUpdate = new ArrayList<>();
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeRepository);
        markRegisteredTime.execute(timeToUpdate);

        List<Time> expectedTime = new ArrayList<>();
        expectedTime.add(buildTime());
        expectedTime.add(buildTime());
        expectedTime.add(buildTime());
        expectedTime.add(buildTime());

        verify(timeRepository, times(1)).update(expectedTime);
    }

    @Test
    public void execute_withMultipleItems() throws DomainException {
        List<Time> timeToUpdate = new ArrayList<>();
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeRepository);
        markRegisteredTime.execute(timeToUpdate);

        List<Time> expectedTime = new ArrayList<>();
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());

        verify(timeRepository, times(1)).update(expectedTime);
    }
}
