/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.data.mapper;

import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.data.entity.ProjectEntity;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectEntityMapperTest {
    @Test
    public void transform_validCursor() {
        Cursor cursor = mock(Cursor.class);

        when(cursor.getColumnIndexOrThrow(ProjectColumns._ID)).thenReturn(0);
        when(cursor.getColumnIndexOrThrow(ProjectColumns.NAME)).thenReturn(1);
        when(cursor.getColumnIndexOrThrow(ProjectColumns.DESCRIPTION)).thenReturn(2);
        when(cursor.getColumnIndexOrThrow(ProjectColumns.ARCHIVED)).thenReturn(3);

        when(cursor.getLong(0)).thenReturn(1L);
        when(cursor.getString(1)).thenReturn("Name");
        when(cursor.getString(2)).thenReturn("Description");
        when(cursor.getLong(3)).thenReturn(1L);

        ProjectEntityMapper entityMapper = new ProjectEntityMapper();
        ProjectEntity entity = entityMapper.transform(cursor);
        assertEquals(1L, entity.getId());
        assertEquals("Name", entity.getName());
        assertEquals("Description", entity.getDescription());
        assertTrue(entity.isArchived());
    }
}
