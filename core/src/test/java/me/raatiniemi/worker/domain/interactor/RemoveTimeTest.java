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
import java.util.List;

import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class RemoveTimeTest {
    private TimeIntervalRepository timeIntervalRepository;
    private RemoveTime removeTime;

    @Before
    public void setUp() {
        timeIntervalRepository = mock(TimeIntervalRepository.class);
        removeTime = new RemoveTime(timeIntervalRepository);
    }

    @Test
    public void execute_withItem() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .id(1L)
                .build();

        removeTime.execute(timeInterval);

        verify(timeIntervalRepository).remove(eq(1L));
    }

    @Test
    public void execute_withItems() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .id(1L)
                .build();
        List<TimeInterval> items = Collections.singletonList(timeInterval);

        removeTime.execute(items);

        verify(timeIntervalRepository).remove(eq(items));
    }
}
