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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemGetFirstLetterFromTitleTest {
    private String expected;
    private Calendar calendar;

    public TimesheetGroupItemGetFirstLetterFromTitleTest(String expected, Calendar calendar) {
        this.expected = expected;
        this.calendar = calendar;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "F",
                                new GregorianCalendar(2016, 6, 1)
                        },
                        {
                                "S",
                                new GregorianCalendar(2016, 6, 2)
                        },
                        {
                                "S",
                                new GregorianCalendar(2016, 6, 3)
                        },
                        {
                                "M",
                                new GregorianCalendar(2016, 6, 4)
                        },
                        {
                                "T",
                                new GregorianCalendar(2016, 6, 5)
                        },
                        {
                                "W",
                                new GregorianCalendar(2016, 6, 6)
                        },
                        {
                                "T",
                                new GregorianCalendar(2016, 6, 7)
                        }
                }
        );
    }

    @Test
    public void getFirstLetterFromTitle() {
        TimesheetGroupItem groupItem = new TimesheetGroupItem(calendar.getTime());

        assertEquals(expected, groupItem.getFirstLetterFromTitle());
    }
}
