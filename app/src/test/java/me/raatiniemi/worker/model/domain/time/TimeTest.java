package me.raatiniemi.worker.model.domain.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.exception.domain.ClockOutBeforeClockInException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimeTest {
    @Test
    public void testConstructor() throws ClockOutBeforeClockInException {
        Long id = 1L;
        Long projectId = 2L;

        Time time = new Time(id, projectId, 3L, 4L);
        assertEquals(id, time.getId());
        assertEquals(projectId, time.getProjectId());
        assertEquals(Long.valueOf(3L), time.getStart());
        assertEquals(Long.valueOf(4L), time.getStop());
    }

    @Test
    public void testConstructorWithProjectId() throws ClockOutBeforeClockInException {
        Long projectId = 2L;

        Time time = new Time(projectId);

        assertNull(time.getId());
        assertEquals(projectId, time.getProjectId());
        assertEquals(Long.valueOf(0L), time.getStop());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void testConstructorWithStopBeforeStart() throws ClockOutBeforeClockInException {
        new Time(1L, 2L, 3L, 1L);
    }

    @Test
    public void testClockInAt() throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime())
            .thenReturn(3L);

        Time time = new Time(1L);
        time.clockInAt(date);

        assertEquals(Long.valueOf(3L), time.getStart());
    }

    @Test
    public void testClockOutAt() throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime())
            .thenReturn(3L);

        Time time = new Time(1L);
        time.setStart(2L);
        time.clockOutAt(date);

        assertEquals(Long.valueOf(3L), time.getStop());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void testClockOutBeforeClockIn() throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime())
            .thenReturn(3L);

        Time time = new Time(1L);
        time.setStart(4L);
        time.clockOutAt(date);
    }

    @Test
    public void testIsActive() throws ClockOutBeforeClockInException {
        Time time = new Time(1L);

        assertTrue(time.isActive());
    }

    @Test
    public void testIsNotActive() throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L);

        assertFalse(time.isActive());
    }

    @Test
    public void testGetTimeWhenActive() throws ClockOutBeforeClockInException {
        Time time = new Time(1L);

        assertEquals(Long.valueOf(0L), time.getTime());
    }

    @Test
    public void testGetTime() throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 11L);

        assertEquals(Long.valueOf(10L), time.getTime());
    }

    @Test
    public void testGetInterval() throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 11L);

        assertEquals(Long.valueOf(10L), time.getInterval());
    }

    @Test
    public void testGetIntervalWhenActive() throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 0L);

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < time.getInterval());
    }
}
