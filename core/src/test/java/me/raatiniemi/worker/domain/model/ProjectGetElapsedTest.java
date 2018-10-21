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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProjectGetElapsedTest {
    private final String message;
    private final long expected;
    private final List<TimeInterval> timeIntervals;

    public ProjectGetElapsedTest(String message, long expected, List<TimeInterval> timeIntervals) {
        this.message = message;
        this.expected = expected;
        this.timeIntervals = timeIntervals;
    }

    @Parameters
    public static Collection<Object[]> getParameters() throws DomainException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "without items",
                                0L,
                                Collections.emptyList()
                        },
                        {
                                "without active item",
                                0L,
                                Collections.singletonList(
                                        TimeIntervalFactory.builder()
                                                .stopInMilliseconds(1L)
                                                .build()
                                )
                        },
                        {
                                // Due to the implementation of the elapsed calculation
                                // (i.e. it creates a new Date instance as reference),
                                // a mock is needed required to test the behaviour.
                                "with active item",
                                50000L,
                                Collections.singletonList(mockActiveTimeWithElapsedTime())
                        }
                }
        );
    }

    private static TimeInterval mockActiveTimeWithElapsedTime() {
        TimeInterval timeInterval = mock(TimeInterval.class);

        when(timeInterval.isActive())
                .thenReturn(true);

        when(timeInterval.getInterval())
                .thenReturn(50000L);

        return timeInterval;
    }

    @Test
    public void getElapsed() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        project.addTime(timeIntervals);

        assertEquals(message, expected, project.getElapsed());
    }
}
