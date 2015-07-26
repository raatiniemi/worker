package me.raatiniemi.worker.model.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.domain.ClockActivityException;
import me.raatiniemi.worker.model.time.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectTest {
    @Test
    public void testConstructorWithIdAndName() {
        Project project = new Project(1L, "Foo");

        // Asserting against the literal directly will result
        // in an ambiguous method call.
        Long id = 1L;
        assertEquals(id, project.getId());
        assertEquals("Foo", project.getName());
    }

    @Test
    public void testConstructorWithName() {
        Project project = new Project("Foo");

        assertNull(project.getId());
        assertEquals("Foo", project.getName());
    }

    @Test
    public void testSetName() {
        Project project = new Project(1L, "Foo");

        assertEquals("Foo", project.getName());
        project.setName("Bar");
        assertEquals("Bar", project.getName());
    }

    @Test
    public void testGetDefaultDescription() {
        Project project = new Project(1L, "Foo");

        assertNull(project.getDescription());
    }

    @Test
    public void testSetDescription() {
        Project project = new Project(1L, "Foo");
        project.setDescription("Bar");

        assertEquals("Bar", project.getDescription());
    }

    @Test
    public void testResetDescriptionToEmptyString() {
        Project project = new Project(1L, "Foo");
        project.setDescription("");

        // The description should always reset
        // to null if the text is empty.
        assertNull(project.getDescription());
    }

    @Test
    public void testResetDescription() {
        Project project = new Project(1L, "Foo");
        project.setDescription(null);

        assertNull(project.getDescription());
    }

    @Test
    public void testGetArchived() {
        Project project = new Project(1L, "Foo");

        // Asserting against the literal directly will result
        // in an ambiguous method call.
        Long archived = 0L;
        assertEquals(archived, project.getArchived());
    }

    @Test
    public void testSetArchived() {
        Project project = new Project(1L, "Foo");
        project.setArchived(1L);

        // Asserting against the literal directly will result
        // in an ambiguous method call.
        Long archived = 1L;
        assertEquals(archived, project.getArchived());
    }

    @Test
    public void testGetTime() {
        Project project = new Project(1L, "Foo");

        assertNotNull(project.getTime());
        assertEquals(0, project.getTime().size());
    }

    @Test
    public void testAddTime() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);

        List<Time> list = project.getTime();
        assertEquals(0, list.size());
        project.addTime(time);

        list = project.getTime();
        assertEquals(1, list.size());
        assertEquals(time, list.get(0));

    }

    @Test
    public void testAddTimeList() {
        Project project = new Project(1L, "Foo");

        Time time1 = mock(Time.class);
        Time time2 = mock(Time.class);

        List<Time> list = new ArrayList<>();
        list.add(time1);
        list.add(time2);
        project.addTime(list);

        list = project.getTime();
        assertEquals(2, list.size());
        assertEquals(time1, list.get(0));
        assertEquals(time2, list.get(1));
    }

    @Test
    public void testAddEmptyTimeList() {
        Project project = new Project(1L, "Foo");

        List<Time> list = new ArrayList<>();
        project.addTime(list);

        list = project.getTime();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testSummarizeTime() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.getTime())
            .thenReturn(60000L);
        project.addTime(time);

        assertEquals(60000L, project.summarizeTime());
    }

    @Test
    public void testSummarizeWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertEquals(0L, project.summarizeTime());
    }

    @Test
    public void testSummarizeTimeWithActiveTime() {
        Project project = new Project(1L, "Foo");

        Time time1 = mock(Time.class);
        when(time1.getTime())
            .thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime())
            .thenReturn(0L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(60000L, project.summarizeTime());
    }

    @Test
    public void testSummarizeTimeWithRoundUp() {
        Project project = new Project(1L, "Foo");

        Time time1 = mock(Time.class);
        when(time1.getTime())
            .thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime())
            .thenReturn(30000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(90000L, project.summarizeTime());
    }

    @Test
    public void testSummarizeTimeWithRoundDown() {
        Project project = new Project(1L, "Foo");

        Time time1 = mock(Time.class);
        when(time1.getTime())
            .thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime())
            .thenReturn(29000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(89000L, project.summarizeTime());
    }

    @Test
    public void testSummarizeTimeWithHours() {
        Project project = new Project(1L, "Foo");

        Time time1 = mock(Time.class);
        when(time1.getTime())
            .thenReturn(3600000L);

        Time time2 = mock(Time.class);
        when(time2.getTime())
            .thenReturn(1800000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(5400000L, project.summarizeTime());
    }

    @Test
    public void testGetElapsedWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertEquals(0L, project.getElapsed());
    }

    @Test
    public void testGetElapsedWhenInactive() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(false);

        project.addTime(time);
        assertEquals(0L, project.getElapsed());
    }

    @Test
    public void testGetElapsedWhenActive() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);
        when(time.getInterval())
            .thenReturn(50000L);

        project.addTime(time);
        assertEquals(50000L, project.getElapsed());
    }

    @Test
    public void testGetClockedInSinceWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertNull(project.getClockedInSince());
    }

    @Test
    public void testGetClockedInSinceWhenInactive() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(false);

        project.addTime(time);
        assertNull(project.getClockedInSince());
    }

    @Test
    public void testGetClockedInSince() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);
        when(time.getStart())
            .thenReturn(500000L);

        project.addTime(time);

        Date date = project.getClockedInSince();
        assertEquals(500000L, date.getTime());
    }

    @Test(expected = ClockActivityException.class)
    public void testClockInAtWhenActive() throws DomainException {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);

        project.addTime(time);

        Date date = mock(Date.class);
        project.clockInAt(date);
    }

    @Test
    public void testClockInAt() throws DomainException {
        Project project = new Project(1L, "Foo");

        Date date = mock(Date.class);
        when(date.getTime())
            .thenReturn(100L);

        Time time = project.clockInAt(date);

        assertNotNull(time);
        assertNull(time.getId());
        assertEquals(1L, time.getProjectId());
        assertEquals(100L, time.getStart());
        assertEquals(0L, time.getStop());
    }

    @Test
    public void testClockOutAt() throws DomainException {
        Project project = new Project(1L, "Foo");
        Date date = new Date();

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);

        project.addTime(time);

        assertEquals(time, project.clockOutAt(date));
        verify(time, times(1))
            .clockOutAt(date);
    }

    @Test(expected = ClockActivityException.class)
    public void testClockOutAtWithoutActiveTime() throws DomainException {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(false);

        project.addTime(time);

        Date date = new Date();
        project.clockOutAt(date);
    }

    @Test(expected = ClockActivityException.class)
    public void testClockOutAtWithoutTime() throws DomainException {
        Project project = new Project(1L, "Foo");

        Date date = new Date();
        project.clockOutAt(date);
    }

    @Test
    public void testIfProjectIsActiveWithActiveTime() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);

        project.addTime(time);

        assertTrue(project.isActive());
    }

    @Test
    public void testIfProjectIsActiveWithoutActiveTime() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(false);

        project.addTime(time);

        assertFalse(project.isActive());
    }

    @Test
    public void testIfProjectIsActiveWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertFalse(project.isActive());
    }
}
