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

package me.raatiniemi.worker.presentation.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class HoursMinutesIntervalFormatTest {
    private final String expected;
    private final long intervalInMilliseconds;

    public HoursMinutesIntervalFormatTest(String expected, long intervalInMilliseconds) {
        this.expected = expected;
        this.intervalInMilliseconds = intervalInMilliseconds;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1m",
                                60000L
                        },
                        {
                                "10m",
                                600000L
                        },
                        {
                                "30m",
                                1800000L
                        },
                        {
                                "1h 0m",
                                3600000L
                        },
                        {
                                "7h 30m",
                                27000000L
                        },
                        {
                                "30h 0m",
                                108000000L
                        },
                        {
                                "56h 25m",
                                203100000L
                        },
                        {
                                "1h 0m",
                                3580000L
                        }
                }
        );
    }

    @Test
    public void format() {
        DateIntervalFormat intervalFormat = new HoursMinutesIntervalFormat();

        assertEquals(expected, intervalFormat.format(intervalInMilliseconds));
    }
}
