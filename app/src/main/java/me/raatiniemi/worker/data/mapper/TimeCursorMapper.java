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
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.domain.exception.DomainException;

/**
 * Handle transformation from {@link Cursor} to {@link Time}.
 */
public class TimeCursorMapper implements CursorMapper<Time> {
    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public Time transform(@NonNull Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.START));

        // Handle the nullability of the `stop`-column.
        int stopIndex = cursor.getColumnIndexOrThrow(TimeColumns.STOP);
        // TODO: Use null instead of zero for null stop value.
        Long stop = !cursor.isNull(stopIndex) ? cursor.getLong(stopIndex) : 0;
        long registered = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.REGISTERED));

        try {
            Time time = new Time(id, projectId, start, stop);
            time.setRegistered(0 != registered);

            return time;
        } catch (DomainException e) {
            // TODO: Handle the DomainException from the construction of Time.
            // Temporarily re-throw the exception as a runtime exception since
            // the transformation method do not support checked exceptions.
            throw new RuntimeException(e);
        }
    }
}
