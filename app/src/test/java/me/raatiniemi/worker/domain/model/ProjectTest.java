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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProjectTest {
    @Test
    public void Builder_withDefaultValues()
            throws InvalidProjectNameException {
        Project project = new Project.Builder("Project name")
                .build();

        assertNull(project.getId());
        assertEquals("Project name", project.getName());
        assertNull(project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    public void Builder_withValues()
            throws InvalidProjectNameException {
        Project project = new Project.Builder("Project name")
                .id(2L)
                .describe("Project description")
                .archive()
                .build();

        assertEquals("Project name", project.getName());
        assertEquals(Long.valueOf(2L), project.getId());
        assertEquals("Project description", project.getDescription());
        assertTrue(project.isArchived());
    }

    @Test
    public void Project_defaultValueFromIdNameConstructor() throws InvalidProjectNameException {
        Project project = new Project(2L, "Project name");

        assertEquals(Long.valueOf(2L), project.getId());
        assertEquals("Project name", project.getName());

        assertFalse(project.isArchived());
        assertTrue(project.getTime().isEmpty());
    }

    @Test
    public void Project_defaultValueFromNameConstructor() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertNull(project.getId());
        assertEquals("Project name", project.getName());

        assertFalse(project.isArchived());
        assertTrue(project.getTime().isEmpty());
    }

    @Test(expected = InvalidProjectNameException.class)
    public void setName_nullValueFromIdNameConstructor() throws InvalidProjectNameException {
        new Project(null, null);
    }

    @Test(expected = InvalidProjectNameException.class)
    public void setName_emptyValueFromIdNameConstructor() throws InvalidProjectNameException {
        new Project(null, "");
    }

    @Test(expected = InvalidProjectNameException.class)
    public void setName_nullValueFromNameConstructor() throws InvalidProjectNameException {
        new Project(null, null);
    }

    @Test(expected = InvalidProjectNameException.class)
    public void setName_emptyValueFromNameConstructor() throws InvalidProjectNameException {
        new Project(null, "");
    }

    @Test
    public void getName_valueFromConstructor() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertEquals("Project name", project.getName());

        project = new Project(1L, "New project name");

        assertEquals("New project name", project.getName());
    }

    @Test(expected = InvalidProjectNameException.class)
    public void rename_withNullValue() throws InvalidProjectNameException {
        Project project = new Project(1L, "Project name");
        project.rename(null);
    }

    @Test(expected = InvalidProjectNameException.class)
    public void rename_withEmptyValue() throws InvalidProjectNameException {
        Project project = new Project(1L, "Project name");
        project.rename("");
    }

    @Test
    public void rename() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");
        project.rename("New project name");

        assertEquals("New project name", project.getName());
    }

    @Test
    public void getDescription_valueFromSetter() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");
        project.setDescription("Project description");

        assertEquals("Project description", project.getDescription());
    }

    @Test
    public void setDescription_withEmptyString() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");
        project.setDescription("");

        assertNull(project.getDescription());
    }

    @Test
    public void setDescription_withNull() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");
        project.setDescription(null);

        assertNull(project.getDescription());
    }

    @Test
    public void isArchived_defaultValue() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertFalse(project.isArchived());
    }

    @Test
    public void isArchived_valueFromSetter() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");
        project.setArchived(true);

        assertTrue(project.isArchived());

        project.setArchived(false);

        assertFalse(project.isArchived());
    }

    @Test
    public void getTime_withoutTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertNotNull(project.getTime());
        assertEquals(0, project.getTime().size());
    }

    @Test
    public void getTime_valueFromAddTimeList() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        List<Time> times = project.getTime();
        assertEquals(0, times.size());

        Time time = mock(Time.class);
        project.addTime(time);

        times = project.getTime();
        assertEquals(1, times.size());
        assertEquals(time, times.get(0));
    }

    @Test
    public void getTime_valuesFromAddTimeList() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        List<Time> times = new ArrayList<>();
        Time time1 = mock(Time.class);
        times.add(time1);
        project.addTime(times);

        times = new ArrayList<>();
        Time time2 = mock(Time.class);
        times.add(time2);
        project.addTime(times);

        times = project.getTime();
        assertEquals(2, times.size());
        assertEquals(time1, times.get(0));
        assertEquals(time2, times.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void addTime_withNullValue() throws InvalidProjectNameException {
        Project project = new Project(1L, "Project name");
        project.addTime((Time) null);
    }

    @Test(expected = NullPointerException.class)
    public void addTime_withNullList() throws InvalidProjectNameException {
        Project project = new Project(1L, "Project name");
        project.addTime((List<Time>) null);
    }

    @Test
    public void addTime_withEmptyTimeList() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        List<Time> times = new ArrayList<>();
        project.addTime(times);

        assertTrue(project.getTime().isEmpty());
    }

    @Test
    public void summarizeTime_withoutTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertEquals(Long.valueOf(0L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_withActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(0L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(60000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_withSingleItem() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time = mock(Time.class);
        when(time.getTime()).thenReturn(60000L);

        project.addTime(time);

        assertEquals(Long.valueOf(60000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_withValueRoundUp() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(30000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(90000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_withValueRoundDown() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(29000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(89000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_multipleItems() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(3600000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(1800000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(5400000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void getElapsed_withoutTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertEquals(Long.valueOf(0L), Long.valueOf(project.getElapsed()));
    }

    @Test
    public void getElapsed_withoutActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        assertEquals(Long.valueOf(0L), Long.valueOf(project.getElapsed()));
        verify(time, times(1)).isActive();
    }

    @Test
    public void getElapsed_withActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);
        when(time.getInterval()).thenReturn(50000L);

        project.addTime(time);

        assertEquals(Long.valueOf(50000L), Long.valueOf(project.getElapsed()));
        verify(time, times(1)).isActive();
        verify(time, times(1)).getInterval();
    }

    @Test
    public void getClockedInSince_withoutTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertNull(project.getClockedInSince());
    }

    @Test
    public void getClockedInSince_withoutActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        assertNull(project.getClockedInSince());
        verify(time, times(1)).isActive();
    }

    @Test
    public void getClockedInSince_withActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);
        when(time.getStartInMilliseconds()).thenReturn(500000L);

        project.addTime(time);

        Date date = project.getClockedInSince();

        assertNotNull(date);
        assertEquals(Long.valueOf(500000L), Long.valueOf(date.getTime()));
        verify(time, times(1)).isActive();
        verify(time, times(1)).getStartInMilliseconds();
    }

    @Test(expected = NullPointerException.class)
    public void clockInAt_withNullDate()
            throws DomainException {
        Project project = new Project(1L, "Project name");
        project.clockInAt(null);
    }

    @Test(expected = ClockActivityException.class)
    public void clockInAt_withActiveTime() throws DomainException {
        Project project = new Project(null, "Project name");

        // Setup the time object to return true for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);

        project.addTime(time);

        Date date = mock(Date.class);
        project.clockInAt(date);

        verify(time, times(1)).isActive();
    }

    @Test
    public void clockInAt_withoutActiveTime() throws DomainException {
        Project project = new Project(1L, "Project name");

        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(100L);

        Time time = project.clockInAt(date);

        assertNotNull(time);
        assertNull(time.getId());
        assertEquals(1L, time.getProjectId());
        assertEquals(100L, time.getStartInMilliseconds());
        assertEquals(0L, time.getStopInMilliseconds());
        verify(date, times(1)).getTime();
    }

    @Test(expected = NullPointerException.class)
    public void clockOutAt_withNullDate()
            throws DomainException {
        Project project = new Project(1L, "Project name");
        project.clockOutAt(null);
    }

    @Test(expected = ClockActivityException.class)
    public void clockOutAt_withoutTime() throws DomainException {
        Project project = new Project(null, "Project name");

        Date date = mock(Date.class);
        project.clockOutAt(date);
    }

    @Test(expected = ClockActivityException.class)
    public void clockOutAt_withoutActiveTime() throws DomainException {
        Project project = new Project(null, "Project name");

        // Setup the time object to return true for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        Date date = mock(Date.class);
        project.clockOutAt(date);

        verify(time, times(1)).isActive();
    }

    @Test
    public void clockOutAt_withActiveTime() throws DomainException {
        Project project = new Project(null, "Project name");

        Time time = new Time.Builder(1L)
                .build();
        project.addTime(time);

        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(2L);

        time = project.clockOutAt(date);
        assertEquals(2L, time.getStopInMilliseconds());
        assertFalse(project.isActive());
    }

    @Test
    public void isActive_withoutTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        assertFalse(project.isActive());
    }

    @Test
    public void isActive_withoutActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        // Setup the time object to return false for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        assertFalse(project.isActive());
        verify(time, times(1)).isActive();
    }

    @Test
    public void isActive_withActiveTime() throws InvalidProjectNameException {
        Project project = new Project(null, "Project name");

        // Setup the time object to return true for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);

        project.addTime(time);

        assertTrue(project.isActive());
        verify(time, times(1)).isActive();
    }
}
