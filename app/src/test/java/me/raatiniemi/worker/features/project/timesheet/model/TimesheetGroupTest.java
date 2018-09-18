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

package me.raatiniemi.worker.features.project.timesheet.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TimesheetGroupTest {
    @Test
    public void getId() {
        Date date = new Date();
        long days = date.getTime() / 1000 / 60 / 60 / 24;

        TimesheetGroup groupItem = TimesheetGroup.Companion.build(date, new TreeSet<>());

        assertEquals(days, groupItem.getId());
    }
}
