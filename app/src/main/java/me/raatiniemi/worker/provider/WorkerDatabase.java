package me.raatiniemi.worker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.raatiniemi.worker.application.Worker;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.provider.WorkerContract.*;

public class WorkerDatabase extends SQLiteOpenHelper
{
    public interface Tables
    {
        String PROJECT = "project";
        String TIME = "time";
    }

    /**
     * Name of the database.
     */
    private static final String DATABASE_NAME = "worker";

    /**
     * Version of the database structure.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Instance for the database helper.
     */
    private static WorkerDatabase mWorkerDatabase;

    /**
     * Retrieve the instance for the database helper, instantiate if necessary.
     * @return Instance for the database helper.
     */
    public static synchronized WorkerDatabase getInstance()
    {
        if ( mWorkerDatabase == null ) {
            mWorkerDatabase = new WorkerDatabase(Worker.getContext());
        }
        return mWorkerDatabase;
    }

    /**
     * Instantiate the database helper with the application context.
     * @param context Application context.
     */
    public WorkerDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + Tables.PROJECT + " ( " +
            ProjectColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProjectColumns.NAME + " TEXT NOT NULL, " +
            ProjectColumns.DESCRIPTION + " TEXT NULL, " +
            ProjectColumns.ARCHIVED + " INTEGER DEFAULT 0, " +
            "UNIQUE (" + ProjectColumns.NAME + ") ON CONFLICT ROLLBACK)");

        db.execSQL("CREATE TABLE " + Tables.TIME + " ( " +
            TimeColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TimeColumns.PROJECT_ID + " INTEGER NOT NULL, " +
            TimeColumns.START + " INTEGER NOT NULL, " +
            TimeColumns.STOP + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
