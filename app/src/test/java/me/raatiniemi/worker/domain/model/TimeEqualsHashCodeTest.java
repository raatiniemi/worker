/*
 * Copyright (C) 2016 Worker Project
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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class TimeEqualsHashCodeTest {
    private String message;
    private Boolean expected;
    private Time time;
    private Object compareTo;

    public TimeEqualsHashCodeTest(
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
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
        Time time = Time.builder(1L)
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
                                Time.builder(2L)
                                        .id(2L)
                                        .startInMilliseconds(3L)
                                        .stopInMilliseconds(4L)
                                        .build()
                        },
                        {
                                "With different id",
                                Boolean.FALSE,
                                time,
                                Time.builder(1L)
                                        .id(1L)
                                        .startInMilliseconds(3L)
                                        .stopInMilliseconds(4L)
                                        .build()
                        },
                        {
                                "With different start in milliseconds",
                                Boolean.FALSE,
                                time,
                                Time.builder(1L)
                                        .id(2L)
                                        .startInMilliseconds(2L)
                                        .stopInMilliseconds(4L)
                                        .build()
                        },
                        {
                                "With different stop in milliseconds",
                                Boolean.FALSE,
                                time,
                                Time.builder(1L)
                                        .id(2L)
                                        .startInMilliseconds(3L)
                                        .stopInMilliseconds(3L)
                                        .build()
                        },
                        {
                                "With different register status",
                                Boolean.FALSE,
                                time,
                                Time.builder(1L)
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
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, time.hashCode() == compareTo.hashCode());
    }
}
