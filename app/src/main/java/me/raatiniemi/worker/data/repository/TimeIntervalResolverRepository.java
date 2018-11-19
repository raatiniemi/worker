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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.data.repository.exception.ContentResolverApplyBatchException;
import me.raatiniemi.worker.data.repository.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.repository.mapper.TimeCursorMapper;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.util.Optional;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class TimeIntervalResolverRepository extends ContentResolverRepository implements TimeIntervalRepository {
    private final TimeCursorMapper cursorMapper = new TimeCursorMapper();
    private final TimeContentValuesMapper contentValuesMapper = new TimeContentValuesMapper();

    public TimeIntervalResolverRepository(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    private List<TimeInterval> fetch(@Nullable Cursor cursor) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        if (isNull(cursor)) {
            return timeIntervals;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    timeIntervals.add(cursorMapper.transform(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return timeIntervals;
    }

    @NonNull
    private Optional<TimeInterval> fetchRow(@Nullable Cursor cursor) {
        if (isNull(cursor)) {
            return Optional.empty();
        }

        try {
            if (cursor.moveToFirst()) {
                TimeInterval timeInterval = cursorMapper.transform(cursor);
                return Optional.of(timeInterval);
            }

            return Optional.empty();
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<TimeInterval> findAll(Project project, long milliseconds) {
        requireNonNull(project);

        Cursor cursor = getContentResolver().query(
                ProviderContract.getProjectItemTimeUri(project.getId()),
                ProviderContract.getTimeColumns(),
                ProviderContract.COLUMN_TIME_START + ">=? OR " + ProviderContract.COLUMN_TIME_STOP + " = 0",
                new String[]{String.valueOf(milliseconds)},
                ProviderContract.ORDER_BY_PROJECT_TIME
        );
        return fetch(cursor);
    }

    @Override
    public Optional<TimeInterval> findById(final long id) {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.getTimeItemUri(id),
                ProviderContract.getTimeColumns(),
                null,
                null,
                null
        );
        return fetchRow(cursor);
    }

    @Override
    public Optional<TimeInterval> findActiveByProjectId(long projectId) {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.getProjectItemTimeUri(projectId),
                ProviderContract.getTimeColumns(),
                ProviderContract.COLUMN_TIME_STOP + " = 0",
                null,
                null
        );
        return fetchRow(cursor);
    }

    @Override
    public Optional<TimeInterval> add(final TimeInterval timeInterval) {
        requireNonNull(timeInterval);

        final ContentValues values = contentValuesMapper.transform(timeInterval);

        final Uri uri = getContentResolver().insert(
                ProviderContract.getTimeStreamUri(),
                values
        );
        return findById(Long.parseLong(ProviderContract.getTimeItemId(uri)));
    }

    @Override
    public Optional<TimeInterval> update(final TimeInterval timeInterval) {
        requireNonNull(timeInterval);

        getContentResolver().update(
                ProviderContract.getTimeItemUri(timeInterval.getId()),
                contentValuesMapper.transform(timeInterval),
                null,
                null
        );

        return findById(timeInterval.getId());
    }

    @Override
    public List<TimeInterval> update(List<TimeInterval> timeIntervals) {
        requireNonNull(timeIntervals);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (TimeInterval timeInterval : timeIntervals) {
            Uri uri = ProviderContract.getTimeItemUri(timeInterval.getId());

            ContentProviderOperation operation = ContentProviderOperation.newUpdate(uri)
                    .withValues(contentValuesMapper.transform(timeInterval))
                    .build();
            batch.add(operation);
        }

        try {
            getContentResolver().applyBatch(ProviderContract.AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            throw new ContentResolverApplyBatchException(e);
        }

        List<TimeInterval> updatedTimeIntervals = new ArrayList<>();
        for (TimeInterval timeInterval : timeIntervals) {
            Optional<TimeInterval> value = findById(timeInterval.getId());
            if (value.isPresent()) {
                updatedTimeIntervals.add(value.get());
            }
        }

        return updatedTimeIntervals;
    }

    @Override
    public void remove(final long id) {
        getContentResolver().delete(
                ProviderContract.getTimeItemUri(id),
                null,
                null
        );
    }

    @Override
    public void remove(List<TimeInterval> timeIntervals) {
        requireNonNull(timeIntervals);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (TimeInterval timeInterval : timeIntervals) {
            Uri uri = ProviderContract.getTimeItemUri(timeInterval.getId());
            batch.add(ContentProviderOperation.newDelete(uri).build());
        }

        try {
            getContentResolver().applyBatch(ProviderContract.AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            throw new ContentResolverApplyBatchException(e);
        }
    }
}
