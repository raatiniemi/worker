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
import java.util.SortedSet;
import java.util.TreeSet;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemBuildResultsTest {
    private final String message;
    private final List<TimesheetAdapterResult> expected;
    private final int groupIndex;
    private final TimesheetGroup groupItem;

    public TimesheetGroupItemBuildResultsTest(
            String message,
            TimesheetAdapterResult[] expected,
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
            return TimesheetGroup.build(new Date(), new TreeSet<>());
        }

        SortedSet<TimesheetItem> items = new TreeSet<>();
        for (long i = 0; i < numberOfItems; i++) {
            Time time = TimeFactory.builder()
                    .startInMilliseconds(i)
                    .build();

            items.add(TimesheetItem.with(time));
        }

        return TimesheetGroup.build(new Date(), items);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without items",
                                new TimesheetAdapterResult[]{
                                },
                                0,
                                buildTimesheetGroupWithNumberOfItems(0)
                        },
                        {
                                "With one item",
                                new TimesheetAdapterResult[]{
                                        TimesheetAdapterResult.build(1, 0, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(0L).build()))
                                },
                                1,
                                buildTimesheetGroupWithNumberOfItems(1)
                        },
                        {
                                "With multiple items",
                                new TimesheetAdapterResult[]{
                                        TimesheetAdapterResult.build(2, 0, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(5L).build())),
                                        TimesheetAdapterResult.build(2, 1, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(4L).build())),
                                        TimesheetAdapterResult.build(2, 2, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(3L).build())),
                                        TimesheetAdapterResult.build(2, 3, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(2L).build())),
                                        TimesheetAdapterResult.build(2, 4, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(1L).build())),
                                        TimesheetAdapterResult.build(2, 5, TimesheetItem.with(TimeFactory.builder().startInMilliseconds(0L).build()))
                                },
                                2,
                                buildTimesheetGroupWithNumberOfItems(6)
                        }
                }
        );
    }

    @Test
    public void buildItemResultsWithGroupIndex() {
        List<TimesheetAdapterResult> actual =
                groupItem.buildItemResultsWithGroupIndex(groupIndex);

        assertEquals(message, expected, actual);
    }
}
