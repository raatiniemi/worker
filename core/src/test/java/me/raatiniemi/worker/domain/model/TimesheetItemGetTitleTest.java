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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetItemGetTitleTest {
    private static final Calendar START = new GregorianCalendar(2016, 1, 28, 8, 0);
    private static final Calendar STOP = new GregorianCalendar(2016, 1, 28, 11, 30);

    private final String message;
    private final String expected;
    private final TimeInterval timeInterval;

    public TimesheetItemGetTitleTest(
            String message,
            String expected,
            TimeInterval timeInterval
    ) {
        this.message = message;
        this.expected = expected;
        this.timeInterval = timeInterval;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "active time interval",
                                "08:00",
                                TimeInterval.builder(1L)
                                        .startInMilliseconds(START.getTimeInMillis())
                                        .build()
                        },
                        {
                                "inactive time interval",
                                "08:00 - 11:30",
                                TimeInterval.builder(1L)
                                        .startInMilliseconds(START.getTimeInMillis())
                                        .stopInMilliseconds(STOP.getTimeInMillis())
                                        .build()
                        }
                }
        );
    }

    @Test
    public void getTitle() {
        TimesheetItem item = TimesheetItem.with(timeInterval);

        assertEquals(message, expected, item.getTitle());
    }
}
