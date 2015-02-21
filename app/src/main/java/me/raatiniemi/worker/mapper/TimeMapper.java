package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;

import me.raatiniemi.worker.domain.DomainObject;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.DomainException;

public class TimeMapper extends AbstractMapper<Time>
{
    private static final String TABLE_NAME = "time";

    private interface Columns
    {
        String PROJECT_ID = "project_id";

        String START = "start";

        String STOP = "stop";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.PROJECT_ID + " INTEGER NOT NULL, " +
            Columns.START + " INTEGER NOT NULL, " +
            Columns.STOP + " INTEGER DEFAULT 0 " +
        ");";

    public TimeMapper()
    {
        super();
    }

    protected String getTable()
    {
        return TABLE_NAME;
    }

    protected String[] getColumns()
    {
        return new String[]{
            BaseColumns._ID,
            Columns.PROJECT_ID,
            Columns.START,
            Columns.STOP
        };
    }

    protected Time load(Cursor row)
    {
        long id = row.getLong(row.getColumnIndex(BaseColumns._ID));
        long projectId = row.getLong(row.getColumnIndex(Columns.PROJECT_ID));
        long start = row.getLong(row.getColumnIndex(Columns.START));
        long stop = row.getLong(row.getColumnIndex(Columns.STOP));

        try {
            return new Time(id, projectId, start, stop);
        } catch (DomainException e) {
            // TODO: Handle DomainException properly.
            return null;
        }
    }

    public ArrayList<Time> findTimeByProject(Project project)
    {
        ArrayList<Time> result = new ArrayList<>();

        // Check that the project actually exists, i.e. it has an value for id.
        if (project != null && project.getId() != null) {
            String selection = Columns.PROJECT_ID + "=" + project.getId();
            String orderBy = Columns.STOP + " DESC," + Columns.START + " ASC";

            Cursor rows = mDatabase.query(getTable(), getColumns(), selection, null, null, null, orderBy);
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

    public Time insert(Time time)
    {
        // TODO: Check if timer is already active for project, throw exception.

        ContentValues values = new ContentValues();
        values.put(Columns.PROJECT_ID, time.getProjectId());
        values.put(Columns.START, time.getStart());
        values.put(Columns.STOP, time.getStop());

        long id = mDatabase.insert(getTable(), null, values);
        return find(id);
    }

    public Time update(Time time)
    {
        ContentValues values = new ContentValues();
        values.put(Columns.START, time.getStart());
        values.put(Columns.STOP, time.getStop());

        String where = BaseColumns._ID + "=" + time.getId();

        mDatabase.update(getTable(), values, where, null);
        return find(time.getId());
    }
}
