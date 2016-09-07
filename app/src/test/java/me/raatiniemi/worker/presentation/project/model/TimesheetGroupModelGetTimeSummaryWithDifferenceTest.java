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

package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupModelGetTimeSummaryWithDifferenceTest {
    private String expected;
    private TimesheetChildModel[] children;

    public TimesheetGroupModelGetTimeSummaryWithDifferenceTest(
            String expected,
            TimesheetChildModel... children
    ) {
        this.expected = expected;
        this.children = children;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1.00 (-7.00)",
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(3600000)
                                }
                        },
                        {
                                "8.00",
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(28800000)
                                }
                        },
                        {
                                "9.00 (+1.00)",
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(32400000)
                                }
                        },
                        {
                                "9.12 (+1.12)",
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(14380327),
                                        buildTimesheetChildModel(18407820)
                                }
                        },
                        {
                                "8.76 (+0.76)",
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(13956031),
                                        buildTimesheetChildModel(17594386)
                                }
                        },
                        {
                                "7.86 (-0.14)",
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(11661632),
                                        buildTimesheetChildModel(16707601)
                                }
                        }
                }
        );
    }

    private static TimesheetChildModel buildTimesheetChildModel(long interval)
            throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(1L)
                .startInMilliseconds(1L)
                .stopInMilliseconds(interval)
                .build();

        return new TimesheetChildModel(time);
    }

    @Test
    public void getTimeSummaryWithDifference() {
        TimesheetGroupModel item = new TimesheetGroupModel(new Date());
        for (TimesheetChildModel child : children) {
            item.add(child);
        }

        assertEquals(expected, item.getTimeSummaryWithDifference());
    }
}
