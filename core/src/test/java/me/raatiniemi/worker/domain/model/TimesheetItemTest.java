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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

@RunWith(JUnit4.class)
public class TimesheetItemTest {
    @Test
    public void asTime() {
        TimeInterval timeInterval = TimeInterval.builder(1L).build();
        TimesheetItem item = TimesheetItem.with(timeInterval);

        assertSame(timeInterval, item.asTimeInterval());
    }

    @Test
    public void getId() {
        TimeInterval timeInterval = TimeInterval.builder(1L)
                .id(1L)
                .build();
        TimesheetItem item = TimesheetItem.with(timeInterval);

        assertEquals(timeInterval.getId(), Long.valueOf(item.getId()));
    }
}
