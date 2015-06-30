package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.model.time.TimeCollection;
import me.raatiniemi.worker.provider.WorkerContract;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.Groupable;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.TimeChild;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.TimeGroup;
import me.raatiniemi.worker.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;
import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;

public class TimeMapper {
    /**
     * Timestamp for the beginning of the month in milliseconds.
     */
    private final long mBeginningOfMonth;

    private Context mContext;

    public TimeMapper(Context context) {
        super();

        mContext = context;

        // Reset the calendar to retrieve timestamp
        // of the beginning of the month.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        mBeginningOfMonth = calendar.getTimeInMillis();
    }

    public TimeCollection findTimeByProject(Project project) {
        TimeCollection result = new TimeCollection();

        if (null == project || null == project.getId()) {
            return result;
        }

        Cursor rows = mContext.getContentResolver().query(
            ProjectContract.getItemTimeUri(String.valueOf(project.getId())),
            TimeContract.COLUMNS,
            TimeColumns.START + ">=? OR " + TimeColumns.STOP + " = 0",
            new String[]{ String.valueOf(mBeginningOfMonth) },
            ProjectContract.ORDER_BY_TIME
        );
        if (rows.moveToFirst()) {
            do {
                Time time = map(rows);
                if (time != null) {
                    result.add(time);
                }
            } while (rows.moveToNext());
        }
        rows.close();

        return result;
    }

    /**
     * Load a batch of time items grouped as an interval.
     *
     * @param intervalRow Cursor for grouped interval.
     * @param positionOffset Offset for the cursor position.
     * @return Time grouped as interval, or null if no rows are available.
     */
    private Groupable loadGroupable(Cursor intervalRow, int positionOffset) {
        // We're getting the id for the time objects as a comma-separated string column.
        // We have to split the value before attempting to retrieve each individual row.
        String grouped = intervalRow.getString(1);
        String[] rows = grouped.split(",");
        if (0 < rows.length) {
            // Instantiate the group. The first column should be
            // the lowest timestamp within the interval.
            TimeGroup group = new TimeGroup(
                (intervalRow.getPosition() + positionOffset),
                new Date(intervalRow.getLong(0))
            );

            ArrayList<TimeChild> children = new ArrayList<>();

            // Iterate through and retrieve each row.
            for (String id : rows) {
                Cursor row = mContext.getContentResolver()
                    .query(
                        TimeContract.getItemUri(id),
                        TimeContract.COLUMNS,
                        null,
                        null,
                        null
                    );
                if (row.moveToFirst()) {
                    TimeChild child;

                    do {
                        Time time = map(row);
                        if (null != time) {
                            child = new TimeChild((row.getPosition() + positionOffset), time);
                            children.add(child);
                        }
                    } while (row.moveToNext());
                }
                row.close();
            }

            // Reverse the order of the children to put the latest
            // item at the top of the list.
            Collections.reverse(children);
            return new Groupable(group, children);
        }
        return null;
    }

    /**
     * Find and group time as an interval for a specified project.
     *
     * @param projectId Id for project connected to the time.
     * @param offset Where in the iteration to start, e.g. zero for first iteration.
     * @return Time grouped as interval for specified project.
     */
    public List<Groupable> findIntervalByProject(Long projectId, int offset) {
        List<Groupable> result = new ArrayList<>();

        // TODO: Simplify the builing of the URI with query parameters.
        Uri uri = ProjectContract.getItemTimesheetUri(String.valueOf(projectId))
            .buildUpon()
            .appendQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET, String.valueOf(offset))
            .appendQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT, "10")
            .build();

        Cursor cursor = mContext.getContentResolver()
            .query(
                uri,
                ProjectContract.COLUMNS_TIMESHEET,
                null,
                null,
                ProjectContract.ORDER_BY_TIMESHEET
            );
        if (cursor.moveToFirst()) {
            do {
                // Attempt to load the grouped interval, might return null
                // if no rows are available.
                Groupable groupable = loadGroupable(cursor, offset);
                if (null != groupable) {
                    result.add(groupable);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public boolean remove(Time time) {
        return 0 < mContext.getContentResolver().delete(
            TimeContract.getItemUri(String.valueOf(time.getId())),
            null,
            null
        );
    }

    public static Time map(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TimeColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndex(TimeColumns.PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndex(TimeColumns.START));
        long stop = cursor.getLong(cursor.getColumnIndex(TimeColumns.STOP));

        try {
            return new Time(id, projectId, start, stop);
        } catch (DomainException e) {
            // TODO: Handle DomainException properly.
            return null;
        }
    }

    public static ContentValues map(Time time) {
        ContentValues values = new ContentValues();
        values.put(TimeColumns.START, time.getStart());
        values.put(TimeColumns.STOP, time.getStop());
        values.put(TimeColumns.PROJECT_ID, time.getProjectId());

        return values;
    }
}
