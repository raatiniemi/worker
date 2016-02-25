package me.raatiniemi.worker.presentation.model.timesheet;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;

import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TimesheetItemTest {
    @DataProvider
    public static Object[][] isRegistered_dataProvider() {
        return new Object[][]{
                {
                        Boolean.TRUE,
                        new Time[]{createTimeForIsRegisteredTest(true)}
                },
                {
                        Boolean.FALSE,
                        new Time[]{createTimeForIsRegisteredTest(false)}
                },
                {
                        Boolean.FALSE,
                        new Time[]{
                                createTimeForIsRegisteredTest(false),
                                createTimeForIsRegisteredTest(true)
                        }
                },
                {
                        Boolean.TRUE,
                        new Time[]{
                                createTimeForIsRegisteredTest(true),
                                createTimeForIsRegisteredTest(true)
                        }
                }
        };
    }

    private static Time createTimeForIsRegisteredTest(boolean registered) {
        Time time = mock(Time.class);
        when(time.isRegistered()).thenReturn(registered);

        return time;
    }

    @Test
    @UseDataProvider("isRegistered_dataProvider")
    public void isRegistered(Boolean expected, Time[] times) {
        TimesheetItem timesheet = new TimesheetItem(new Date());
        timesheet.addAll(Arrays.asList(times));

        if (expected) {
            assertTrue(timesheet.isRegistered());
            return;
        }
        assertFalse(timesheet.isRegistered());
    }
}
