package me.raatiniemi.worker.mapper;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import me.raatiniemi.worker.domain.DomainObject;

public class TimeMapper extends AbstractMapper
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
            Columns.STOP + " INTEGER NULL " +
        ");";

    public TimeMapper(Context context)
    {
        super(context);
    }

    protected String[] getColumns()
    {
        // TODO: Implement getColumns for time mapper.
        return null;
    }

    protected DomainObject load(Cursor row)
    {
        // TODO: Implement load for time mapper.
        return null;
    }
}
