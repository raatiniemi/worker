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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimeReportGroupGetTitleTest {
    private final String expected;
    private final Calendar calendar;

    public TimeReportGroupGetTitleTest(String expected, Calendar calendar) {
        this.expected = expected;
        this.calendar = calendar;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Sun (Feb 28)",
                                new GregorianCalendar(2016, 1, 28)
                        }
                }
        );
    }

    @Test
    public void getTitle() {
        TimeReportGroup groupItem = TimeReportGroup.Companion.build(calendar.getTime(), new TreeSet<>());

        assertEquals(expected, groupItem.getTitle());
    }
}
