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

import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TimeTest {
    @Test
    public void Time_defaultValueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L, false);

        assertEquals(Long.valueOf(1L), time.getId());
        assertEquals(2L, time.getProjectId());
        assertEquals(3L, time.getStart());
        assertEquals(4L, time.getStop());
        assertFalse(time.isRegistered());
    }

    @Test
    public void getProjectId_valueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L, false);

        assertEquals(2L, time.getProjectId());
    }

    @Test
    public void getStart_valueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L, false);

        assertEquals(3L, time.getStart());
    }

    @Test
    public void getStop_valueFromConstructor()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L, false);

        assertEquals(4L, time.getStop());
    }

    @Test
    public void markAsRegistered()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 1L, false);

        assertFalse(time.isRegistered());
        time = time.markAsRegistered();
        assertTrue(time.isRegistered());
    }

    @Test
    public void markAsRegistered_alreadyRegistered()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 1L, true);

        assertTrue(time.isRegistered());
        time = time.markAsRegistered();
        assertTrue(time.isRegistered());
    }

    @Test
    public void unmarkRegistered()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 1L, true);

        assertTrue(time.isRegistered());
        time = time.unmarkRegistered();
        assertFalse(time.isRegistered());
    }

    @Test
    public void unmarkRegistered_notRegistered()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 1L, false);

        assertFalse(time.isRegistered());
        time = time.unmarkRegistered();
        assertFalse(time.isRegistered());
    }

    @Test(expected = NullPointerException.class)
    public void clockOutAt_withNullDate()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1, 1, 0, false);
        time.clockOutAt(null);
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void clockOutAt_clockOutBeforeClockIn()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(1L);

        Time time = new Time(1L, 1L, 2L, 0L, false);
        time.clockOutAt(date);
    }

    @Test
    public void clockOutAt_clockOutAfterClockIn()
            throws ClockOutBeforeClockInException {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(2L);

        Time time = new Time(1L, 1L, 1L, 0L, false);
        time = time.clockOutAt(date);

        assertEquals(2L, time.getStop());
    }

    @Test
    public void isActive_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 0L, false);

        assertTrue(time.isActive());
    }

    @Test
    public void isActive_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 11L, false);

        assertFalse(time.isActive());
    }

    @Test
    public void getTime_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 0L, false);

        assertEquals(0L, time.getTime());
    }

    @Test
    public void getTime_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 11L, false);

        assertEquals(10L, time.getTime());
    }

    @Test
    public void getInterval_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 0L, false);

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < time.getInterval());
    }

    @Test
    public void getInterval_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 1L, 1L, 11L, false);

        assertEquals(10L, time.getInterval());
    }
}
