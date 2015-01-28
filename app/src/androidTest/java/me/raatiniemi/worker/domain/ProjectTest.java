package me.raatiniemi.worker.domain;

import android.app.Application;
import android.test.ApplicationTestCase;

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

    public void testGetTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertTrue(project.getTime() != null);
        assertTrue(project.getTime().size() == 0);
    }

    public void testAddTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertTrue(project.getTime().size() == 0);
        project.addTime(new Time(project.getId()));
        assertTrue(project.getTime().size() == 1);
    }

    public void testSummarizeSingleTime()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 0, 60000));

        assertEquals("0h 1m", project.summarizeTime());
    }

    public void testSummarizeWithoutTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertEquals("0h 0m", project.summarizeTime());
    }

    public void testSummarizeTimeWithActiveTime()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 0, 60000));
        project.addTime(new Time(null, project.getId(), 60000, 0));

        assertEquals("0h 1m", project.summarizeTime());
    }

    public void testSummarizeTimeWithRoundUp()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 0, 60000));
        project.addTime(new Time(null, project.getId(), 60000, 90000));

        assertEquals("0h 2m", project.summarizeTime());
    }

    public void testSummarizeTimeWithRoundDown()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 0, 60000));
        project.addTime(new Time(null, project.getId(), 60000, 89000));

        assertEquals("0h 1m", project.summarizeTime());
    }

    public void testSummarizeTimeWithHours()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 3600000, 7200000));
        project.addTime(new Time(null, project.getId(), 7200000, 9000000));

        assertEquals("1h 30m", project.summarizeTime());
    }

    public void testClockOut()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 60000, 0));

        assertTrue(project.clockOut() != null);
    }

    public void testClockOutWithoutActiveTime()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 60000, 120000));

        assertTrue(project.clockOut() == null);
    }

    public void testClockOutWithoutTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertTrue(project.clockOut() == null);
    }

    public void testIsActiveWithActiveTime()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 60000, 0));

        assertTrue(project.isActive());
    }

    public void testIsActiveWithoutActiveTime()
    {
        Project project = new Project((long) 1, "Foo");
        project.addTime(new Time(null, project.getId(), 60000, 120000));

        assertFalse(project.isActive());
    }

    public void testIsActiveWithoutTime()
    {
        Project project = new Project((long) 1, "Foo");

        assertFalse(project.isActive());
    }
}
