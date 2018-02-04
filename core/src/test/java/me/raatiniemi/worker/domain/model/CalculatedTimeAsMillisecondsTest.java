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
public class CalculatedTimeAsMillisecondsTest {
    private final long expected;
    private final CalculatedTime calculatedTime;

    public CalculatedTimeAsMillisecondsTest(long expected, CalculatedTime calculatedTime) {
        this.expected = expected;
        this.calculatedTime = calculatedTime;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                60000L,
                                createCalculatedTime(0, 1)
                        },
                        {
                                600000L,
                                createCalculatedTime(0, 10)
                        },
                        {
                                900000L,
                                createCalculatedTime(0, 15)
                        },
                        {
                                1800000L,
                                createCalculatedTime(0, 30)
                        },
                        {
                                3600000L,
                                createCalculatedTime(1, 0)
                        },
                        {
                                4500000L,
                                createCalculatedTime(1, 15)
                        },
                        {
                                7200000L,
                                createCalculatedTime(2, 0)
                        },
                        {
                                27000000L,
                                createCalculatedTime(7, 30)
                        },
                        {
                                108000000L,
                                createCalculatedTime(30, 0)
                        },
                        {
                                203100000L,
                                createCalculatedTime(56, 25)
                        }
                }
        );
    }

    private static CalculatedTime createCalculatedTime(int hours, int minutes) {
        return new CalculatedTime(hours, minutes);
    }

    @Test
    public void asMilliseconds() {
        assertEquals(expected, calculatedTime.asMilliseconds());
    }
}
