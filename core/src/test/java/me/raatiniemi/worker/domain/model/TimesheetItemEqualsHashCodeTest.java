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

import static junit.framework.Assert.assertEquals;
import static me.raatiniemi.worker.util.NullUtil.isNull;
import static org.junit.Assert.assertNotEquals;

@RunWith(Parameterized.class)
public class TimesheetItemEqualsHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final TimesheetItem timesheetItem;
    private final Object compareTo;

    public TimesheetItemEqualsHashCodeTest(
            String message,
            Boolean expected,
            TimesheetItem timesheetItem,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.timesheetItem = timesheetItem;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        TimesheetItem timesheetItem = TimesheetItem.with(
                TimeInterval.builder(1L)
                        .build()
        );

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                timesheetItem,
                                timesheetItem
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                timesheetItem,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                timesheetItem,
                                ""
                        },
                        {
                                "With different project id",
                                Boolean.FALSE,
                                timesheetItem,
                                TimesheetItem.with(
                                        TimeInterval.builder(2L)
                                                .build()
                                )
                        },
                        {
                                "With different id",
                                Boolean.FALSE,
                                timesheetItem,
                                TimesheetItem.with(
                                        TimeInterval.builder(1L)
                                                .id(2L)
                                                .build()
                                )
                        },
                        {
                                "With different start in milliseconds",
                                Boolean.FALSE,
                                timesheetItem,
                                TimesheetItem.with(
                                        TimeInterval.builder(1L)
                                                .startInMilliseconds(2L)
                                                .build()
                                )
                        },
                        {
                                "With different stop in milliseconds",
                                Boolean.FALSE,
                                timesheetItem,
                                TimesheetItem.with(
                                        TimeInterval.builder(1L)
                                                .stopInMilliseconds(1L)
                                                .build()
                                )
                        },
                        {
                                "With different register status",
                                Boolean.FALSE,
                                timesheetItem,
                                TimesheetItem.with(
                                        TimeInterval.builder(1L)
                                                .register()
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
            return;
        }

        assertNotEqual();
    }

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertEquals(message, timesheetItem, compareTo);

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, timesheetItem.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotEquals(message, timesheetItem, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertNotEquals(message, timesheetItem.hashCode(), compareTo.hashCode());
    }
}
