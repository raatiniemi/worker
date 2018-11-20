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

package me.raatiniemi.worker.data.repository.mapper;

import android.database.Cursor;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.domain.model.TimeInterval;

/**
 * Handle transformation from {@link Cursor} to {@link TimeInterval}.
 */
public class TimeCursorMapper implements CursorMapper<TimeInterval> {
    @Override
    @NonNull
    public TimeInterval transform(@NonNull Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndexOrThrow(ProviderContract.COLUMN_TIME_PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndexOrThrow(ProviderContract.COLUMN_TIME_START));

        // Handle the nullability of the `stop`-column.
        int stopIndex = cursor.getColumnIndexOrThrow(ProviderContract.COLUMN_TIME_STOP);
        long stop = !cursor.isNull(stopIndex) ? cursor.getLong(stopIndex) : 0;
        long registered = cursor.getLong(cursor.getColumnIndexOrThrow(ProviderContract.COLUMN_TIME_REGISTERED));

        TimeInterval.Builder builder = TimeInterval.builder(projectId)
                .id(id)
                .startInMilliseconds(start)
                .stopInMilliseconds(stop);

        if (0 != registered) {
            builder.register();
        }

        return builder.build();
    }
}
