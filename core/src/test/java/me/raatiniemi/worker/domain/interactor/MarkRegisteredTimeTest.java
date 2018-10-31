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
import me.raatiniemi.worker.factory.TimeIntervalFactory;

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
                TimeIntervalFactory.builder()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .build()
        );

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeIntervalRepository);
        markRegisteredTime.execute(timeIntervalsToUpdate);

        List<TimeInterval> expectedTimeIntevals = new ArrayList<>();
        expectedTimeIntevals.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        expectedTimeIntevals.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        expectedTimeIntevals.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        expectedTimeIntevals.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        verify(timeIntervalRepository, times(1)).update(expectedTimeIntevals);
    }

    @Test
    public void execute_withMultipleRegisteredItems() {
        List<TimeInterval> timeIntervalsToUpdate = new ArrayList<>();
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeIntervalRepository);
        markRegisteredTime.execute(timeIntervalsToUpdate);

        List<TimeInterval> expectedTimeInterval = new ArrayList<>();
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        verify(timeIntervalRepository, times(1)).update(expectedTimeInterval);
    }

    @Test
    public void execute_withMultipleItems() {
        List<TimeInterval> timeIntervalsToUpdate = new ArrayList<>();
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        timeIntervalsToUpdate.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );

        MarkRegisteredTime markRegisteredTime = new MarkRegisteredTime(timeIntervalRepository);
        markRegisteredTime.execute(timeIntervalsToUpdate);

        List<TimeInterval> expectedTimeInterval = new ArrayList<>();
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );

        expectedTimeInterval.add(
                TimeIntervalFactory.builder()
                        .register()
                        .build()
        );
        verify(timeIntervalRepository, times(1)).update(expectedTimeInterval);
    }
}
