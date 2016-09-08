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
public class TimesheetGroupModelIsRegisteredTest {
    private boolean expected;
    private TimesheetChildModel[] children;

    public TimesheetGroupModelIsRegisteredTest(
            boolean expected,
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
                                Boolean.TRUE,
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(true)
                                }
                        },
                        {
                                Boolean.FALSE,
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(false)
                                }
                        },
                        {
                                Boolean.FALSE,
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(false),
                                        buildTimesheetChildModel(true)
                                }
                        },
                        {
                                Boolean.TRUE,
                                new TimesheetChildModel[]{
                                        buildTimesheetChildModel(true),
                                        buildTimesheetChildModel(true)
                                }
                        }
                }
        );
    }

    private static TimesheetChildModel buildTimesheetChildModel(boolean registered)
            throws ClockOutBeforeClockInException {
        Time.Builder builder = new Time.Builder(1L);

        if (registered) {
            builder.register();
        }

        Time time = builder.build();
        return new TimesheetChildModel(time);
    }

    @Test
    public void isRegistered() {
        TimesheetGroupModel item = new TimesheetGroupModel(new Date());
        for (TimesheetChildModel child : children) {
            item.add(child);
        }

        assertEquals(expected, item.isRegistered());
    }
}
