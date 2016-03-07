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

package me.raatiniemi.worker.data.mapper;

import android.database.Cursor;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TimeCursorMapperTest {
    private static Cursor createCursor(long id, long projectId, long start, Long stop, long registered) {
        Cursor cursor = mock(Cursor.class);

        when(cursor.getColumnIndexOrThrow(TimeColumns._ID)).thenReturn(0);
        when(cursor.getColumnIndexOrThrow(TimeColumns.PROJECT_ID)).thenReturn(1);
        when(cursor.getColumnIndexOrThrow(TimeColumns.START)).thenReturn(2);
        when(cursor.getColumnIndexOrThrow(TimeColumns.STOP)).thenReturn(3);
        when(cursor.getColumnIndexOrThrow(TimeColumns.REGISTERED)).thenReturn(4);

        when(cursor.getLong(0)).thenReturn(id);
        when(cursor.getLong(1)).thenReturn(projectId);
        when(cursor.getLong(2)).thenReturn(start);
        when(cursor.getLong(4)).thenReturn(registered);

        // Depending on whether the `stop` variable is `null` different
        // behaviour should be applied to the mock object.
        boolean isNull = null == stop;
        when(cursor.isNull(3)).thenReturn(isNull);
        if (!isNull) {
            when(cursor.getLong(3)).thenReturn(stop);
        }

        return cursor;
    }

    private static Time createTime(long id, long projectId, long start, long stop, boolean registered) throws ClockOutBeforeClockInException {
        Time.Builder builder = new Time.Builder(projectId)
                .id(id)
                .startInMilliseconds(start)
                .stopInMilliseconds(stop);

        if (registered) {
            builder.register();
        }

        return builder.build();
    }

    @DataProvider
    public static Object[][] transform_dataProvider() throws ClockOutBeforeClockInException {
        return new Object[][]{
                {createCursor(1, 1, 1, null, 0), createTime(1, 1, 1, 0, false)},
                {createCursor(1, 1, 1, 2L, 0), createTime(1, 1, 1, 2L, false)},
                {createCursor(1, 1, 1, 2L, 1), createTime(1, 1, 1, 2L, true)}
        };
    }

    @Test
    @UseDataProvider("transform_dataProvider")
    public void transform(Cursor cursor, Time expected) throws ClockOutBeforeClockInException {
        TimeCursorMapper entityMapper = new TimeCursorMapper();
        Time entity = entityMapper.transform(cursor);

        assertEquals(expected.getId(), entity.getId());
        assertEquals(expected.getProjectId(), entity.getProjectId());
        assertEquals(expected.getStart(), entity.getStart());
        assertEquals(expected.getStop(), entity.getStop());
        assertEquals(expected.isRegistered(), entity.isRegistered());
    }
}
