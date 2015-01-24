package me.raatiniemi.worker.mapper;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import me.raatiniemi.worker.domain.DomainObject;

public class ProjectMapper extends AbstractMapper
{
    private static final String TABLE_NAME = "project";

    private interface Columns
    {
        String NAME = "name";

        String DESCRIPTION = "description";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.NAME + " TEXT NOT NULL, " +
            Columns.DESCRIPTION + " TEXT NULL, " +
            "UNIQUE (" + Columns.NAME + ") ON CONFLICT ROLLBACK" +
        ");";

    private TimeMapper mTimeMapper;

    public ProjectMapper(Context context, TimeMapper timeMapper)
    {
        super(context);
        mTimeMapper = timeMapper;
    }

    protected DomainObject load(Cursor resultSet)
    {
        // TODO: Implement load for project mapper.
        return null;
    }
}
