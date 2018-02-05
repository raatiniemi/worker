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

package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat;
import me.raatiniemi.worker.domain.util.FractionIntervalFormat;
import me.raatiniemi.worker.domain.util.HoursMinutesFormat;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupGetTimeSummaryWithDifferenceTest {
    private final String expected;
    private final HoursMinutesFormat formatter;
    private final TimesheetGroup item;

    public TimesheetGroupGetTimeSummaryWithDifferenceTest(
            String expected,
            HoursMinutesFormat formatter,
            TimesheetItem... times
    ) {
        this.expected = expected;
        this.formatter = formatter;

        TreeSet<TimesheetItem> items = new TreeSet<>(Arrays.asList(times));
        item = TimesheetGroup.build(new Date(), items);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1.00 (-7.00)",
                                new FractionIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(3600000)
                                }
                        },
                        {
                                "8.00",
                                new FractionIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(28800000)
                                }
                        },
                        {
                                "9.00 (+1.00)",
                                new FractionIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(32400000)
                                }
                        },
                        {
                                "9.12 (+1.12)",
                                new FractionIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(14380327),
                                        buildTimesheetItemWithInterval(18407820)
                                }
                        },
                        {
                                "8.77 (+0.77)",
                                new FractionIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(13956031),
                                        buildTimesheetItemWithInterval(17594386)
                                }
                        },
                        {
                                "7.87 (-0.13)",
                                new FractionIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(11661632),
                                        buildTimesheetItemWithInterval(16707601)
                                }
                        },
                        {
                                "1:00 (-7:00)",
                                new DigitalHoursMinutesIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(3600000)
                                }
                        },
                        {
                                "8:00",
                                new DigitalHoursMinutesIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(28800000)
                                }
                        },
                        {
                                "9:00 (+1:00)",
                                new DigitalHoursMinutesIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(32400000)
                                }
                        },
                        {
                                "9:07 (+1:07)",
                                new DigitalHoursMinutesIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(14380327),
                                        buildTimesheetItemWithInterval(18407820)
                                }
                        },
                        {
                                "8:46 (+0:46)",
                                new DigitalHoursMinutesIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(13956031),
                                        buildTimesheetItemWithInterval(17594386)
                                }
                        },
                        {
                                "7:52 (-0:08)",
                                new DigitalHoursMinutesIntervalFormat(),
                                new TimesheetItem[]{
                                        buildTimesheetItemWithInterval(11661632),
                                        buildTimesheetItemWithInterval(16707601)
                                }
                        }
                }
        );
    }

    private static TimesheetItem buildTimesheetItemWithInterval(long interval) {
        return TimesheetItem.with(
                TimeFactory.builder()
                        .startInMilliseconds(1L)
                        .stopInMilliseconds(interval)
                        .build()
        );
    }

    @Test
    public void getTimeSummaryWithDifference() {
        assertEquals(expected, item.getTimeSummaryWithDifference(formatter));
    }
}
