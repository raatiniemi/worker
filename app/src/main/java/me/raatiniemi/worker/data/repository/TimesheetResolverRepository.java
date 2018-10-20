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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.data.repository.mapper.TimeCursorMapper;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.domain.repository.TimesheetRepository;
import me.raatiniemi.worker.util.Optional;
import timber.log.Timber;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.data.provider.QueryParameter.appendPageRequest;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class TimesheetResolverRepository extends ContentResolverRepository implements TimesheetRepository {
    private static final int TIMESHEET_DATE_CURSOR_INDEX = 0;
    private static final int TIMESHEET_IDS_CURSOR_INDEX = 1;

    private final TimeCursorMapper cursorMapper = new TimeCursorMapper();

    public TimesheetResolverRepository(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    private Optional<TimeInterval> fetchRow(@Nullable Cursor cursor) throws ClockOutBeforeClockInException {
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

    @NonNull
    private Optional<TimeInterval> get(final long id) throws ClockOutBeforeClockInException {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.getTimeItemUri(id),
                ProviderContract.getTimeColumns(),
                null,
                null,
                null
        );
        return fetchRow(cursor);
    }

    @NonNull
    private Map<Date, Set<TimesheetItem>> fetchTimesheet(@Nullable Cursor cursor) {
        if (isNull(cursor)) {
            return Collections.emptyMap();
        }

        Map<Date, Set<TimesheetItem>> result = new LinkedHashMap<>();

        if (cursor.moveToFirst()) {
            do {
                String ids = cursor.getString(TIMESHEET_IDS_CURSOR_INDEX);
                String[] rows = ids.split(",");

                Set<TimesheetItem> segment = getSegmentForTimesheet(rows);
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
    private Set<TimesheetItem> getSegmentForTimesheet(String[] ids) {
        if (0 == ids.length) {
            return Collections.emptySet();
        }

        Set<TimesheetItem> items = new LinkedHashSet<>();
        for (String id : ids) {
            Optional<TimeInterval> value = getSegmentItemForTimesheet(id);
            if (value.isPresent()) {
                items.add(TimesheetItem.with(value.get()));
            }
        }

        return items;
    }

    @NonNull
    private Optional<TimeInterval> getSegmentItemForTimesheet(@NonNull final String id) {
        try {
            return get(Long.valueOf(id));
        } catch (DomainException e) {
            Timber.w(e, "Unable to fetch item for timesheet");

            return Optional.empty();
        }
    }

    @Override
    public Map<Date, Set<TimesheetItem>> getTimesheet(final long projectId, final PageRequest pageRequest) {
        requireNonNull(pageRequest);

        final Uri uri = ProviderContract.getTimesheetStreamUri(projectId);
        final Cursor cursor = getContentResolver().query(
                appendPageRequest(uri, pageRequest),
                ProviderContract.getTimesheetStreamColumns(),
                null,
                null,
                ProviderContract.ORDER_BY_TIMESHEET
        );
        return fetchTimesheet(cursor);
    }

    @Override
    public Map<Date, Set<TimesheetItem>> getTimesheetWithoutRegisteredEntries(long projectId, final PageRequest pageRequest) {
        requireNonNull(pageRequest);

        final Uri uri = ProviderContract.getTimesheetStreamUri(projectId);
        final Cursor cursor = getContentResolver().query(
                appendPageRequest(uri, pageRequest),
                ProviderContract.getTimesheetStreamColumns(),
                ProviderContract.COLUMN_TIME_REGISTERED + " = 0",
                null,
                ProviderContract.ORDER_BY_TIMESHEET
        );
        return fetchTimesheet(cursor);
    }
}
