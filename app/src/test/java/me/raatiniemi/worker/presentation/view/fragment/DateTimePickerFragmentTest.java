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

package me.raatiniemi.worker.presentation.view.fragment;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import me.raatiniemi.worker.RobolectricTestCase;

public class DateTimePickerFragmentTest extends RobolectricTestCase {
    private DateTimePickerFragment fragment;
    private Calendar today;
    private Calendar nextYear;

    @Before
    public void setUp() throws Exception {
        fragment = new DateTimePickerFragment();

        today = Calendar.getInstance();
        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
    }

    @Test
    public void setMinDate_withValidDate() {
        fragment.setMinDate(today);
    }

    @Test(expected = RuntimeException.class)
    public void setMinDate_afterMaxDate() {
        fragment.setMaxDate(today);
        fragment.setMinDate(nextYear);
    }

    @Test
    public void setMaxDate_withValidDate() {
        fragment.setMaxDate(nextYear);
    }

    @Test(expected = RuntimeException.class)
    public void setMaxDate_beforeMinDate() {
        fragment.setMinDate(nextYear);
        fragment.setMaxDate(today);
    }
}
