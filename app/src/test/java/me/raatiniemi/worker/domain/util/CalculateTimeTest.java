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

package me.raatiniemi.worker.domain.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.model.CalculatedTime;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculateTimeTest {
    private final CalculatedTime expected;
    private final long milliseconds;

    public CalculateTimeTest(CalculatedTime expected, long milliseconds) {
        this.expected = expected;
        this.milliseconds = milliseconds;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                createCalculatedTime(0, 1),
                                60000L
                        },
                        {
                                createCalculatedTime(0, 10),
                                600000L
                        },
                        {
                                createCalculatedTime(0, 15),
                                900000L
                        },
                        {
                                createCalculatedTime(0, 30),
                                1800000L
                        },
                        {
                                createCalculatedTime(1, 0),
                                3580000L
                        },
                        {
                                createCalculatedTime(1, 0),
                                3600000L
                        },
                        {
                                createCalculatedTime(1, 15),
                                4500000L
                        },
                        {
                                createCalculatedTime(2, 0),
                                7175000L
                        },
                        {
                                createCalculatedTime(7, 30),
                                27000000L
                        },
                        {
                                createCalculatedTime(30, 0),
                                108000000L
                        },
                        {
                                createCalculatedTime(56, 25),
                                203100000L
                        }
                }
        );
    }

    private static CalculatedTime createCalculatedTime(int hours, int minutes) {
        return new CalculatedTime(hours, minutes);
    }

    @Test
    public void calculateTime() {
        assertEquals(expected, CalculateTime.calculateTime(milliseconds));
    }
}
