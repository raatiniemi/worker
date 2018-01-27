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

package me.raatiniemi.worker.domain.comparator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetItemComparatorTest {
    private final String message;
    private final int expected;
    private final TimesheetItem lhs;
    private final TimesheetItem rhs;

    public TimesheetItemComparatorTest(
            String message,
            int expected,
            TimesheetItem lhs,
            TimesheetItem rhs
    ) {
        this.message = message;
        this.expected = expected;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Parameters
    public static Collection<Object[]> getParameters() throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Active with lhs.start = rhs.start",
                                0,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(0L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(0L)
                                                .build()
                                )
                        },
                        {
                                "Active with lhs.start > rhs.start",
                                -1,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(2L)
                                                .stopInMilliseconds(0L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(0L)
                                                .build()
                                )
                        },
                        {
                                "Active with lhs.start < rhs.start",
                                1,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(0L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(2L)
                                                .stopInMilliseconds(0L)
                                                .build()
                                )
                        },
                        {
                                "lhs == rhs",
                                0,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                )
                        },
                        {
                                "lhs.start > rhs.start",
                                -1,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(2L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                )
                        },
                        {
                                "lhs.start < rhs.start",
                                1,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(2L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                )
                        },
                        {
                                "lhs.stop > rhs.stop",
                                -1,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(1L)
                                                .build()
                                )
                        },
                        {
                                "lhs.stop < rhs.stop",
                                1,
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(1L)
                                                .build()
                                ),
                                TimesheetItem.with(
                                        TimeFactory.builder()
                                                .startInMilliseconds(1L)
                                                .stopInMilliseconds(2L)
                                                .build()
                                )
                        }
                }
        );
    }

    @Test
    public void compareTo() {
        assertEquals(message, expected, lhs.compareTo(rhs));
    }
}
