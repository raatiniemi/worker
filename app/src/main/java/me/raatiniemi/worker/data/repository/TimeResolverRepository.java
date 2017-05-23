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
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.provider.WorkerContract;
import me.raatiniemi.worker.data.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.data.provider.WorkerContract.TimeContract;
import me.raatiniemi.worker.data.repository.exception.ContentResolverApplyBatchException;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.domain.repository.TimesheetRepository;
import me.raatiniemi.worker.util.Optional;
import timber.log.Timber;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.data.provider.QueryParameter.appendPageRequest;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class TimeResolverRepository extends ContentResolverRepository implements TimeRepository, TimesheetRepository {
    private static final int TIMESHEET_DATE_CURSOR_INDEX = 0;
    private static final int TIMESHEET_IDS_CURSOR_INDEX = 1;
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

    @Nullable
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
                ProjectContract.getItemTimeUri(project.getId()),
                TimeContract.getColumns(),
                TimeColumns.START + ">=?",
                new String[]{String.valueOf(milliseconds)},
                null
        );
        return fetch(cursor);
    }

    @Override
    public Optional<Time> get(final long id) throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                TimeContract.getItemUri(id),
                TimeContract.getColumns(),
                null,
                null,
                null
        );
        return fetchRow(cursor);
    }

    private Optional<Time> get(final String id) throws ClockOutBeforeClockInException {
        return get(Long.valueOf(id));
    }

    @Override
    public Optional<Time> add(final Time time) throws ClockOutBeforeClockInException {
        requireNonNull(time);

        final ContentValues values = contentValuesMapper.transform(time);

        final Uri uri = getContentResolver().insert(
                TimeContract.getStreamUri(),
                values
        );
        return get(Long.parseLong(TimeContract.getItemId(uri)));
    }

    @Override
    public Optional<Time> update(final Time time) throws ClockOutBeforeClockInException {
        requireNonNull(time);

        getContentResolver().update(
                TimeContract.getItemUri(time.getId()),
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
            Uri uri = TimeContract.getItemUri(time.getId());

            ContentProviderOperation operation = ContentProviderOperation.newUpdate(uri)
                    .withValues(contentValuesMapper.transform(time))
                    .build();
            batch.add(operation);
        }

        try {
            getContentResolver().applyBatch(WorkerContract.AUTHORITY, batch);
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
                TimeContract.getItemUri(id),
                null,
                null
        );
    }

    @Override
    public void remove(List<Time> times) {
        requireNonNull(times);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (Time time : times) {
            Uri uri = TimeContract.getItemUri(time.getId());
            batch.add(ContentProviderOperation.newDelete(uri).build());
        }

        try {
            getContentResolver().applyBatch(WorkerContract.AUTHORITY, batch);
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
                ProjectContract.getItemTimeUri(projectId),
                TimeContract.getColumns(),
                TimeColumns.START + ">=? OR " + TimeColumns.STOP + " = 0",
                new String[]{String.valueOf(calendar.getTimeInMillis())},
                ProjectContract.ORDER_BY_TIME
        );
        return fetch(cursor);
    }

    @NonNull
    private Map<Date, Set<Time>> fetchTimesheet(@Nullable Cursor cursor) {
        if (isNull(cursor)) {
            return Collections.emptyMap();
        }

        Map<Date, Set<Time>> result = new LinkedHashMap<>();

        if (cursor.moveToFirst()) {
            do {
                String ids = cursor.getString(TIMESHEET_IDS_CURSOR_INDEX);
                String[] rows = ids.split(",");

                Set<Time> segment = getSegmentForTimesheet(rows);
                if (segment.isEmpty()) {
                    continue;
                }

                Date date = new Date(cursor.getLong(TIMESHEET_DATE_CURSOR_INDEX));
                result.put(date, segment);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    @NonNull
    private Set<Time> getSegmentForTimesheet(String[] ids) {
        if (0 == ids.length) {
            return Collections.emptySet();
        }

        Set<Time> items = new LinkedHashSet<>();
        for (String id : ids) {
            Optional<Time> value = getSegmentItemForTimesheet(id);
            if (value.isPresent()) {
                items.add(value.get());
            }
        }

        return items;
    }

    @NonNull
    private Optional<Time> getSegmentItemForTimesheet(@NonNull final String id) {
        try {
            return get(id);
        } catch (DomainException e) {
            Timber.w(e, "Unable to fetch item for timesheet");

            return Optional.empty();
        }
    }

    @Override
    public Map<Date, Set<Time>> getTimesheet(final long projectId, final PageRequest pageRequest) {
        requireNonNull(pageRequest);

        final Uri uri = ProjectContract.getItemTimesheetUri(projectId);
        final Cursor cursor = getContentResolver().query(
                appendPageRequest(uri, pageRequest),
                ProjectContract.getTimesheetColumns(),
                null,
                null,
                ProjectContract.ORDER_BY_TIMESHEET
        );
        return fetchTimesheet(cursor);
    }

    @Override
    public Map<Date, Set<Time>> getTimesheetWithoutRegisteredEntries(long projectId, final PageRequest pageRequest) {
        requireNonNull(pageRequest);

        final Uri uri = ProjectContract.getItemTimesheetUri(projectId);
        final Cursor cursor = getContentResolver().query(
                appendPageRequest(uri, pageRequest),
                ProjectContract.getTimesheetColumns(),
                TimeColumns.REGISTERED + " = 0",
                null,
                ProjectContract.ORDER_BY_TIMESHEET
        );
        return fetchTimesheet(cursor);
    }

    @Override
    public Optional<Time> getActiveTimeForProject(long projectId)
            throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                ProjectContract.getItemTimeUri(projectId),
                TimeContract.getColumns(),
                TimeColumns.STOP + " = 0",
                null,
                null
        );
        return fetchRow(cursor);
    }
}
