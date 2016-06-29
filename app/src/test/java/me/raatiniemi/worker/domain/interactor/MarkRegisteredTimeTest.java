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

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class MarkRegisteredTimeTest {
    private TimeRepository mTimeRepository;

    private static Time buildTime() throws ClockOutBeforeClockInException {
        return new Time.Builder(1L)
                .build();
    }

    private static Time buildRegisteredTime() throws ClockOutBeforeClockInException {
        return new Time.Builder(1L)
                .register()
                .build();
    }

    @Before
    public void setUp() {
        mTimeRepository = mock(TimeRepository.class);
    }

    @Test
    public void execute_unmarkRegistered() throws DomainException {
        Time markedTime = new Time.Builder(1L)
                .register()
                .build();

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(mTimeRepository);
        markRegisteredTime.execute(markedTime);

        Time unmarkedTime = markedTime.unmarkRegistered();
        verify(mTimeRepository).update(eq(unmarkedTime));
    }

    @Test
    public void execute_markRegistered() throws DomainException {
        Time unmarkedTime = new Time.Builder(1L)
                .build();

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(mTimeRepository);
        markRegisteredTime.execute(unmarkedTime);

        Time markedTime = unmarkedTime.markAsRegistered();
        verify(mTimeRepository).update(eq(markedTime));
    }

    @Test
    public void execute_withMultipleUnregisteredItems() throws DomainException {
        List<Time> timeToUpdate = new ArrayList<>();
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildTime());

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(mTimeRepository);
        markRegisteredTime.execute(timeToUpdate);

        List<Time> expectedTime = new ArrayList<>();
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());

        verify(mTimeRepository, times(1)).update(expectedTime);
    }

    @Test
    public void execute_withMultipleRegisteredItems() throws DomainException {
        List<Time> timeToUpdate = new ArrayList<>();
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(mTimeRepository);
        markRegisteredTime.execute(timeToUpdate);

        List<Time> expectedTime = new ArrayList<>();
        expectedTime.add(buildTime());
        expectedTime.add(buildTime());
        expectedTime.add(buildTime());
        expectedTime.add(buildTime());

        verify(mTimeRepository, times(1)).update(expectedTime);
    }

    @Test
    public void execute_withMultipleItems() throws DomainException {
        List<Time> timeToUpdate = new ArrayList<>();
        timeToUpdate.add(buildTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());
        timeToUpdate.add(buildRegisteredTime());

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(mTimeRepository);
        markRegisteredTime.execute(timeToUpdate);

        List<Time> expectedTime = new ArrayList<>();
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());
        expectedTime.add(buildRegisteredTime());

        verify(mTimeRepository, times(1)).update(expectedTime);
    }
}
