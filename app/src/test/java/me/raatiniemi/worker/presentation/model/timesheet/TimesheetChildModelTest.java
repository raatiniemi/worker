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

package me.raatiniemi.worker.presentation.model.timesheet;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class TimesheetChildModelTest {
    @DataProvider
    public static Object[][] getTimeSummary_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "1.00",
                        createTimeForGetTimeSummaryTest(3600000)
                },
                {
                        "9.00",
                        createTimeForGetTimeSummaryTest(32400000)
                }
        };
    }

    @DataProvider
    public static Object[][] isRegistered_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "is registered",
                        Boolean.TRUE,
                        createTimeForIsRegisteredTest(true)
                },
                {
                        "is not registered",
                        Boolean.FALSE,
                        createTimeForIsRegisteredTest(false)
                }
        };
    }

    private static Time createTimeForGetTimeSummaryTest(long interval)
            throws ClockOutBeforeClockInException {
        return new Time.Builder(1L)
                .stopInMilliseconds(interval)
                .build();
    }

    private static Time createTimeForIsRegisteredTest(boolean registered)
            throws ClockOutBeforeClockInException {
        Time.Builder builder = new Time.Builder(1L);

        if (registered) {
            builder.register();
        }

        return builder.build();
    }

    @Test
    public void asTime() throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(1L).build();
        TimesheetChildModel model = new TimesheetChildModel(time);

        assertTrue(time == model.asTime());
    }

    @Test
    @UseDataProvider("getTimeSummary_dataProvider")
    public void getTimeSummary(String expected, Time time) {
        TimesheetChildModel item = new TimesheetChildModel(time);

        assertEquals(expected, item.getTimeSummary());
    }

    @Test
    @UseDataProvider("isRegistered_dataProvider")
    public void isRegistered(String message, Boolean expected, Time time) {
        TimesheetChildModel actual = new TimesheetChildModel(time);

        assertTrue(message, expected == actual.isRegistered());
    }
}
