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

import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemGetTimeSummaryWithDifferenceTest {
    private final String expected;
    private final TimesheetChildItem[] childItems;

    public TimesheetGroupItemGetTimeSummaryWithDifferenceTest(
            String expected,
            TimesheetChildItem... childItems
    ) {
        this.expected = expected;
        this.childItems = childItems;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1.00 (-7.00)",
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(3600000L)
                                                        .build()
                                        )
                                }
                        },
                        {
                                "8.00",
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(28800000L)
                                                        .build()
                                        )
                                }
                        },
                        {
                                "9.00 (+1.00)",
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(32400000L)
                                                        .build()
                                        )
                                }
                        },
                        {
                                "9.12 (+1.12)",
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(14380327L)
                                                        .build()
                                        ),
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(18407820L)
                                                        .build()
                                        )
                                }
                        },
                        {
                                "8.76 (+0.76)",
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(13956031L)
                                                        .build()
                                        ),
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(17594386L)
                                                        .build()
                                        )
                                }
                        },
                        {
                                "7.86 (-0.14)",
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(11661632L)
                                                        .build()
                                        ),
                                        new TimesheetChildItem(
                                                TimeFactory.builder()
                                                        .startInMilliseconds(1L)
                                                        .stopInMilliseconds(16707601L)
                                                        .build()
                                        )
                                }
                        }
                }
        );
    }

    @Test
    public void getTimeSummaryWithDifference() {
        TimesheetGroupItem groupItem = TimesheetGroupItem.build(new Date());
        for (TimesheetChildItem childItem : childItems) {
            groupItem.add(childItem);
        }

        assertEquals(expected, groupItem.getTimeSummaryWithDifference());
    }
}
