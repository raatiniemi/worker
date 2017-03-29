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

package me.raatiniemi.worker.presentation.projects.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static me.raatiniemi.util.NullUtil.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProjectsItemGetClockedInSinceTest extends ProjectsItemResourceTest {
    private final String message;
    private final String expected;
    private final Time[] registeredTime;

    public ProjectsItemGetClockedInSinceTest(
            String message,
            String expected,
            Time... registeredTime
    ) {
        this.message = message;
        this.expected = expected;
        this.registeredTime = registeredTime;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
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
                                new Time[]{
                                        Time.builder(1L)
                                                .stopInMilliseconds(1L)
                                                .build()
                                }
                        },
                        {
                                "With an hour elapsed",
                                "Since 15:14 (1h 0m)",
                                new Time[]{
                                        createTimeForGetClockedInSinceTestWithElapsedAndClockedInTime(
                                                3600L,
                                                new GregorianCalendar(2016, 1, 28, 15, 14)
                                        )
                                }
                        },
                        {
                                "With half an hour elapsed",
                                "Since 20:25 (30m)",
                                new Time[]{
                                        createTimeForGetClockedInSinceTestWithElapsedAndClockedInTime(
                                                1800L,
                                                new GregorianCalendar(2016, 1, 28, 20, 25)
                                        )
                                }
                        }
                }
        );
    }

    private static Time createTimeForGetClockedInSinceTestWithElapsedAndClockedInTime(
            long intervalInSeconds,
            Calendar clockedInTime
    ) {
        Time time = mock(Time.class);

        when(time.isActive()).thenReturn(true);
        when(time.getInterval()).thenReturn(intervalInSeconds * 1000);

        when(time.getStartInMilliseconds()).thenReturn(clockedInTime.getTimeInMillis());

        return time;
    }

    @Test
    public void getClockedInSince() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        if (isNull(registeredTime)) {
            assertNull(message, projectsItem.getClockedInSince(getResources()));
            return;
        }
        project.addTime(Arrays.asList(registeredTime));

        assertEquals(expected, projectsItem.getClockedInSince(getResources()));
    }
}
