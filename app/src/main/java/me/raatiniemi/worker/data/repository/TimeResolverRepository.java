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

package me.raatiniemi.worker.data.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.data.provider.ProviderContract.TimeColumns;
import me.raatiniemi.worker.data.repository.exception.ContentResolverApplyBatchException;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.util.Optional;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class TimeResolverRepository extends ContentResolverRepository implements TimeRepository {
    private final TimeCursorMapper cursorMapper = new TimeCursorMapper();
    private final TimeContentValuesMapper contentValuesMapper = new TimeContentValuesMapper();

    public TimeResolverRepository(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    private List<Time> fetch(@Nullable Cursor cursor) throws ClockOutBeforeClockInException {
        List<Time> results = new ArrayList<>();
        if (isNull(cursor)) {
            return results;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    results.add(cursorMapper.transform(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return results;
    }

    @NonNull
    private Optional<Time> fetchRow(@Nullable Cursor cursor) throws ClockOutBeforeClockInException {
        if (isNull(cursor)) {
            return Optional.empty();
        }

        try {
            if (cursor.moveToFirst()) {
                Time result = cursorMapper.transform(cursor);
                return Optional.of(result);
            }

            return Optional.empty();
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<Time> findProjectTimeSinceStartingPointInMilliseconds(Project project, long milliseconds) throws DomainException {
        requireNonNull(project);

        Cursor cursor = getContentResolver().query(
                ProviderContract.Project.getItemTimeUri(project.getId()),
                ProviderContract.Time.getColumns(),
                TimeColumns.START + ">=?",
                new String[]{String.valueOf(milliseconds)},
                null
        );
        return fetch(cursor);
    }

    @Override
    public Optional<Time> get(final long id) throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.Time.getItemUri(id),
                ProviderContract.Time.getColumns(),
                null,
                null,
                null
        );
        return fetchRow(cursor);
    }

    @Override
    public Optional<Time> add(final Time time) throws ClockOutBeforeClockInException {
        requireNonNull(time);

        final ContentValues values = contentValuesMapper.transform(time);

        final Uri uri = getContentResolver().insert(
                ProviderContract.Time.getStreamUri(),
                values
        );
        return get(Long.parseLong(ProviderContract.Time.getItemId(uri)));
    }

    @Override
    public Optional<Time> update(final Time time) throws ClockOutBeforeClockInException {
        requireNonNull(time);

        getContentResolver().update(
                ProviderContract.Time.getItemUri(time.getId()),
                contentValuesMapper.transform(time),
                null,
                null
        );

        return get(time.getId());
    }

    @Override
    public List<Time> update(List<Time> times) throws ClockOutBeforeClockInException {
        requireNonNull(times);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (Time time : times) {
            Uri uri = ProviderContract.Time.getItemUri(time.getId());

            ContentProviderOperation operation = ContentProviderOperation.newUpdate(uri)
                    .withValues(contentValuesMapper.transform(time))
                    .build();
            batch.add(operation);
        }

        try {
            getContentResolver().applyBatch(ProviderContract.AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            throw new ContentResolverApplyBatchException(e);
        }

        List<Time> updatedTimes = new ArrayList<>();
        for (Time time : times) {
            Optional<Time> value = get(time.getId());
            if (value.isPresent()) {
                updatedTimes.add(value.get());
            }
        }

        return updatedTimes;
    }

    @Override
    public void remove(final long id) {
        getContentResolver().delete(
                ProviderContract.Time.getItemUri(id),
                null,
                null
        );
    }

    @Override
    public void remove(List<Time> times) {
        requireNonNull(times);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (Time time : times) {
            Uri uri = ProviderContract.Time.getItemUri(time.getId());
            batch.add(ContentProviderOperation.newDelete(uri).build());
        }

        try {
            getContentResolver().applyBatch(ProviderContract.AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            throw new ContentResolverApplyBatchException(e);
        }
    }

    @Override
    public List<Time> getProjectTimeSinceBeginningOfMonth(long projectId)
            throws ClockOutBeforeClockInException {
        // Reset the calendar to retrieve timestamp
        // of the beginning of the month.
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        final Cursor cursor = getContentResolver().query(
                ProviderContract.Project.getItemTimeUri(projectId),
                ProviderContract.Time.getColumns(),
                TimeColumns.START + ">=? OR " + TimeColumns.STOP + " = 0",
                new String[]{String.valueOf(calendar.getTimeInMillis())},
                ProviderContract.Project.ORDER_BY_TIME
        );
        return fetch(cursor);
    }

    @Override
    public Optional<Time> getActiveTimeForProject(long projectId)
            throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.Project.getItemTimeUri(projectId),
                ProviderContract.Time.getColumns(),
                TimeColumns.STOP + " = 0",
                null,
                null
        );
        return fetchRow(cursor);
    }
}
