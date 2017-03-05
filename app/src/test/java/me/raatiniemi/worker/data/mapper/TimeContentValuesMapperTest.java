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

import android.content.ContentValues;
import android.provider.BaseColumns;

import org.junit.Before;
import org.junit.Test;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.data.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TimeContentValuesMapperTest extends RobolectricTestCase {
    private TimeContentValuesMapper mapper;

    @Before
    public void setUp() {
        mapper = new TimeContentValuesMapper();
    }

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

    @Test
    public void transform_withTime() {
        ContentValues expected = createContentValues(1L, 1L, 1L, 0L);
        Time time = TimeFactory.builder(1L)
                .startInMilliseconds(1L)
                .stopInMilliseconds(1L)
                .build();
        ContentValues contentValues = mapper.transform(time);

        // the id column should not be mapped since that would introduce the
        // possibility of the id being modified.
        assertNull(contentValues.get(BaseColumns._ID));
        assertEquals(expected.get(TimeColumns.PROJECT_ID), contentValues.get(TimeColumns.PROJECT_ID));
        assertEquals(expected.get(TimeColumns.START), contentValues.get(TimeColumns.START));
        assertEquals(expected.get(TimeColumns.STOP), contentValues.get(TimeColumns.STOP));
        assertEquals(expected.get(TimeColumns.REGISTERED), contentValues.get(TimeColumns.REGISTERED));
    }

    @Test
    public void transform_withRegisteredTime() {
        ContentValues expected = createContentValues(1L, 1L, 1L, 1L);
        Time time = TimeFactory.builder(1L)
                .startInMilliseconds(1L)
                .stopInMilliseconds(1L)
                .register()
                .build();
        ContentValues contentValues = mapper.transform(time);

        // the id column should not be mapped since that would introduce the
        // possibility of the id being modified.
        assertNull(contentValues.get(BaseColumns._ID));
        assertEquals(expected.get(TimeColumns.PROJECT_ID), contentValues.get(TimeColumns.PROJECT_ID));
        assertEquals(expected.get(TimeColumns.START), contentValues.get(TimeColumns.START));
        assertEquals(expected.get(TimeColumns.STOP), contentValues.get(TimeColumns.STOP));
        assertEquals(expected.get(TimeColumns.REGISTERED), contentValues.get(TimeColumns.REGISTERED));
    }
}
