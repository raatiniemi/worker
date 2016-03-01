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

package me.raatiniemi.worker.domain.model;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.raatiniemi.worker.domain.model.CalculatedTime;
import me.raatiniemi.worker.domain.util.CalculateTime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class CalculatedTimeTest {
    @DataProvider
    public static Object[][] asMilliseconds_dataProvider() {
        return new Object[][]{
                {60000L, createCalculatedTime(0, 1)},
                {600000L, createCalculatedTime(0, 10)},
                {900000L, createCalculatedTime(0, 15)},
                {1800000L, createCalculatedTime(0, 30)},
                {3600000L, createCalculatedTime(1, 0)},
                {4500000L, createCalculatedTime(1, 15)},
                {7200000L, createCalculatedTime(2, 0)},
                {27000000L, createCalculatedTime(7, 30)},
                {108000000L, createCalculatedTime(30, 0)},
                {203100000L, createCalculatedTime(56, 25)}
        };
    }
    @DataProvider
    public static Object[][] equals_dataProvider() {
        return new Object[][]{
                {
                        Boolean.TRUE,
                        createCalculatedTime(3, 15),
                        createCalculatedTime(3, 15)
                },
                {
                        Boolean.TRUE,
                        createCalculatedTime(3, 15),
                        createCalculatedTime(3, 15)
                },
                {
                        Boolean.FALSE,
                        createCalculatedTime(3, 15),
                        createCalculatedTime(5, 15)
                },
                {
                        Boolean.FALSE,
                        createCalculatedTime(3, 14),
                        createCalculatedTime(3, 15)
                }
        };
    }

    private static CalculatedTime createCalculatedTime(int hours, int minutes) {
        return new CalculatedTime(hours, minutes);
    }

    @Test
    @UseDataProvider("asMilliseconds_dataProvider")
    public void asMilliseconds(long expected, CalculatedTime actual) {
        assertEquals(expected, actual.asMilliseconds());
    }

    @Test
    public void equals_withSameInstance() {
        CalculatedTime calculatedTime = new CalculatedTime(1, 0);
        //noinspection EqualsWithItself
        assertTrue(calculatedTime.equals(calculatedTime));
    }

    @Test
    public void equals_withNull() {
        CalculatedTime calculatedTime = new CalculatedTime(1, 0);
        //noinspection ObjectEqualsNull
        assertFalse(calculatedTime.equals(null));
    }

    @Test
    @UseDataProvider("equals_dataProvider")
    public void equals(Boolean expected, CalculatedTime lh, CalculatedTime rh) {
        if (expected) {
            assertTrue(lh.equals(rh));
            assertTrue(rh.equals(lh));
            return;
        }
        assertFalse(lh.equals(rh));
        assertFalse(rh.equals(lh));
    }

    @Test
    @UseDataProvider("equals_dataProvider")
    public void hashCode(Boolean expected, CalculatedTime lh, CalculatedTime rh) {
        if (expected) {
            assertTrue(lh.hashCode() == rh.hashCode());
            return;
        }
        assertFalse(lh.hashCode() == rh.hashCode());
    }
}
