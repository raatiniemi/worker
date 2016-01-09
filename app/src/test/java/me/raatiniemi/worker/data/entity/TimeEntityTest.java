/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.data.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimeEntityTest {
    @Test
    public void TimeEntity_valuesFromConstructor() {
        TimeEntity entity = new TimeEntity(1, 1, 0, null, false);
        assertEquals(1, entity.getId());
        assertEquals(1, entity.getProjectId());
        assertEquals(0, entity.getStart());
        assertNull(entity.getStop());
        assertFalse(entity.isRegistered());

        entity = new TimeEntity(1, 1, 0, 1L, true);
        assertEquals(1, entity.getId());
        assertEquals(1, entity.getProjectId());
        assertEquals(0, entity.getStart());
        assertEquals(Long.valueOf(1L), entity.getStop());
        assertTrue(entity.isRegistered());
    }
}
