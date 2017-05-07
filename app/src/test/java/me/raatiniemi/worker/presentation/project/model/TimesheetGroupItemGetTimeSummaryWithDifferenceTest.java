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
import java.util.TreeSet;

import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemGetTimeSummaryWithDifferenceTest {
    private final String expected;
    private final TimesheetGroupItem item;

    public TimesheetGroupItemGetTimeSummaryWithDifferenceTest(
            String expected,
            Time... times
    ) {
        this.expected = expected;

        TreeSet<Time> items = new TreeSet<>(new TimesheetItemComparator());
        items.addAll(Arrays.asList(times));
        item = TimesheetGroupItem.build(new Date(), items);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1.00 (-7.00)",
                                new Time[]{
                                        buildTimeWithInterval(3600000)
                                }
                        },
                        {
                                "8.00",
                                new Time[]{
                                        buildTimeWithInterval(28800000)
                                }
                        },
                        {
                                "9.00 (+1.00)",
                                new Time[]{
                                        buildTimeWithInterval(32400000)
                                }
                        },
                        {
                                "9.12 (+1.12)",
                                new Time[]{
                                        buildTimeWithInterval(14380327),
                                        buildTimeWithInterval(18407820)
                                }
                        },
                        {
                                "8.76 (+0.76)",
                                new Time[]{
                                        buildTimeWithInterval(13956031),
                                        buildTimeWithInterval(17594386)
                                }
                        },
                        {
                                "7.86 (-0.14)",
                                new Time[]{
                                        buildTimeWithInterval(11661632),
                                        buildTimeWithInterval(16707601)
                                }
                        }
                }
        );
    }

    private static Time buildTimeWithInterval(long interval) {
        return TimeFactory.builder()
                .startInMilliseconds(1L)
                .stopInMilliseconds(interval)
                .build();
    }

    @Test
    public void getTimeSummaryWithDifference() {
        assertEquals(expected, item.getTimeSummaryWithDifference());
    }
}
