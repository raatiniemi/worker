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

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class HoursMinutesIntervalFormatTest {
    @DataProvider
    public static Object[][] format_dataProvider() {
        return new Object[][]{
                {60000L, "1m"},
                {600000L, "10m"},
                {1800000L, "30m"},
                {3600000L, "1h 0m"},
                {27000000L, "7h 30m"},
                {108000000L, "30h 0m"},
                {203100000L, "56h 25m"},
                {3580000L, "1h 0m"}
        };
    }

    @Test
    @UseDataProvider("format_dataProvider")
    public void format(Long intervalInMilliseconds, String expected) {
        DateIntervalFormat intervalFormat = new HoursMinutesIntervalFormat();

        assertEquals(expected, intervalFormat.format(intervalInMilliseconds));
    }
}
