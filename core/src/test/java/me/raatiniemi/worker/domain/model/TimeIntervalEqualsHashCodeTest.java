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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.worker.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class TimeIntervalEqualsHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final TimeInterval timeInterval;
    private final Object compareTo;

    public TimeIntervalEqualsHashCodeTest(
            String message,
            Boolean expected,
            TimeInterval timeInterval,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.timeInterval = timeInterval;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .build();

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                timeInterval,
                                timeInterval
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                timeInterval,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                timeInterval,
                                ""
                        },
                        {
                                "With different project id",
                                Boolean.FALSE,
                                timeInterval,
                                TimeIntervalFactory.builder(2L)
                                        .build()
                        },
                        {
                                "With different id",
                                Boolean.FALSE,
                                timeInterval,
                                TimeIntervalFactory.builder()
                                        .id(2L)
                                        .build()
                        },
                        {
                                "With different start in milliseconds",
                                Boolean.FALSE,
                                timeInterval,
                                TimeIntervalFactory.builder()
                                        .startInMilliseconds(2L)
                                        .build()
                        },
                        {
                                "With different stop in milliseconds",
                                Boolean.FALSE,
                                timeInterval,
                                TimeIntervalFactory.builder()
                                        .stopInMilliseconds(1L)
                                        .build()
                        },
                        {
                                "With different register status",
                                Boolean.FALSE,
                                timeInterval,
                                TimeIntervalFactory.builder()
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
        assertTrue(message, timeInterval.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, timeInterval.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, timeInterval.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, timeInterval.hashCode() == compareTo.hashCode());
    }
}
