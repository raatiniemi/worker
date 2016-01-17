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

import android.content.ContentValues;
import android.database.Cursor;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;

public class TimeMapper {
    /**
     * Private constructor, instantiation is not allowed.
     */
    private TimeMapper() {
    }

    /**
     * Map Time from cursor.
     *
     * @param cursor Cursor with data to map to Time.
     * @return Time with data from cursor.
     */
    public static Time map(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TimeColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndex(TimeColumns.PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndex(TimeColumns.START));
        long stop = cursor.getLong(cursor.getColumnIndex(TimeColumns.STOP));
        long registered = cursor.getLong(cursor.getColumnIndex(TimeColumns.REGISTERED));

        try {
            Time time = new Time(id, projectId, start, stop);
            time.setRegistered(0 != registered);

            return time;
        } catch (DomainException e) {
            // TODO: Handle DomainException properly.
            return null;
        }
    }

    /**
     * Map Time to ContentValues.
     *
     * @param time Time to map to ContentValues.
     * @return Mapped ContentValues.
     */
    public static ContentValues map(Time time) {
        ContentValues values = new ContentValues();
        values.put(TimeColumns.START, time.getStart());
        values.put(TimeColumns.STOP, time.getStop());
        values.put(TimeColumns.PROJECT_ID, time.getProjectId());
        values.put(TimeColumns.REGISTERED, time.isRegistered() ? 1L : 0L);

        return values;
    }
}
