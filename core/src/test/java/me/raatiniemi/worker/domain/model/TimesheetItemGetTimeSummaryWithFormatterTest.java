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

import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat;
import me.raatiniemi.worker.domain.util.FractionIntervalFormat;
import me.raatiniemi.worker.domain.util.HoursMinutesFormat;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetItemGetTimeSummaryWithFormatterTest {
    private final String expected;
    private final HoursMinutesFormat formatter;
    private final TimeInterval timeInterval;

    public TimesheetItemGetTimeSummaryWithFormatterTest(
            String expected,
            HoursMinutesFormat formatter,
            TimeInterval timeInterval
    ) {
        this.expected = expected;
        this.formatter = formatter;
        this.timeInterval = timeInterval;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1.00",
                                new FractionIntervalFormat(),
                                TimeInterval.builder(1L)
                                        .stopInMilliseconds(3600000)
                                        .build()
                        },
                        {
                                "9.00",
                                new FractionIntervalFormat(),
                                TimeInterval.builder(1L)
                                        .stopInMilliseconds(32400000)
                                        .build()
                        },
                        {
                                "1:00",
                                new DigitalHoursMinutesIntervalFormat(),
                                TimeInterval.builder(1L)
                                        .stopInMilliseconds(3600000)
                                        .build()
                        },
                        {
                                "9:00",
                                new DigitalHoursMinutesIntervalFormat(),
                                TimeInterval.builder(1L)
                                        .stopInMilliseconds(32400000)
                                        .build()
                        }
                }
        );
    }

    @Test
    public void getTimeSummary() {
        TimesheetItem item = TimesheetItem.with(timeInterval);

        assertEquals(expected, item.getTimeSummaryWithFormatter(formatter));
    }
}
