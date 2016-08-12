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

package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TimeInAdapterResultTest {
    private String message;
    private Boolean expected;
    private TimeInAdapterResult timeInAdapterResult;
    private Object compareTo;

    public TimeInAdapterResultTest(
            String message,
            Boolean expected,
            TimeInAdapterResult timeInAdapterResult,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.timeInAdapterResult = timeInAdapterResult;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> parameters()
            throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(0)
                .build();

        TimeInAdapterResult timeInAdapterResult = TimeInAdapterResult.build(0, 0, time);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                timeInAdapterResult,
                                timeInAdapterResult
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                ""
                        },
                        {
                                "With different group position",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                TimeInAdapterResult.build(1, 0, time)
                        },
                        {
                                "With different child position",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                TimeInAdapterResult.build(0, 1, time)
                        },
                        {
                                "With different time object",
                                Boolean.TRUE,
                                timeInAdapterResult,
                                TimeInAdapterResult.build(
                                        0,
                                        0,
                                        new Time.Builder(0)
                                                .build()
                                )
                        }
                }
        );
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            validateHashCodeWhenEqual();
            return;
        }

        assertNotEqual();
        validateHashCodeWhenNotEqual();
    }

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertTrue(message, timeInAdapterResult.equals(compareTo));
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, timeInAdapterResult.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, timeInAdapterResult.equals(compareTo));
    }

    private void validateHashCodeWhenNotEqual() {
        if (null == compareTo) {
            return;
        }

        assertFalse(message, timeInAdapterResult.hashCode() == compareTo.hashCode());
    }
}
