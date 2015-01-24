package me.raatiniemi.worker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

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
            Structure.COLUMN_TIME_START + " INTEGER NULL, " +
            Structure.COLUMN_TIME_STOP + " INTEGER NULL " +
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
        String orderBy = Structure.COLUMN_TIME_START + " DESC";

        Cursor cursor = mDatabase.query(Structure.TABLE_NAME, columns, selection, null, null, null, orderBy);
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

    public Time findTimeById(long id)
    {
        String[] columns = new String[]{
            Structure.COLUMN_ID,
            Structure.COLUMN_PROJECT_ID,
            Structure.COLUMN_TIME_START,
            Structure.COLUMN_TIME_STOP
        };

        String selection = Structure.COLUMN_ID +"="+ id;

        Cursor row = mDatabase.query(Structure.TABLE_NAME, columns, selection, null, null, null, null);
        if (!row.moveToFirst()) {
            Log.d("findTimeById", "No time item with id: "+ id);
            return null;
        }

        Time time = new Time();
        time.setId(row.getLong(row.getColumnIndex(Structure.COLUMN_ID)));
        time.setProjectId(row.getLong(row.getColumnIndex(Structure.COLUMN_PROJECT_ID)));
        time.setStart(row.getLong(row.getColumnIndex(Structure.COLUMN_TIME_START)));
        time.setStop(row.getLong(row.getColumnIndex(Structure.COLUMN_TIME_STOP)));

        return time;
    }

    public Time startTimerForProject(long project_id)
    {
        // TODO: Check if timer is already active for project, throw exception.

        ContentValues values = new ContentValues();
        values.put(Structure.COLUMN_PROJECT_ID, project_id);
        values.put(Structure.COLUMN_TIME_START, (new Date().getTime()));

        long id = mDatabase.insert(Structure.TABLE_NAME, null, values);
        return findTimeById(id);
    }
}
