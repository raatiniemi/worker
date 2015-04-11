package me.raatiniemi.worker.domain;

import android.app.Application;
import android.test.ApplicationTestCase;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.domain.ClockActivityException;

public class ProjectTest extends ApplicationTestCase<Application>
{
    public ProjectTest()
    {
        super(Application.class);
    }

    public void testGetName()
    {
        Project project = new Project((long) 1, "Foo");

        assertEquals("Foo", project.getName());
    }

    public void testSetName()
    {
        Project project = new Project((long) 1, null);
        project.setName("Foo");

        assertEquals("Foo", project.getName());
    }

    public void testSetDescription()
    {
        Project project = new Project((long) 1, null);
        project.setDescription("Foobar");

        assertEquals("Foobar", project.getDescription());
    }

    public void testSetArchived()
    {
        Project project = new Project((long) 1, null);

        Long archived = (long) 1;
        project.setArchived(archived);

        assertEquals(archived, project.getArchived());
    }

    public void testGetTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertTrue(project.getTime() != null);
        assertTrue(project.getTime().size() == 0);
    }

    public void testAddTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");

            assertTrue(project.getTime().size() == 0);
            project.addTime(new Time(project.getId()));
            assertTrue(project.getTime().size() == 1);
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testSummarizeSingleTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 0, 60000));

            assertEquals((long) 60000, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testSummarizeWithoutTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertEquals((long) 0, project.summarizeTime());
    }

    public void testSummarizeTimeWithActiveTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 0, 60000));
            project.addTime(new Time(null, project.getId(), 60000, 0));

            assertEquals((long) 60000, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testSummarizeTimeWithRoundUp()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 0, 60000));
            project.addTime(new Time(null, project.getId(), 60000, 90000));

            assertEquals((long) 90000, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testSummarizeTimeWithRoundDown()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 0, 60000));
            project.addTime(new Time(null, project.getId(), 60000, 89000));

            assertEquals((long) 89000, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testSummarizeTimeWithHours()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 3600000, 7200000));
            project.addTime(new Time(null, project.getId(), 7200000, 9000000));

            assertEquals((long) 5400000, project.summarizeTime());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testClockOut()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 60000, 0));

            assertTrue(project.clockOut() != null);
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testClockOutWithoutActiveTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 60000, 120000));

            assertTrue(project.clockOut() == null);
        } catch (ClockActivityException e) {
            assertTrue(true);
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testClockOutWithoutTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");

            project.clockOut();
        } catch (ClockActivityException e) {
            assertTrue(true);
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testIsActiveWithActiveTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 60000, 0));

            assertTrue(project.isActive());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testIsActiveWithoutActiveTime()
    {
        try {
            Project project = new Project((long) 1, "Foo");
            project.addTime(new Time(null, project.getId(), 60000, 120000));

            assertFalse(project.isActive());
        } catch (DomainException e) {
            assertFalse(true);
        }
    }

    public void testIsActiveWithoutTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertFalse(project.isActive());
    }
}
