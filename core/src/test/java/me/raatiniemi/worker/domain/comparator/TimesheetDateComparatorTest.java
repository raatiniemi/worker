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

package me.raatiniemi.worker.domain.comparator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TimesheetDateComparatorTest {
    private TimesheetDateComparator comparator;

    @Before
    public void setUp() {
        comparator = new TimesheetDateComparator();
    }

    @Test
    public void compare_withDateBefore() {
        Date before = new Date(1L);
        Date after = new Date(2L);

        assertEquals(-1, comparator.compare(after, before));
    }

    @Test
    public void compare_withDateAfter() {
        Date before = new Date(1L);
        Date after = new Date(2L);

        assertEquals(1, comparator.compare(before, after));
    }

    @Test
    public void compare_withSameDate() {
        Date date = new Date(1L);

        assertEquals(0, comparator.compare(date, date));
    }
}
