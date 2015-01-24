package me.raatiniemi.worker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class TimeDataSource
{
    public interface Structure extends BaseColumns
    {
        public static final String TABLE_NAME = "time";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_PROJECT_ID = "project_id";

        public static final String COLUMN_TIME_START = "start";

        public static final String COLUMN_TIME_STOP = "stop";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + Structure.TABLE_NAME + " ( " +
            Structure.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Structure.COLUMN_PROJECT_ID + " INTEGER NOT NULL, " +
            Structure.COLUMN_TIME_START + " INTEGER NOT NULL, " +
            Structure.COLUMN_TIME_STOP + " INTEGER NOT NULL " +
        ");";

    protected Helper mHelper;

    protected SQLiteDatabase mDatabase;

    public TimeDataSource(Helper helper)
    {
        mHelper = helper;
        mDatabase = mHelper.getWritableDatabase();
    }

    public TimeDataSource(Context context)
    {
        this(Helper.getInstance(context));
    }
}
