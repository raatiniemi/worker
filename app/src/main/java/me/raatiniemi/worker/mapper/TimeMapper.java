package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.model.time.TimeCollection;
import me.raatiniemi.worker.provider.ExpandableDataProvider.Child;
import me.raatiniemi.worker.provider.ExpandableDataProvider.Groupable;
import me.raatiniemi.worker.provider.TimesheetExpandableDataProvider.TimeChild;
import me.raatiniemi.worker.provider.TimesheetExpandableDataProvider.TimeGroup;
import me.raatiniemi.worker.provider.WorkerContract.Tables;
import me.raatiniemi.worker.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;

public class TimeMapper extends AbstractMapper<Time> {
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

    protected String getTable() {
        return Tables.TIME;
    }

    protected String[] getColumns() {
        return TimeContract.COLUMNS;
    }

    protected Time load(Cursor row) {
        long id = row.getLong(row.getColumnIndex(TimeColumns.ID));
        long projectId = row.getLong(row.getColumnIndex(TimeColumns.PROJECT_ID));
        long start = row.getLong(row.getColumnIndex(TimeColumns.START));
        long stop = row.getLong(row.getColumnIndex(TimeColumns.STOP));

        try {
            return new Time(id, projectId, start, stop);
        } catch (DomainException e) {
            // TODO: Handle DomainException properly.
            return null;
        }
    }

    public TimeCollection findTimeByProject(Project project) {
        TimeCollection result = new TimeCollection();

        // Check that the project actually exists, i.e. it has an value for id.
        if (project != null && project.getId() != null) {
            String selection = TimeColumns.PROJECT_ID + "=" + project.getId() + " AND (" + TimeColumns.START + ">=? OR " + TimeColumns.STOP + " = 0)";
            String[] selectionArgs = new String[]{ String.valueOf(mBeginningOfMonth) };
            String orderBy = TimeColumns.STOP + " ASC," + TimeColumns.START + " ASC";

            Cursor rows = mDatabase.query(getTable(), getColumns(), selection, selectionArgs, null, null, orderBy);
            if (rows.moveToFirst()) {
                do {
                    Time time = load(rows);
                    if (time != null) {
                        result.add(time);
                    }
                } while (rows.moveToNext());
            }
        }

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

            ArrayList<Child> children = new ArrayList<>();

            String selection = TimeColumns.ID + " = ?";
            String[] selectionArgs;

            // Iterate through and retrieve each row.
            for (String id : rows) {
                selectionArgs = new String[]{ id };

                Cursor row = mDatabase.query(getTable(), getColumns(), selection, selectionArgs, null, null, null);
                if (row.moveToFirst()) {
                    TimeChild child;

                    do {
                        Time time = load(row);
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
     * @param project Project connected to the time.
     * @param start Where in the iteration to start, e.g. zero for first iteration.
     * @return Time grouped as interval for specified project.
     */
    public List<Groupable> findIntervalByProject(Project project, int start) {
        List<Groupable> result = new ArrayList<>();

        // Check that the project is a valid candidate, i.e. it's an existing project.
        if (null != project && null != project.getId()) {
            // We have to group each of the time objects related to the interval.
            String[] columns = new String[]{
                "MIN(start) AS date",
                "GROUP_CONCAT(" + TimeColumns.ID + ")"
            };

            String selection = TimeColumns.PROJECT_ID + "=" + project.getId();

            // Since we're storing everything registered time as milliseconds we have to
            // convert it to seconds and then group it by the desired interval.
            String groupBy = "strftime('%Y%m%d', start / 1000, 'unixepoch')";
            String orderBy = TimeColumns.START + " DESC," + TimeColumns.STOP + " DESC";

            // Build the limit section, the start control where in the
            // result we should begin fetching the rows.
            String limit = start + ", 10";

            Cursor intervalRow = mDatabase.query(getTable(), columns, selection, null, groupBy, null, orderBy, limit);
            if (intervalRow.moveToFirst()) {
                do {
                    // Attempt to load the grouped interval, might return null
                    // if no rows are available.
                    Groupable groupable = loadGroupable(intervalRow, start);
                    if (null != groupable) {
                        result.add(groupable);
                    }
                } while (intervalRow.moveToNext());
            }
            intervalRow.close();
        }

        return result;
    }

    /**
     * Find and group time as an interval for a specified project.
     *
     * @param project Project connected to the time.
     * @return Time grouped as interval for specified project.
     */
    public List<Groupable> findIntervalByProject(Project project) {
        return findIntervalByProject(project, 0);
    }

    public Time insert(Time time) {
        // TODO: Check if timer is already active for project, throw exception.

        ContentValues values = new ContentValues();
        values.put(TimeColumns.PROJECT_ID, time.getProjectId());
        values.put(TimeColumns.START, time.getStart());
        values.put(TimeColumns.STOP, time.getStop());

        long id = mDatabase.insert(getTable(), null, values);

        // Retrieve the time item from the database.
        return find(id);
    }

    public Time update(Time time) {
        ContentValues values = new ContentValues();
        values.put(TimeColumns.START, time.getStart());
        values.put(TimeColumns.STOP, time.getStop());

        String where = TimeColumns.ID + "=" + time.getId();

        mDatabase.update(getTable(), values, where, null);

        // Retrieve the time item from the database.
        return find(time.getId());
    }

    public boolean remove(Time time) {
        String where = TimeColumns.ID + "=" + time.getId();

        return 0 < mDatabase.delete(getTable(), where, null);
    }
}
