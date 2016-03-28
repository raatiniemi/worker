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
public class FractionIntervalFormatTest {
    @DataProvider
    public static Object[][] format_dataProvider() {
        return new Object[][]{
                {900000L, "0.25"},
                {3600000L, "1.00"},
                {4500000L, "1.25"},
                {7175000L, "2.00"},
                {108000000L, "30.00"},
                {203100000L, "56.42"}
        };
    }

    @Test
    @UseDataProvider("format_dataProvider")
    public void format(Long intervalInMilliseconds, String expected) {
        DateIntervalFormat intervalFormat = new FractionIntervalFormat();

        assertEquals(expected, intervalFormat.format(intervalInMilliseconds));
    }
}
