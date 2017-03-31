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

package me.raatiniemi.worker.data.mapper;

import android.database.Cursor;
import android.provider.BaseColumns;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static me.raatiniemi.worker.util.NullUtil.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class TimeCursorMapperTest {
    private final Time expected;
    private final Cursor cursor;

    public TimeCursorMapperTest(Time expected, Cursor cursor) {
        this.expected = expected;
        this.cursor = cursor;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                createTime(1, 1, 1, 0, false),
                                createCursor(1, 1, 1, null, 0)
                        },
                        {
                                createTime(1, 1, 1, 2L, false),
                                createCursor(1, 1, 1, 2L, 0)
                        },
                        {
                                createTime(1, 1, 1, 2L, true),
                                createCursor(1, 1, 1, 2L, 1)
                        }
                }
        );
    }

    private static Time createTime(
            long id,
            long projectId,
            long start,
            long stop,
            boolean registered
    ) throws ClockOutBeforeClockInException {
        Time.Builder builder = Time.builder(projectId)
                .id(id)
                .startInMilliseconds(start)
                .stopInMilliseconds(stop);

        if (registered) {
            builder.register();
        }

        return builder.build();
    }

    private static Cursor createCursor(
            long id,
            long projectId,
            long start,
            Long stop,
            long registered
    ) {
        Cursor cursor = mock(Cursor.class);

        when(cursor.getColumnIndexOrThrow(BaseColumns._ID)).thenReturn(0);
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
        boolean isNull = isNull(stop);
        when(cursor.isNull(3)).thenReturn(isNull);
        if (!isNull) {
            when(cursor.getLong(3)).thenReturn(stop);
        }

        return cursor;
    }

    @Test
    public void transform() throws ClockOutBeforeClockInException {
        TimeCursorMapper mapper = new TimeCursorMapper();
        Time actual = mapper.transform(cursor);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getProjectId(), actual.getProjectId());
        assertEquals(expected.getStartInMilliseconds(), actual.getStartInMilliseconds());
        assertEquals(expected.getStopInMilliseconds(), actual.getStopInMilliseconds());
        assertEquals(expected.isRegistered(), actual.isRegistered());
    }
}
