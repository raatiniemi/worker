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

package me.raatiniemi.worker.data.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.WorkerContract.TimeContract;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

public class TimeResolverRepository
        extends ContentResolverRepository<TimeCursorMapper, TimeContentValuesMapper>
        implements TimeRepository {
    /**
     * @inheritDoc
     */
    public TimeResolverRepository(
            @NonNull ContentResolver contentResolver,
            @NonNull TimeCursorMapper cursorMapper,
            @NonNull final TimeContentValuesMapper contentValuesMapper
    ) {
        super(contentResolver, cursorMapper, contentValuesMapper);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Time get(final long id) throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                TimeContract.getItemUri(id),
                TimeContract.COLUMNS,
                null,
                null,
                null
        );
        if (null == cursor) {
            return null;
        }

        Time time = null;
        if (cursor.moveToFirst()) {
            time = getCursorMapper().transform(cursor);
        }
        cursor.close();

        return time;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Time add(final Time time) throws ClockOutBeforeClockInException {
        final ContentValues values = getContentValuesMapper().transform(time);

        final Uri uri = getContentResolver().insert(
                TimeContract.getStreamUri(),
                values
        );
        return get(Long.valueOf(TimeContract.getItemId(uri)));
    }

    /**
     * @inheritDoc
     */
    @Override
    public Time update(final Time time) throws ClockOutBeforeClockInException {
        getContentResolver().update(
                TimeContract.getItemUri(time.getId()),
                getContentValuesMapper().transform(time),
                null,
                null
        );

        return get(time.getId());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove(final long id) {
        getContentResolver().delete(
                TimeContract.getItemUri(id),
                null,
                null
        );
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Time> getProjectTimeSinceBeginningOfMonth(long projectId)
            throws ClockOutBeforeClockInException {
        final List<Time> result = new ArrayList<>();

        // Reset the calendar to retrieve timestamp
        // of the beginning of the month.
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        final Cursor cursor = getContentResolver().query(
                ProjectContract.getItemTimeUri(projectId),
                TimeContract.COLUMNS,
                TimeContract.START + ">=? OR " + TimeContract.STOP + " = 0",
                new String[]{String.valueOf(calendar.getTimeInMillis())},
                ProjectContract.ORDER_BY_TIME
        );
        if (null == cursor) {
            return result;
        }

        if (cursor.moveToFirst()) {
            do {
                result.add(getCursorMapper().transform(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }
}
