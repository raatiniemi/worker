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

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.factory.TimeFactory;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class RemoveTimeTest {
    private TimeRepository timeRepository;
    private RemoveTime removeTime;

    @Before
    public void setUp() {
        timeRepository = mock(TimeRepository.class);
        removeTime = new RemoveTime(timeRepository);
    }

    @Test
    public void execute_withItem() throws DomainException {
        Time time = TimeFactory.builder()
                .id(1L)
                .build();

        removeTime.execute(time);

        verify(timeRepository).remove(eq(1L));
    }

    @Test
    public void execute_withItems() throws DomainException {
        Time time = TimeFactory.builder()
                .id(1L)
                .build();
        List<Time> items = Collections.singletonList(time);

        removeTime.execute(items);

        verify(timeRepository).remove(eq(items));
    }
}
