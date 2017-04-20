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
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

/**
 * Handle transformation from {@link Cursor} to {@link Time}.
 */
public class TimeCursorMapper implements CursorMapper<Time> {
    @Override
    @NonNull
    public Time transform(@NonNull Cursor cursor) throws ClockOutBeforeClockInException {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.START));

        // Handle the nullability of the `stop`-column.
        int stopIndex = cursor.getColumnIndexOrThrow(TimeColumns.STOP);
        long stop = !cursor.isNull(stopIndex) ? cursor.getLong(stopIndex) : 0;
        long registered = cursor.getLong(cursor.getColumnIndexOrThrow(TimeColumns.REGISTERED));

        Time.Builder builder = Time.builder(projectId)
                .id(id)
                .startInMilliseconds(start)
                .stopInMilliseconds(stop);

        if (0 != registered) {
            builder.register();
        }

        return builder.build();
    }
}
