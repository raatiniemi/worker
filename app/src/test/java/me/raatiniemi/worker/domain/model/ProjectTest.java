/*
 * Copyright (C) 2015-2016 Worker Project
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

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class ProjectTest {
    private static Project.Builder createProjectBuilder() {
        return new Project.Builder("Project name");
    }

    private static Time createTimeWithIntervalInMilliseconds(
            long intervalInMilliseconds
    ) throws ClockOutBeforeClockInException {
        return new Time.Builder(1L)
                .stopInMilliseconds(intervalInMilliseconds)
                .build();
    }

    private static Time createActiveTimeWithElapsedTimeInMilliseconds(
            long elapsedTimeInMilliseconds
    ) {
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);
        when(time.getInterval()).thenReturn(elapsedTimeInMilliseconds);

        return time;
    }

    private static Time createActiveTimeWithStartInMilliseconds(
            long clockedInSinceInMilliseconds
    ) throws ClockOutBeforeClockInException {
        return new Time.Builder(1L)
                .startInMilliseconds(clockedInSinceInMilliseconds)
                .build();
    }

    @DataProvider
    public static Object[][] summarizeTime_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "without items",
                        0L,
                        new Time[]{}
                },
                {
                        "with single item",
                        60000L,
                        new Time[]{
                                createTimeWithIntervalInMilliseconds(60000L)
                        }
                },
                {
                        "with multiple items",
                        90000L,
                        new Time[]{
                                createTimeWithIntervalInMilliseconds(60000L),
                                createTimeWithIntervalInMilliseconds(30000L)
                        }
                },
                {
                        "with active time",
                        60000L,
                        new Time[]{
                                createTimeWithIntervalInMilliseconds(60000L),
                                createTimeWithIntervalInMilliseconds(0L)
                        }
                }
        };
    }

    @DataProvider
    public static Object[][] getElapsed_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "without items",
                        0L,
                        new Time[]{}
                },
                {
                        "without active item",
                        0L,
                        new Time[]{
                                createTimeWithIntervalInMilliseconds(1L)
                        }
                },
                {
                        // Due to the implementation of the elapsed calculation
                        // (i.e. it creates a new Date instance as reference),
                        // a mock is needed required to test the behaviour.
                        "with active item",
                        50000L,
                        new Time[]{
                                createActiveTimeWithElapsedTimeInMilliseconds(50000L)
                        }
                }
        };
    }

    @DataProvider
    public static Object[][] getClockedInSince_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "without items",
                        null,
                        new Time[]{}
                },
                {
                        "without active item",
                        null,
                        new Time[]{
                                createTimeWithIntervalInMilliseconds(1L)
                        }
                },
                {
                        "with active item",
                        new Date(50000L),
                        new Time[]{
                                createActiveTimeWithStartInMilliseconds(50000L)
                        }
                }
        };
    }

    @DataProvider
    public static Object[][] isActive_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "without items",
                        false,
                        new Time[]{}
                },
                {
                        "without active item",
                        false,
                        new Time[]{
                                createTimeWithIntervalInMilliseconds(1L)
                        }
                },
                {
                        "with active item",
                        true,
                        new Time[]{
                                createActiveTimeWithStartInMilliseconds(50000L)
                        }
                }
        };
    }

    @Test
    public void Builder_withDefaultValues()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        assertNull(project.getId());
        assertEquals("Project name", project.getName());
        assertNull(project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    public void Builder_withValues()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .id(2L)
                .describe("Project description")
                .archive()
                .build();

        assertEquals("Project name", project.getName());
        assertEquals(Long.valueOf(2L), project.getId());
        assertEquals("Project description", project.getDescription());
        assertTrue(project.isArchived());
    }

    @Test(expected = InvalidProjectNameException.class)
    public void Project_withNullName()
            throws InvalidProjectNameException {
        new Project.Builder(null)
                .build();
    }

    @Test(expected = InvalidProjectNameException.class)
    public void Project_withEmptyName()
            throws InvalidProjectNameException {
        new Project.Builder("")
                .build();
    }

    @Test
    public void getName()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        assertEquals("Project name", project.getName());
    }

    @Test(expected = InvalidProjectNameException.class)
    public void rename_withNullName()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.rename(null);
    }

    @Test(expected = InvalidProjectNameException.class)
    public void rename_withEmptyName()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.rename("");
    }

    @Test
    public void rename()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.rename("New project name");

        assertEquals("New project name", project.getName());
    }

    @Test
    public void describe()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.describe("Project description");

        assertEquals("Project description", project.getDescription());
    }

    @Test
    public void describe_withNullDescription()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.describe(null);

        assertNull(project.getDescription());
    }

    @Test
    public void describe_withEmptyDescription()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.describe("");

        assertNull(project.getDescription());
    }

    @Test
    public void archive()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        assertFalse(project.isArchived());
        project.archive();
        assertTrue(project.isArchived());
    }

    @Test
    public void unarchive()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .archive()
                .build();

        assertTrue(project.isArchived());
        project.unarchive();
        assertFalse(project.isArchived());
    }

    @Test
    public void addTime_withList()
            throws InvalidProjectNameException, ClockOutBeforeClockInException {
        Project project = createProjectBuilder()
                .id(1L)
                .build();

        List<Time> times = new ArrayList<>();
        times.add(
                new Time.Builder(project.getId())
                        .build()
        );
        times.add(
                new Time.Builder(project.getId())
                        .build()
        );
        project.addTime(times);

        times = project.getTime();
        assertEquals(2, times.size());
    }

    @Test(expected = NullPointerException.class)
    public void addTime_withNullList()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.addTime(null);
    }

    @Test
    public void addTime_withEmptyTimeList()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        List<Time> times = new ArrayList<>();
        project.addTime(times);

        assertTrue(project.getTime().isEmpty());
    }

    @Test
    @UseDataProvider("summarizeTime_dataProvider")
    public void summarizeTime(
            String message,
            long expected,
            Time[] times
    ) throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.addTime(Arrays.asList(times));

        assertEquals(message, expected, project.summarizeTime());
    }

    @Test
    @UseDataProvider("getElapsed_dataProvider")
    public void getElapsed(
            String message,
            long expected,
            Time[] times
    ) throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.addTime(Arrays.asList(times));

        assertEquals(message, expected, project.getElapsed());
    }

    @Test
    @UseDataProvider("getClockedInSince_dataProvider")
    public void getClockedInSince(
            String message,
            Date expected,
            Time[] times
    ) throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.addTime(Arrays.asList(times));

        if (null == expected) {
            assertNull(message, project.getClockedInSince());
            return;
        }

        Date actual = project.getClockedInSince();
        assertEquals(message, expected.getTime(), actual.getTime());
    }

    @Test(expected = NullPointerException.class)
    public void clockInAt_withNullDate()
            throws DomainException {
        Project project = createProjectBuilder()
                .build();

        project.clockInAt(null);
    }

    @Test(expected = ClockActivityException.class)
    public void clockInAt_withActiveTime()
            throws DomainException {
        Project project = createProjectBuilder()
                .build();

        List<Time> times = new ArrayList<>();
        times.add(
                createActiveTimeWithStartInMilliseconds(0L)
        );
        project.addTime(times);

        Date date = new Date();
        project.clockInAt(date);
    }

    @Test
    public void clockInAt_withoutActiveTime()
            throws DomainException {
        Project project = createProjectBuilder()
                .id(1L)
                .build();

        Date date = new Date(100L);

        Time time = project.clockInAt(date);
        assertNotNull(time);
        assertNull(time.getId());
        assertEquals(1L, time.getProjectId());
        assertEquals(100L, time.getStartInMilliseconds());
        assertEquals(0L, time.getStopInMilliseconds());
    }

    @Test(expected = NullPointerException.class)
    public void clockOutAt_withNullDate()
            throws DomainException {
        Project project = createProjectBuilder()
                .build();

        project.clockOutAt(null);
    }

    @Test(expected = ClockActivityException.class)
    public void clockOutAt_withoutTime()
            throws DomainException {
        Project project = createProjectBuilder()
                .build();

        Date date = new Date();
        project.clockOutAt(date);
    }

    @Test(expected = ClockActivityException.class)
    public void clockOutAt_withoutActiveTime()
            throws DomainException {
        Project project = createProjectBuilder()
                .build();

        List<Time> times = new ArrayList<>();
        times.add(createTimeWithIntervalInMilliseconds(500L));
        project.addTime(times);

        Date date = new Date();
        project.clockOutAt(date);
    }

    @Test
    public void clockOutAt_withActiveTime()
            throws DomainException {
        Project project = createProjectBuilder()
                .build();

        List<Time> times = new ArrayList<>();
        times.add(createActiveTimeWithStartInMilliseconds(100L));
        project.addTime(times);

        Date date = new Date(200L);
        Time time = project.clockOutAt(date);
        assertEquals(100L, time.getStartInMilliseconds());
        assertEquals(200L, time.getStopInMilliseconds());

        // The `clockOutAt` modifies the active time, i.e. the project should
        // be inactive after clocking out.
        assertFalse(project.isActive());
    }

    @Test
    @UseDataProvider("isActive_dataProvider")
    public void isActive(
            String message,
            boolean expected,
            Time[] times
    ) throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.addTime(Arrays.asList(times));

        assertTrue(message, expected == project.isActive());
    }
}
