/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;

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
    public void Time_defaultValueFromDefaultConstructor() {
        Time time = new Time();

        assertNull(time.getId());
        assertNull(time.getProjectId());
        assertEquals(Long.valueOf(0L), time.getStart());
        assertEquals(Long.valueOf(0L), time.getStop());
        assertFalse(time.isRegistered());
    }

    @Test
    public void Time_defaultValueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L);

        assertEquals(Long.valueOf(1L), time.getId());
        assertEquals(Long.valueOf(2L), time.getProjectId());
        assertEquals(Long.valueOf(3L), time.getStart());
        assertEquals(Long.valueOf(4L), time.getStop());
        assertFalse(time.isRegistered());
    }

    @Test
    public void getProjectId_defaultValue() {
        Time time = new Time();

        assertNull(time.getProjectId());
    }

    @Test
    public void getProjectId_valueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L);

        assertEquals(Long.valueOf(2L), time.getProjectId());

        time = new Time(3L);

        assertEquals(Long.valueOf(3L), time.getProjectId());
    }

    @Test
    public void getProjectId_valueFromSetter() {
        Time time = new Time();
        time.setProjectId(1L);

        assertEquals(Long.valueOf(1L), time.getProjectId());
    }

    @Test
    public void getStart_defaultValue() {
        Time time = new Time();

        assertEquals(Long.valueOf(0L), time.getStop());
    }

    @Test
    public void getStart_valueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L);

        assertEquals(Long.valueOf(3L), time.getStart());
    }

    @Test
    public void getStart_valueFromSetter()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);

        assertEquals(Long.valueOf(1L), time.getStart());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void getStart_stopLessThanStart()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStop(1L);
        time.setStart(10L);
    }

    @Test
    public void getStop_defaultValue() {
        Time time = new Time();

        assertEquals(Long.valueOf(0L), time.getStop());
    }

    @Test
    public void getStop_valueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L);

        assertEquals(Long.valueOf(4L), time.getStop());
    }

    @Test
    public void getStop_valueFromSetter()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStop(1L);

        assertEquals(Long.valueOf(1L), time.getStop());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void setStop_stopLessThanStart()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(10L);
        time.setStop(1L);
    }

    @Test
    public void isRegistered_defaultValue() {
        Time time = new Time();

        assertFalse(time.isRegistered());
    }

    @Test
    public void isRegistered_valueFromSetter() {
        Time time = new Time();
        time.setRegistered(true);

        assertTrue(time.isRegistered());

        time.setRegistered(false);

        assertFalse(time.isRegistered());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void clockInAt_clockInAfterClockOut()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(2L);

        Time time = new Time();
        time.setStop(1L);
        time.clockInAt(date);

        assertEquals(Long.valueOf(2L), time.getStart());
    }

    @Test
    public void clockInAt_clockInBeforeClockOut()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time();
        time.setStop(2L);
        time.clockInAt(date);

        assertEquals(Long.valueOf(1L), time.getStart());
    }

    @Test
    public void clockInAt_whenInactive()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time();
        time.clockInAt(date);

        assertEquals(Long.valueOf(1L), time.getStart());
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void clockOutAt_clockOutBeforeClockIn()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time();
        time.setStart(2L);
        time.clockOutAt(date);
    }

    @Test
    public void clockOutAt_clockOutAfterClockIn()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(2L);

        Time time = new Time();
        time.setStart(1L);
        time.clockOutAt(date);

        assertEquals(Long.valueOf(2L), time.getStop());
    }

    @Test
    public void isActive_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);

        assertTrue(time.isActive());
    }

    @Test
    public void isActive_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);
        time.setStop(11L);

        assertFalse(time.isActive());
    }

    @Test
    public void getTime_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);

        assertEquals(Long.valueOf(0L), time.getTime());
    }

    @Test
    public void getTime_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);
        time.setStop(11L);

        assertEquals(Long.valueOf(10L), time.getTime());
    }

    @Test
    public void getInterval_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < time.getInterval());
    }

    @Test
    public void getInterval_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = new Time();
        time.setStart(1L);
        time.setStop(11L);

        assertEquals(Long.valueOf(10L), time.getInterval());
    }
}
