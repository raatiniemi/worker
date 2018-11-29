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

package me.raatiniemi.worker.domain.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FractionIntervalFormatTest {
    private final String expected;
    private final long intervalInMilliseconds;

    public FractionIntervalFormatTest(String expected, long intervalInMilliseconds) {
        this.expected = expected;
        this.intervalInMilliseconds = intervalInMilliseconds;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "0.25",
                                900000L
                        },
                        {
                                "1.00",
                                3600000L
                        },
                        {
                                "1.25",
                                4500000L
                        },
                        {
                                "2.00",
                                7175000L
                        },
                        {
                                "30.00",
                                108000000L
                        },
                        {
                                "56.42",
                                203100000L
                        }
                }
        );
    }

    @Test
    public void format() {
        DateIntervalFormat intervalFormat = new FractionIntervalFormat();

        assertEquals(expected, intervalFormat.format(intervalInMilliseconds));
    }
}
