package me.raatiniemi.worker.presentation.project.model;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TimesheetGroupModelTest {
    @DataProvider
    public static Object[][] getTimeSummaryWithDifference_dataProvider() {
        return new Object[][]{
                {
                        "1.00 (-7.00)",
                        new TimesheetChildModel[]{
                                createChildForGetTimeSummaryWithDifferenceTest(3600000)
                        }
                },
                {
                        "8.00",
                        new TimesheetChildModel[]{
                                createChildForGetTimeSummaryWithDifferenceTest(28800000)
                        }
                },
                {
                        "9.00 (+1.00)",
                        new TimesheetChildModel[]{
                                createChildForGetTimeSummaryWithDifferenceTest(32400000)
                        }
                },
                {
                        "9.12 (+1.12)",
                        new TimesheetChildModel[]{
                                createChildForGetTimeSummaryWithDifferenceTest(14380327),
                                createChildForGetTimeSummaryWithDifferenceTest(18407820)
                        }
                },
                {
                        "8.76 (+0.76)",
                        new TimesheetChildModel[]{
                                createChildForGetTimeSummaryWithDifferenceTest(13956031),
                                createChildForGetTimeSummaryWithDifferenceTest(17594386)
                        }
                },
                {
                        "7.86 (-0.14)",
                        new TimesheetChildModel[]{
                                createChildForGetTimeSummaryWithDifferenceTest(11661632),
                                createChildForGetTimeSummaryWithDifferenceTest(16707601)
                        }
                }
        };
    }

    private static TimesheetChildModel createChildForGetTimeSummaryWithDifferenceTest(long interval) {
        Time time = mock(Time.class);
        when(time.getInterval()).thenReturn(interval);

        return new TimesheetChildModel(time);
    }

    @Test
    public void getId() {
        Date date = new Date();
        TimesheetGroupModel groupModel = new TimesheetGroupModel(date);

        assertEquals(date.getTime(), groupModel.getId());
    }

    @Test
    @UseDataProvider("getTimeSummaryWithDifference_dataProvider")
    public void getTimeSummaryWithDifference(String expected, TimesheetChildModel[] times) {
        TimesheetGroupModel timesheet = new TimesheetGroupModel(new Date());
        for (TimesheetChildModel childModel : times) {
            timesheet.add(childModel);
        }

        assertEquals(expected, timesheet.getTimeSummaryWithDifference());
    }
}
