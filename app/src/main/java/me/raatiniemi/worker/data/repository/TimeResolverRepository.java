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
import java.util.List;
import java.util.Map;

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
import timber.log.Timber;

import static me.raatiniemi.worker.data.provider.QueryParameter.appendPageRequest;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class TimeResolverRepository
        extends ContentResolverRepository<TimeCursorMapper, TimeContentValuesMapper>
        implements TimeRepository, TimesheetRepository {
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

    @NonNull
    private List<Time> fetch(@Nullable Cursor cursor) throws ClockOutBeforeClockInException {
        List<Time> results = new ArrayList<>();
        if (isNull(cursor)) {
            return results;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    results.add(getCursorMapper().transform(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return results;
    }

    @Nullable
    private Time fetchRow(@Nullable Cursor cursor) throws ClockOutBeforeClockInException {
        if (isNull(cursor)) {
            return null;
        }

        Time result = null;
        try {
            if (cursor.moveToFirst()) {
                result = getCursorMapper().transform(cursor);
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Time> findProjectTimeSinceStartingPointInMilliseconds(Project project, long milliseconds) throws DomainException {
        Cursor cursor = getContentResolver().query(
                ProjectContract.getItemTimeUri(project.getId()),
                TimeContract.getColumns(),
                TimeColumns.START + ">=?",
                new String[]{String.valueOf(milliseconds)},
                null
        );
        return fetch(cursor);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Time get(final long id) throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                TimeContract.getItemUri(id),
                TimeContract.getColumns(),
                null,
                null,
                null
        );
        return fetchRow(cursor);
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
        return get(Long.parseLong(TimeContract.getItemId(uri)));
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
    public List<Time> update(List<Time> times) throws ClockOutBeforeClockInException {
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (Time time : times) {
            Uri uri = TimeContract.getItemUri(time.getId());

            ContentProviderOperation operation = ContentProviderOperation.newUpdate(uri)
                    .withValues(getContentValuesMapper().transform(time))
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
            updatedTimes.add(get(time.getId()));
        }

        return updatedTimes;
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
    public void remove(List<Time> times) {
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

    /**
     * @inheritDoc
     */
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
    private Map<Date, List<Time>> fetchTimesheet(@Nullable Cursor cursor) {
        Map<Date, List<Time>> result = new LinkedHashMap<>();
        if (isNull(cursor)) {
            return result;
        }

        if (cursor.moveToFirst()) {
            do {
                String ids = cursor.getString(1);
                String[] rows = ids.split(",");
                if (0 == rows.length) {
                    continue;
                }

                List<Time> items = new ArrayList<>();
                for (String id : rows) {
                    try {
                        items.add(get(Long.parseLong(id)));
                    } catch (DomainException e) {
                        Timber.w(e, "Unable to fetch item for timesheet");
                    }
                }

                // Reverse the order of the children to put the latest
                // item at the top of the list.
                Collections.reverse(items);

                Date date = new Date(cursor.getLong(0));
                result.put(date, items);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<Date, List<Time>> getTimesheet(final long projectId, final PageRequest pageRequest) {
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
    public Map<Date, List<Time>> getTimesheetWithoutRegisteredEntries(long projectId, final PageRequest pageRequest) {
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

    /**
     * @inheritDoc
     */
    @Override
    public Time getActiveTimeForProject(long projectId)
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
