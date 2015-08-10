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
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimeTest {
    @Test
    public void clockInAt_ClockInBeforeClockOut_True()
        throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time();
        time.setStop(2L);
        time.clockInAt(date);

        assertEquals(Long.valueOf(1L), time.getStart());
    }

    @Test
    public void clockInAt_WhenInactive_True() {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time();
        time.clockInAt(date);

        assertEquals(Long.valueOf(1L), time.getStart());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void clockOutAt_ClockOutBeforeClockIn_ThrowException()
        throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time();
        time.setStart(2L);
        time.clockOutAt(date);
    }

    @Test
    public void clockOutAt_ClockOutAfterClockIn_True()
        throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(2L);

        Time time = new Time();
        time.setStart(1L);
        time.clockOutAt(date);

        assertEquals(Long.valueOf(2L), time.getStop());
    }

    @Test
    public void isActive_WhenActive_True() {
        Time time = new Time();
        time.setStart(1L);

        assertTrue(time.isActive());
    }

    @Test
    public void isActive_WhenInactive_False()
        throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);
        time.setStop(11L);

        assertFalse(time.isActive());
    }

    @Test
    public void getTime_WhenActive_True() {
        Time time = new Time();
        time.setStart(1L);

        assertEquals(Long.valueOf(0L), time.getTime());
    }

    @Test
    public void getTime_WhenInactive_True()
        throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);
        time.setStop(11L);

        assertEquals(Long.valueOf(10L), time.getTime());
    }

    @Test
    public void getInterval_WhenActive_True()
        throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < time.getInterval());
    }

    @Test
    public void getInterval_WhenInactive_True()
        throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);
        time.setStop(11L);

        assertEquals(Long.valueOf(10L), time.getInterval());
    }
}
