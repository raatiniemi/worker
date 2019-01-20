/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timereport.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimesheetItem;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemBuildResultsTest {
    private final String message;
    private final List<TimeReportAdapterResult> expected;
    private final int groupIndex;
    private final TimesheetGroup groupItem;

    public TimesheetGroupItemBuildResultsTest(
            String message,
            TimeReportAdapterResult[] expected,
            int groupIndex,
            TimesheetGroup groupItem
    ) {
        this.message = message;
        this.expected = Arrays.asList(expected);
        this.groupIndex = groupIndex;
        this.groupItem = groupItem;
    }

    private static TimesheetGroup buildTimesheetGroupWithNumberOfItems(int numberOfItems) {
        if (0 == numberOfItems) {
            return TimesheetGroup.Companion.build(new Date(), new TreeSet<>());
        }

        SortedSet<TimesheetItem> items = new TreeSet<>();
        for (long i = 0; i < numberOfItems; i++) {
            TimeInterval timeInterval = TimeInterval.builder(1L)
                    .startInMilliseconds(i)
                    .build();

            items.add(TimesheetItem.with(timeInterval));
        }

        return TimesheetGroup.Companion.build(new Date(), items);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without items",
                                new TimeReportAdapterResult[]{
                                },
                                0,
                                buildTimesheetGroupWithNumberOfItems(0)
                        },
                        {
                                "With one item",
                                new TimeReportAdapterResult[]{
                                        new TimeReportAdapterResult(1, 0, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(0L).build()))
                                },
                                1,
                                buildTimesheetGroupWithNumberOfItems(1)
                        },
                        {
                                "With multiple items",
                                new TimeReportAdapterResult[]{
                                        new TimeReportAdapterResult(2, 0, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(5L).build())),
                                        new TimeReportAdapterResult(2, 1, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(4L).build())),
                                        new TimeReportAdapterResult(2, 2, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(3L).build())),
                                        new TimeReportAdapterResult(2, 3, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(2L).build())),
                                        new TimeReportAdapterResult(2, 4, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(1L).build())),
                                        new TimeReportAdapterResult(2, 5, TimesheetItem.with(TimeInterval.builder(1L).startInMilliseconds(0L).build()))
                                },
                                2,
                                buildTimesheetGroupWithNumberOfItems(6)
                        }
                }
        );
    }

    @Test
    public void buildItemResultsWithGroupIndex() {
        List<TimeReportAdapterResult> actual =
                groupItem.buildItemResultsWithGroupIndex(groupIndex);

        assertEquals(message, expected, actual);
    }
}
