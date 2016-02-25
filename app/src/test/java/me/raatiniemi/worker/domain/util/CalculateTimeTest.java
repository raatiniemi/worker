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

package me.raatiniemi.worker.domain.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class CalculateTimeTest {
    @DataProvider
    public static Object[][] calculateTime_dataProvider() {
        return new Object[][]{
                {60000L, createCalculatedTime(0, 1)},
                {600000L, createCalculatedTime(0, 10)},
                {900000L, createCalculatedTime(0, 15)},
                {1800000L, createCalculatedTime(0, 30)},
                {3580000L, createCalculatedTime(1, 0)},
                {3600000L, createCalculatedTime(1, 0)},
                {4500000L, createCalculatedTime(1, 15)},
                {7175000L, createCalculatedTime(2, 0)},
                {27000000L, createCalculatedTime(7, 30)},
                {108000000L, createCalculatedTime(30, 0)},
                {203100000L, createCalculatedTime(56, 25)}
        };
    }

    private static CalculateTime.CalculatedTime createCalculatedTime(int hours, int minutes) {
        return new CalculateTime.CalculatedTime(hours, minutes);
    }

    @Test
    @UseDataProvider("calculateTime_dataProvider")
    public void calculateTime(Long milliseconds, CalculateTime.CalculatedTime expected) {
        CalculateTime.CalculatedTime calculatedTime = CalculateTime.calculateTime(milliseconds);

        assertEquals(expected.hours, calculatedTime.hours);
        assertEquals(expected.minutes, calculatedTime.minutes);
    }
}

