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
import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemGetTimeSummaryWithDifferenceTest {
    private final String expected;
    private final TimesheetChildItem[] childItems;

    public TimesheetGroupItemGetTimeSummaryWithDifferenceTest(
            String expected,
            TimesheetChildItem... childItems
    ) {
        this.expected = expected;
        this.childItems = childItems;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1.00 (-7.00)",
                                new TimesheetChildItem[]{
                                        buildTimesheetChildItem(3600000)
                                }
                        },
                        {
                                "8.00",
                                new TimesheetChildItem[]{
                                        buildTimesheetChildItem(28800000)
                                }
                        },
                        {
                                "9.00 (+1.00)",
                                new TimesheetChildItem[]{
                                        buildTimesheetChildItem(32400000)
                                }
                        },
                        {
                                "9.12 (+1.12)",
                                new TimesheetChildItem[]{
                                        buildTimesheetChildItem(14380327),
                                        buildTimesheetChildItem(18407820)
                                }
                        },
                        {
                                "8.76 (+0.76)",
                                new TimesheetChildItem[]{
                                        buildTimesheetChildItem(13956031),
                                        buildTimesheetChildItem(17594386)
                                }
                        },
                        {
                                "7.86 (-0.14)",
                                new TimesheetChildItem[]{
                                        buildTimesheetChildItem(11661632),
                                        buildTimesheetChildItem(16707601)
                                }
                        }
                }
        );
    }

    private static TimesheetChildItem buildTimesheetChildItem(long interval)
            throws ClockOutBeforeClockInException {
        Time time = Time.builder(1L)
                .startInMilliseconds(1L)
                .stopInMilliseconds(interval)
                .build();

        return new TimesheetChildItem(time);
    }

    @Test
    public void getTimeSummaryWithDifference() {
        TimesheetGroupItem groupItem = new TimesheetGroupItem(new Date());
        for (TimesheetChildItem childItem : childItems) {
            groupItem.add(childItem);
        }

        assertEquals(expected, groupItem.getTimeSummaryWithDifference());
    }
}
