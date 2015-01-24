package me.raatiniemi.worker.mapper;

import android.content.Context;
import android.provider.BaseColumns;

public class ProjectMapper extends AbstractMapper
{
    private static final String TABLE_NAME = "project";

    private interface Columns extends BaseColumns
    {
        public static final String NAME = "name";

        public static final String DESCRIPTION = "description";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
}
