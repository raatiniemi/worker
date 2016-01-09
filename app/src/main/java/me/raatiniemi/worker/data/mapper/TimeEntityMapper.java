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
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.data.entity.TimeEntity;

/**
 * Mapper for transforming {@link Cursor} to {@link TimeEntity}.
 */
public class TimeEntityMapper implements EntityMapper<TimeEntity> {
    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public TimeEntity transform(@NonNull Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.START));

        // Handle the nullability of the `stop`-column.
        int stopIndex = cursor.getColumnIndexOrThrow(TimeColumns.STOP);
        Long stop = !cursor.isNull(stopIndex) ? cursor.getLong(stopIndex) : null;
        long registered = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.REGISTERED));

        return new TimeEntity(id, projectId, start, stop, 0 != registered);
    }
}
