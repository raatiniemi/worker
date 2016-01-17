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

package me.raatiniemi.worker.domain.mapper;

import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimeMapperTest {
    @Test
    public void map_cursorToTime() {
        Cursor cursor = mock(Cursor.class);

        when(cursor.getColumnIndex(TimeColumns._ID)).thenReturn(0);
        when(cursor.getLong(0)).thenReturn(1L);

        when(cursor.getColumnIndex(TimeColumns.PROJECT_ID)).thenReturn(1);
        when(cursor.getLong(1)).thenReturn(2L);

        when(cursor.getColumnIndex(TimeColumns.START)).thenReturn(2);
        when(cursor.getLong(2)).thenReturn(3L);

        when(cursor.getColumnIndex(TimeColumns.STOP)).thenReturn(3);
        when(cursor.getLong(3)).thenReturn(4L);

        when(cursor.getColumnIndex(TimeColumns.REGISTERED)).thenReturn(4);
        when(cursor.getLong(4)).thenReturn(1L);

        Time time = TimeMapper.map(cursor);

        assertNotNull(time);
        assertEquals(Long.valueOf(1L), time.getId());
        assertEquals(2L, time.getProjectId());
        assertEquals(3L, time.getStart());
        assertEquals(4L, time.getStop());
        assertTrue(time.isRegistered());
    }
}
