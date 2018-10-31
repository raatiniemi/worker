/*
 * Copyright (C) 2017 Worker Project
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
import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TimeTest {
    @Test
    public void Builder_withDefaultValues() {
        TimeInterval timeInterval = TimeIntervalFactory.builder(1L)
                .build();

        assertNull(timeInterval.getId());
        assertEquals(1L, timeInterval.getProjectId());
        assertEquals(0L, timeInterval.getStartInMilliseconds());
        assertEquals(0L, timeInterval.getStopInMilliseconds());
        assertFalse(timeInterval.isRegistered());
    }

    @Test
    public void Builder_withValues() {
        TimeInterval timeInterval = TimeIntervalFactory.builder(1L)
                .id(2L)
                .startInMilliseconds(3L)
                .stopInMilliseconds(4L)
                .register()
                .build();

        assertEquals(Long.valueOf(2L), timeInterval.getId());
        assertEquals(1L, timeInterval.getProjectId());
        assertEquals(3L, timeInterval.getStartInMilliseconds());
        assertEquals(4L, timeInterval.getStopInMilliseconds());
        assertTrue(timeInterval.isRegistered());
    }

    @Test
    public void markAsRegistered() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .build();

        assertFalse(timeInterval.isRegistered());
        timeInterval = timeInterval.markAsRegistered();
        assertTrue(timeInterval.isRegistered());
    }

    @Test
    public void markAsRegistered_alreadyRegistered() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .register()
                .build();

        assertTrue(timeInterval.isRegistered());
        timeInterval = timeInterval.markAsRegistered();
        assertTrue(timeInterval.isRegistered());
    }

    @Test
    public void unmarkRegistered() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .register()
                .build();

        assertTrue(timeInterval.isRegistered());
        timeInterval = timeInterval.unmarkRegistered();
        assertFalse(timeInterval.isRegistered());
    }

    @Test
    public void unmarkRegistered_notRegistered() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .build();

        assertFalse(timeInterval.isRegistered());
        timeInterval = timeInterval.unmarkRegistered();
        assertFalse(timeInterval.isRegistered());
    }

    @Test(expected = NullPointerException.class)
    public void clockOutAt_withNullDate() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .build();

        timeInterval.clockOutAt(null);
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void clockOutAt_clockOutBeforeClockIn() {
        Date date = new Date();
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .startInMilliseconds(date.getTime() + 1)
                .build();

        timeInterval.clockOutAt(date);
    }

    @Test
    public void clockOutAt() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .build();
        Date date = new Date();

        timeInterval = timeInterval.clockOutAt(date);

        assertEquals(date.getTime(), timeInterval.getStopInMilliseconds());
        assertFalse(timeInterval.isRegistered());
    }

    @Test
    public void clockOutAt_withRegistered() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .register()
                .build();
        Date date = new Date();

        timeInterval = timeInterval.clockOutAt(date);

        assertEquals(date.getTime(), timeInterval.getStopInMilliseconds());
        assertTrue(timeInterval.isRegistered());
    }

    @Test
    public void isActive_whenActive() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .build();

        assertTrue(timeInterval.isActive());
    }

    @Test
    public void isActive_whenInactive() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .stopInMilliseconds(1L)
                .build();

        assertFalse(timeInterval.isActive());
    }

    @Test
    public void getTime_whenActive() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .startInMilliseconds(1L)
                .build();

        assertEquals(0L, timeInterval.getTime());
    }

    @Test
    public void getTime_whenInactive() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .startInMilliseconds(1L)
                .stopInMilliseconds(11L)
                .build();

        assertEquals(10L, timeInterval.getTime());
    }

    @Test
    public void getInterval_whenActive() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .startInMilliseconds(1L)
                .build();

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < timeInterval.getInterval());
    }

    @Test
    public void getInterval_whenInactive() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .startInMilliseconds(1L)
                .stopInMilliseconds(11L)
                .build();

        assertEquals(10L, timeInterval.getInterval());
    }
}
