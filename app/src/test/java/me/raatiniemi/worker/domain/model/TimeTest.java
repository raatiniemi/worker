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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TimeTest {
    private Time.Builder createTimeBuilder() {
        return new Time.Builder(1L);
    }

    @Test
    public void Builder_withDefaultValues()
            throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(1L)
                .build();

        assertNull(time.getId());
        assertEquals(1L, time.getProjectId());
        assertEquals(0L, time.getStartInMilliseconds());
        assertEquals(0L, time.getStopInMilliseconds());
        assertFalse(time.isRegistered());
    }

    @Test
    public void Builder_withValues()
            throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(1L)
                .id(2L)
                .startInMilliseconds(3L)
                .stopInMilliseconds(4L)
                .register()
                .build();

        assertEquals(Long.valueOf(2L), time.getId());
        assertEquals(1L, time.getProjectId());
        assertEquals(3L, time.getStartInMilliseconds());
        assertEquals(4L, time.getStopInMilliseconds());
        assertTrue(time.isRegistered());
    }

    @Test
    public void markAsRegistered()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .build();

        assertFalse(time.isRegistered());
        time = time.markAsRegistered();
        assertTrue(time.isRegistered());
    }

    @Test
    public void markAsRegistered_alreadyRegistered()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .register()
                .build();

        assertTrue(time.isRegistered());
        time = time.markAsRegistered();
        assertTrue(time.isRegistered());
    }

    @Test
    public void unmarkRegistered()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .register()
                .build();

        assertTrue(time.isRegistered());
        time = time.unmarkRegistered();
        assertFalse(time.isRegistered());
    }

    @Test
    public void unmarkRegistered_notRegistered()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .build();

        assertFalse(time.isRegistered());
        time = time.unmarkRegistered();
        assertFalse(time.isRegistered());
    }

    @Test(expected = NullPointerException.class)
    public void clockOutAt_withNullDate()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .build();

        time.clockOutAt(null);
    }

    @Test(expected = ClockOutBeforeClockInException.class)
    public void clockOutAt_clockOutBeforeClockIn()
            throws ClockOutBeforeClockInException {
        Date date = new Date();

        Time time = createTimeBuilder()
                .startInMilliseconds(date.getTime() + 1)
                .build();

        time.clockOutAt(date);
    }

    @Test
    public void clockOutAt()
            throws ClockOutBeforeClockInException {
        Date date = new Date();

        Time time = createTimeBuilder()
                .build();

        time = time.clockOutAt(date);
        assertEquals(date.getTime(), time.getStopInMilliseconds());
        assertFalse(time.isRegistered());
    }

    @Test
    public void clockOutAt_withRegistered()
            throws ClockOutBeforeClockInException {
        Date date = new Date();

        Time time = createTimeBuilder()
                .register()
                .build();

        time = time.clockOutAt(date);
        assertEquals(date.getTime(), time.getStopInMilliseconds());
        assertTrue(time.isRegistered());
    }

    @Test
    public void isActive_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .build();

        assertTrue(time.isActive());
    }

    @Test
    public void isActive_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .stopInMilliseconds(1L)
                .build();

        assertFalse(time.isActive());
    }

    @Test
    public void getTime_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .build();

        assertEquals(0L, time.getTime());
    }

    @Test
    public void getTime_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .startInMilliseconds(1L)
                .stopInMilliseconds(11L)
                .build();

        assertEquals(10L, time.getTime());
    }

    @Test
    public void getInterval_whenActive()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .startInMilliseconds(1L)
                .build();

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < time.getInterval());
    }

    @Test
    public void getInterval_whenInactive()
            throws ClockOutBeforeClockInException {
        Time time = createTimeBuilder()
                .startInMilliseconds(1L)
                .stopInMilliseconds(11L)
                .build();

        assertEquals(10L, time.getInterval());
    }

    @RunWith(Parameterized.class)
    public static class TimeTest_equals {
        private String message;
        private Boolean expected;
        private Time time;
        private Object compareTo;

        public TimeTest_equals(
                String message,
                Boolean expected,
                Time time,
                Object compareTo
        ) {
            this.message = message;
            this.expected = expected;
            this.time = time;
            this.compareTo = compareTo;
        }

        @Parameters
        public static Collection<Object[]> parameters()
                throws ClockOutBeforeClockInException {
            Time time = new Time.Builder(1L)
                    .id(2L)
                    .startInMilliseconds(3L)
                    .stopInMilliseconds(4L)
                    .build();

            return Arrays.asList(
                    new Object[][]{
                            {
                                    "With same instance",
                                    Boolean.TRUE,
                                    time,
                                    time
                            },
                            {
                                    "With null",
                                    Boolean.FALSE,
                                    time,
                                    null
                            },
                            {
                                    "With incompatible object",
                                    Boolean.FALSE,
                                    time,
                                    ""
                            },
                            {
                                    "With different project id",
                                    Boolean.FALSE,
                                    time,
                                    new Time.Builder(2L)
                                            .id(2L)
                                            .startInMilliseconds(3L)
                                            .stopInMilliseconds(4L)
                                            .build()
                            },
                            {
                                    "With different id",
                                    Boolean.FALSE,
                                    time,
                                    new Time.Builder(1L)
                                            .id(1L)
                                            .startInMilliseconds(3L)
                                            .stopInMilliseconds(4L)
                                            .build()
                            },
                            {
                                    "With different start in milliseconds",
                                    Boolean.FALSE,
                                    time,
                                    new Time.Builder(1L)
                                            .id(2L)
                                            .startInMilliseconds(2L)
                                            .stopInMilliseconds(4L)
                                            .build()
                            },
                            {
                                    "With different stop in milliseconds",
                                    Boolean.FALSE,
                                    time,
                                    new Time.Builder(1L)
                                            .id(2L)
                                            .startInMilliseconds(3L)
                                            .stopInMilliseconds(3L)
                                            .build()
                            },
                            {
                                    "With different register status",
                                    Boolean.FALSE,
                                    time,
                                    new Time.Builder(1L)
                                            .id(2L)
                                            .startInMilliseconds(3L)
                                            .stopInMilliseconds(4L)
                                            .register()
                                            .build()
                            }
                    }
            );
        }

        @Test
        public void equals() {
            if (shouldBeEqual()) {
                assertEqual();
                return;
            }

            assertNotEqual();
        }

        private Boolean shouldBeEqual() {
            return expected;
        }

        private void assertEqual() {
            assertTrue(message, time.equals(compareTo));

            validateHashCodeWhenEqual();
        }

        private void validateHashCodeWhenEqual() {
            assertTrue(message, time.hashCode() == compareTo.hashCode());
        }

        private void assertNotEqual() {
            assertFalse(message, time.equals(compareTo));

            validateHashCodeWhenNotEqual();
        }

        private void validateHashCodeWhenNotEqual() {
            if (null == compareTo) {
                return;
            }

            assertFalse(message, time.hashCode() == compareTo.hashCode());
        }
    }
}
