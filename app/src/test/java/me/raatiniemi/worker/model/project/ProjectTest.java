package me.raatiniemi.worker.model.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.domain.ClockActivityException;
import me.raatiniemi.worker.model.time.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
    public void testSetDescription() {
        Project project = new Project(1L, null);
        project.setDescription("Foobar");

        assertEquals("Foobar", project.getDescription());
    }

    @Test
    public void testGetArchived() {
        Project project = new Project(1L, null);

        Long archived = 0L;

        assertEquals(archived, project.getArchived());
    }

    @Test
    public void testSetArchived() {
        Project project = new Project(1L, null);

        Long archived = 1L;
        project.setArchived(archived);

        assertEquals(archived, project.getArchived());
    }

    @Test
    public void testGetTime() {
        Project project = new Project(1L, "Foo");

        assertTrue(null != project.getTime());
        assertTrue(0 == project.getTime().size());
    }

    @Test
    public void testAddTime() {
        try {
            Project project = new Project(1L, "Foo");

            assertTrue(0 == project.getTime().size());
            project.addTime(new Time(project.getId()));
            assertTrue(1 == project.getTime().size());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testSummarizeSingleTime() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 0L, 60000L));

            assertEquals(60000L, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testSummarizeWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertEquals(0L, project.summarizeTime());
    }

    @Test
    public void testSummarizeTimeWithActiveTime() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 0L, 60000L));
            project.addTime(new Time(null, project.getId(), 60000L, 0L));

            assertEquals(60000L, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testSummarizeTimeWithRoundUp() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 0L, 60000L));
            project.addTime(new Time(null, project.getId(), 60000L, 90000L));

            assertEquals(90000L, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testSummarizeTimeWithRoundDown() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 0L, 60000L));
            project.addTime(new Time(null, project.getId(), 60000L, 89000L));

            assertEquals(89000L, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testSummarizeTimeWithHours() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 3600000L, 7200000L));
            project.addTime(new Time(null, project.getId(), 7200000L, 9000000L));

            assertEquals(5400000L, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testClockOut() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 60000L, 0L));

            assertTrue(null != project.clockOut());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testClockOutWithoutActiveTime() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 60000L, 120000L));

            assertTrue(null == project.clockOut());
        } catch (ClockActivityException e) {
            assertTrue(true);
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testClockOutWithoutTime() {
        try {
            Project project = new Project(1L, "Foo");

            project.clockOut();
        } catch (ClockActivityException e) {
            assertTrue(true);
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testIsActiveWithActiveTime() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 60000L, 0L));

            assertTrue(project.isActive());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testIsActiveWithoutActiveTime() {
        try {
            Project project = new Project(1L, "Foo");
            project.addTime(new Time(null, project.getId(), 60000L, 120000L));

            assertFalse(project.isActive());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testIsActiveWithoutTime() {
        Project project = new Project(1L, "Foo");

        assertFalse(project.isActive());
    }
}
