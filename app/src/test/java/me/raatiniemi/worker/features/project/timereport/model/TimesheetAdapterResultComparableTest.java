/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timereport.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimesheetItem;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetAdapterResultComparableTest {
    private final String message;
    private final int expected;
    private final TimesheetAdapterResult lhs;
    private final TimesheetAdapterResult rhs;

    public TimesheetAdapterResultComparableTest(
            String message,
            int expected,
            TimesheetAdapterResult lhs,
            TimesheetAdapterResult rhs
    ) {
        this.message = message;
        this.expected = expected;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        TimeInterval timeInterval = TimeInterval.builder(1L).build();
        TimesheetItem item = TimesheetItem.with(timeInterval);

        return Arrays.asList(
                new Object[][]{
                        {
                                "Equal",
                                0,
                                new TimesheetAdapterResult(0, 0, item),
                                new TimesheetAdapterResult(0, 0, item)
                        },
                        {
                                "lhs is more than rhs (group)",
                                1,
                                new TimesheetAdapterResult(1, 0, item),
                                new TimesheetAdapterResult(0, 0, item)
                        },
                        {
                                "lhs is less than rhs (group)",
                                -1,
                                new TimesheetAdapterResult(0, 0, item),
                                new TimesheetAdapterResult(1, 0, item)
                        },
                        {
                                "lhs is more than rhs (child)",
                                1,
                                new TimesheetAdapterResult(0, 1, item),
                                new TimesheetAdapterResult(0, 0, item)
                        },
                        {
                                "lhs is less than rhs (child)",
                                -1,
                                new TimesheetAdapterResult(0, 0, item),
                                new TimesheetAdapterResult(0, 1, item)
                        }
                }
        );
    }

    @Test
    public void compareTo() {
        assertEquals(message, expected, lhs.compareTo(rhs));
    }
}
