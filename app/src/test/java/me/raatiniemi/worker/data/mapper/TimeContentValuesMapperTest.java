/*
 * Copyright (C) 2016 Worker Project
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

import android.content.ContentValues;
import android.provider.BaseColumns;

import org.junit.Test;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.RobolectricTestCase;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TimeContentValuesMapperTest extends RobolectricTestCase {
    private static ContentValues createContentValues(
            final long projectId,
            final long start,
            final long stop,
            final long registered
    ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeColumns.PROJECT_ID, projectId);
        contentValues.put(TimeColumns.START, start);
        contentValues.put(TimeColumns.STOP, stop);
        contentValues.put(TimeColumns.REGISTERED, registered);

        return contentValues;
    }

    private static Time createTime(
            final long projectId,
            final long start,
            final long stop,
            final boolean registered
    ) throws ClockOutBeforeClockInException {
        Time.Builder builder = Time.builder(projectId)
                .startInMilliseconds(start)
                .stopInMilliseconds(stop);

        if (registered) {
            builder.register();
        }

        return builder.build();
    }

    @Test
    public void transform()
            throws ClockOutBeforeClockInException {
        TimeContentValuesMapper entityMapper = new TimeContentValuesMapper();

        ContentValues expected = createContentValues(1L, 1L, 1L, 0L);
        Time time = createTime(1L, 1L, 1L, false);
        ContentValues contentValues = entityMapper.transform(time);

        // the id column should not be mapped since that would introduce the
        // possibility of the id being modified.
        assertNull(contentValues.get(BaseColumns._ID));
        assertEquals(expected.get(TimeColumns.PROJECT_ID), contentValues.get(TimeColumns.PROJECT_ID));
        assertEquals(expected.get(TimeColumns.START), contentValues.get(TimeColumns.START));
        assertEquals(expected.get(TimeColumns.STOP), contentValues.get(TimeColumns.STOP));
        assertEquals(expected.get(TimeColumns.REGISTERED), contentValues.get(TimeColumns.REGISTERED));

        expected = createContentValues(1L, 1L, 1L, 1L);
        time = createTime(1L, 1L, 1L, true);
        contentValues = entityMapper.transform(time);

        // the id column should not be mapped since that would introduce the
        // possibility of the id being modified.
        assertNull(contentValues.get(BaseColumns._ID));
        assertEquals(expected.get(TimeColumns.PROJECT_ID), contentValues.get(TimeColumns.PROJECT_ID));
        assertEquals(expected.get(TimeColumns.START), contentValues.get(TimeColumns.START));
        assertEquals(expected.get(TimeColumns.STOP), contentValues.get(TimeColumns.STOP));
        assertEquals(expected.get(TimeColumns.REGISTERED), contentValues.get(TimeColumns.REGISTERED));
    }
}
