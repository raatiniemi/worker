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

@RunWith(Parameterized.class)
public class HoursMinutesAsMillisecondsTest {
    private final long expected;
    private final HoursMinutes hoursMinutes;

    public HoursMinutesAsMillisecondsTest(long expected, HoursMinutes hoursMinutes) {
        this.expected = expected;
        this.hoursMinutes = hoursMinutes;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                60000L,
                                createHoursMinutes(0, 1)
                        },
                        {
                                600000L,
                                createHoursMinutes(0, 10)
                        },
                        {
                                900000L,
                                createHoursMinutes(0, 15)
                        },
                        {
                                1800000L,
                                createHoursMinutes(0, 30)
                        },
                        {
                                3600000L,
                                createHoursMinutes(1, 0)
                        },
                        {
                                4500000L,
                                createHoursMinutes(1, 15)
                        },
                        {
                                7200000L,
                                createHoursMinutes(2, 0)
                        },
                        {
                                27000000L,
                                createHoursMinutes(7, 30)
                        },
                        {
                                108000000L,
                                createHoursMinutes(30, 0)
                        },
                        {
                                203100000L,
                                createHoursMinutes(56, 25)
                        }
                }
        );
    }

    private static HoursMinutes createHoursMinutes(int hours, int minutes) {
        return new HoursMinutes(hours, minutes);
    }

    @Test
    public void asMilliseconds() {
        assertEquals(expected, hoursMinutes.asMilliseconds());
    }
}
