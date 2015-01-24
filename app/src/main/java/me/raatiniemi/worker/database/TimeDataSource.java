package me.raatiniemi.worker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

import me.raatiniemi.worker.data.Time;

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

    public ArrayList<Time> getTimeByProjectId(long project_id)
    {
        ArrayList<Time> time = new ArrayList<>();
        String[] columns = new String[]{
            Structure.COLUMN_ID,
            Structure.COLUMN_PROJECT_ID,
            Structure.COLUMN_TIME_START,
            Structure.COLUMN_TIME_STOP
        };

        // TODO: Retrieve time with support for interval, day, week, and month.
        String selection = Structure.COLUMN_PROJECT_ID +"="+ project_id;

        Cursor cursor = mDatabase.query(Structure.TABLE_NAME, columns, selection, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // Populate the time item with its data.
                Time item = new Time();
                item.setId(cursor.getLong(cursor.getColumnIndex(Structure.COLUMN_ID)));
                item.setProjectId(cursor.getLong(cursor.getColumnIndex(Structure.COLUMN_PROJECT_ID)));
                item.setStart(cursor.getLong(cursor.getColumnIndex(Structure.COLUMN_TIME_START)));
                item.setStop(cursor.getLong(cursor.getColumnIndex(Structure.COLUMN_TIME_STOP)));

                time.add(item);
            } while (cursor.moveToNext());
        }

        return time;
    }
}
