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

import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class MarkRegisteredTimeTest {
    private TimeIntervalRepository timeIntervalRepository;

    @Before
    public void setUp() {
        timeIntervalRepository = mock(TimeIntervalRepository.class);
    }

    @Test
    public void execute_withMultipleUnregisteredItems() {
        List<TimeInterval> timeIntervalsToUpdate = new ArrayList<>();
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .build()
        );

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeIntervalRepository);
        markRegisteredTime.execute(timeIntervalsToUpdate);

        List<TimeInterval> expectedTimeIntervals = new ArrayList<>();
        expectedTimeIntervals.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        expectedTimeIntervals.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        expectedTimeIntervals.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        expectedTimeIntervals.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        verify(timeIntervalRepository, times(1)).update(expectedTimeIntervals);
    }

    @Test
    public void execute_withMultipleRegisteredItems() {
        List<TimeInterval> timeIntervalsToUpdate = new ArrayList<>();
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeIntervalRepository);
        markRegisteredTime.execute(timeIntervalsToUpdate);

        List<TimeInterval> expectedTimeInterval = new ArrayList<>();
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .build()
        );
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .build()
        );
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .build()
        );
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .build()
        );
        verify(timeIntervalRepository, times(1)).update(expectedTimeInterval);
    }

    @Test
    public void execute_withMultipleItems() {
        List<TimeInterval> timeIntervalsToUpdate = new ArrayList<>();
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeIntervalRepository);
        markRegisteredTime.execute(timeIntervalsToUpdate);

        List<TimeInterval> expectedTimeInterval = new ArrayList<>();
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );

        expectedTimeInterval.add(
                TimeInterval.builder(1L)
                        .register()
                        .build()
        );
        verify(timeIntervalRepository, times(1)).update(expectedTimeInterval);
    }
}
