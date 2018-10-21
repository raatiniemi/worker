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

package me.raatiniemi.worker.features.projects.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProjectsItemGetClockedInSinceTest extends ProjectsItemResourceTest {
    private final String message;
    private final String expected;
    private final TimeInterval[] timeIntervals;

    public ProjectsItemGetClockedInSinceTest(
            String message,
            String expected,
            TimeInterval... timeIntervals
    ) {
        this.message = message;
        this.expected = expected;
        this.timeIntervals = timeIntervals;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without registered time",
                                null,
                                null
                        },
                        {
                                "Without active time",
                                null,
                                new TimeInterval[]{
                                        TimeIntervalFactory.builder()
                                                .stopInMilliseconds(1L)
                                                .build()
                                }
                        },
                        {
                                "With an hour elapsed",
                                "Since 15:14 (1h 0m)",
                                new TimeInterval[]{
                                        mockActiveTimeWithElapsedTimeInSecondsAndClockedInTime(
                                                3600L,
                                                new GregorianCalendar(2016, 1, 28, 15, 14)
                                        )
                                }
                        },
                        {
                                "With half an hour elapsed",
                                "Since 20:25 (30m)",
                                new TimeInterval[]{
                                        mockActiveTimeWithElapsedTimeInSecondsAndClockedInTime(
                                                1800L,
                                                new GregorianCalendar(2016, 1, 28, 20, 25)
                                        )
                                }
                        }
                }
        );
    }

    private static TimeInterval mockActiveTimeWithElapsedTimeInSecondsAndClockedInTime(
            long elapsedTimeInSeconds,
            Calendar clockedInTime
    ) {
        TimeInterval timeInterval = mock(TimeInterval.class);

        when(timeInterval.isActive())
                .thenReturn(true);

        when(timeInterval.getInterval())
                .thenReturn(elapsedTimeInSeconds * 1000);

        when(timeInterval.getStartInMilliseconds())
                .thenReturn(clockedInTime.getTimeInMillis());

        return timeInterval;
    }

    @Test
    public void getClockedInSince() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        if (isNull(timeIntervals)) {
            assertNull(message, projectsItem.getClockedInSince(getResources()));
            return;
        }
        project.addTime(Arrays.asList(timeIntervals));

        assertEquals(expected, projectsItem.getClockedInSince(getResources()));
    }
}
