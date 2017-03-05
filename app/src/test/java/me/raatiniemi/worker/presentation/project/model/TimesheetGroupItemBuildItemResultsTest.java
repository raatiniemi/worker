/*
 * Copyright (C) 2017 Worker Project
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
import java.util.List;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemBuildItemResultsTest {
    private static final Time time = TimeFactory.builder().build();

    private final String message;
    private final List<TimeInAdapterResult> expected;
    private final int groupIndex;
    private final TimesheetGroupItem groupItem;

    public TimesheetGroupItemBuildItemResultsTest(
            String message,
            TimeInAdapterResult[] expected,
            int groupIndex,
            TimesheetGroupItem groupItem
    ) {
        this.message = message;
        this.expected = Arrays.asList(expected);
        this.groupIndex = groupIndex;
        this.groupItem = groupItem;
    }

    private static TimesheetGroupItem buildTimesheetGroupWithNumberOfChildItems(int numberOfChildItems) {
        TimesheetGroupItem groupItem = new TimesheetGroupItem(new Date());
        if (0 == numberOfChildItems) {
            return groupItem;
        }

        for (int i = 0; i < numberOfChildItems; i++) {
            Time time = TimeFactory.builder()
                    .build();

            groupItem.add(new TimesheetChildItem(time));
        }

        return groupItem;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without items",
                                new TimeInAdapterResult[]{
                                },
                                0,
                                buildTimesheetGroupWithNumberOfChildItems(0)
                        },
                        {
                                "With one item",
                                new TimeInAdapterResult[]{
                                        TimeInAdapterResult.build(1, 0, time)
                                },
                                1,
                                buildTimesheetGroupWithNumberOfChildItems(1)
                        },
                        {
                                "With multiple items",
                                new TimeInAdapterResult[]{
                                        TimeInAdapterResult.build(2, 0, time),
                                        TimeInAdapterResult.build(2, 1, time),
                                        TimeInAdapterResult.build(2, 2, time),
                                        TimeInAdapterResult.build(2, 3, time),
                                        TimeInAdapterResult.build(2, 4, time),
                                        TimeInAdapterResult.build(2, 5, time),
                                },
                                2,
                                buildTimesheetGroupWithNumberOfChildItems(6)
                        }
                }
        );
    }

    @Test
    public void buildItemResultsWithGroupIndex() {
        List<TimeInAdapterResult> actual =
                groupItem.buildItemResultsWithGroupIndex(groupIndex);

        assertEquals(message, expected, actual);
    }
}
