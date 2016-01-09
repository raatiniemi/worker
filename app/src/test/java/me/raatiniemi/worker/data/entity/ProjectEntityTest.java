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
public class ProjectEntityTest {
    @Test
    public void ProjectEntity_valuesFromConstructor() {
        ProjectEntity entity = new ProjectEntity(1L, "Name", null, false);
        assertEquals(1L, entity.getId());
        assertEquals("Name", entity.getName());
        assertNull(entity.getDescription());
        assertFalse(entity.isArchived());

        entity = new ProjectEntity(1L, "Name", "Description", true);
        assertEquals(1L, entity.getId());
        assertEquals("Name", entity.getName());
        assertEquals("Description", entity.getDescription());
        assertTrue(entity.isArchived());
    }
}
