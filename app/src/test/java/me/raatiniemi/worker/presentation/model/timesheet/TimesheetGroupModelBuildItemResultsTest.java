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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupModelBuildItemResultsTest {
    private String mMessage;
    private List<TimeInAdapterResult> mExpected;
    private int mGroupIndex;
    private TimesheetGroupModel mTimesheetGroupModel;

    public TimesheetGroupModelBuildItemResultsTest(
            String message,
            TimeInAdapterResult[] expected,
            int groupIndex,
            TimesheetGroupModel timesheetGroupModel
    ) {
        mMessage = message;
        mExpected = Arrays.asList(expected);
        mGroupIndex = groupIndex;
        mTimesheetGroupModel = timesheetGroupModel;
    }

    private static TimesheetGroupModel buildTimesheetGroupWithNumberOfChildModelItems(
            int numberOfChildModelItems
    ) throws ClockOutBeforeClockInException {
        TimesheetGroupModel groupModel = new TimesheetGroupModel(new Date());
        if (0 == numberOfChildModelItems) {
            return groupModel;
        }

        for (int i = 0; i < numberOfChildModelItems; i++) {
            groupModel.add(new TimesheetChildModel(buildTime()));
        }

        return groupModel;
    }

    private static Time buildTime() throws ClockOutBeforeClockInException {
        return new Time.Builder(1)
                .build();
    }

    @Parameters
    public static Collection<Object[]> parameters()
            throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without items",
                                new TimeInAdapterResult[]{
                                },
                                0,
                                buildTimesheetGroupWithNumberOfChildModelItems(0)
                        },
                        {
                                "With one item",
                                new TimeInAdapterResult[]{
                                        TimeInAdapterResult.build(1, 0, buildTime())
                                },
                                1,
                                buildTimesheetGroupWithNumberOfChildModelItems(1)
                        },
                        {
                                "With multiple items",
                                new TimeInAdapterResult[]{
                                        TimeInAdapterResult.build(2, 0, buildTime()),
                                        TimeInAdapterResult.build(2, 1, buildTime()),
                                        TimeInAdapterResult.build(2, 2, buildTime()),
                                        TimeInAdapterResult.build(2, 3, buildTime()),
                                        TimeInAdapterResult.build(2, 4, buildTime()),
                                        TimeInAdapterResult.build(2, 5, buildTime()),
                                },
                                2,
                                buildTimesheetGroupWithNumberOfChildModelItems(6)
                        }
                }
        );
    }

    @Test
    public void buildItemResultsWithGroupIndex() {
        List<TimeInAdapterResult> actual =
                mTimesheetGroupModel.buildItemResultsWithGroupIndex(mGroupIndex);

        assertEquals(mMessage, mExpected, actual);
    }
}
