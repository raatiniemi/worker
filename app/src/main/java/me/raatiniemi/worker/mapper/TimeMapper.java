package me.raatiniemi.worker.mapper;

import android.content.Context;
import android.provider.BaseColumns;

public class TimeMapper extends AbstractMapper
{
    private static final String TABLE_NAME = "time";

    private interface Columns extends BaseColumns
    {
        public static final String PROJECT_ID = "project_id";

        public static final String START = "start";

        public static final String STOP = "stop";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.PROJECT_ID + " INTEGER NOT NULL, " +
            Columns.START + " INTEGER NOT NULL, " +
            Columns.STOP + " INTEGER NULL " +
        ");";

    public TimeMapper(Context context)
    {
        super(context);
    }
}
