package me.raatiniemi.worker.model.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

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
    public void testGetName() {
        Project project = new Project(1L, "Foo");

        assertEquals("Foo", project.getName());
    }

    @Test
    public void testRenameProject() {
        Project project = new Project(1L, "Foo");
        project.setName("Bar");

        assertEquals("Bar", project.getName());
    }

    @Test
    public void testSetAndGetDescription() {
        Project project = new Project(1L, "Foo");
        project.setDescription("Bar");

        assertEquals("Bar", project.getDescription());
    }

    @Test
    public void testGetEmptyDescription() {
        Project project = new Project(1L, "Foo");

        assertNull(project.getDescription());
    }

    @Test
    public void testGetArchived() {
        Project project = new Project(1L, "Foo");

        // Asserting against '0L' directly will result
        // in an ambiguous method call.
        Long archived = 0L;

        assertEquals(archived, project.getArchived());
    }

    @Test
    public void testSetArchived() {
        Project project = new Project(1L, "Foo");

        // Asserting against '1L' directly will result
        // in an ambiguous method call.
        Long archived = 1L;
        project.setArchived(archived);

        assertEquals(archived, project.getArchived());
    }

    @Test
    public void testGetEmptyTime() {
        Project project = new Project(1L, "Foo");

        assertNotNull(project.getTime());
        assertEquals(project.getTime().size(), 0);
    }

    @Test
    public void testAddTime() {
        Project project = new Project(1L, "Foo");

        assertEquals(project.getTime().size(), 0);

        project.addTime(mock(Time.class));

        assertEquals(project.getTime().size(), 1);
    }

    @Test
    public void testSummarizeSingleTime() {
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
    public void testClockOut() throws DomainException {
        Project project = new Project(1L, "Foo");
        Date date = new Date();

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);

        project.addTime(time);

        assertEquals(time, project.clockOutAt(date));
        verify(time, times(1)).clockOutAt(date);
    }

    @Test(expected = ClockActivityException.class)
    public void testClockOutWithoutActiveTime() throws DomainException {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(false);

        project.clockOut();
    }

    @Test(expected = ClockActivityException.class)
    public void testClockOutWithoutTime() throws DomainException {
        Project project = new Project(1L, "Foo");

        project.clockOut();
    }

    @Test
    public void testIsActiveWithActiveTime() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(true);

        project.addTime(time);

        assertTrue(project.isActive());
    }

    @Test
    public void testIsActiveWithoutActiveTime() {
        Project project = new Project(1L, "Foo");

        Time time = mock(Time.class);
        when(time.isActive())
            .thenReturn(false);

        project.addTime(time);

        assertFalse(project.isActive());
    }

    @Test
    public void testIsActiveWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertFalse(project.isActive());
    }
}
