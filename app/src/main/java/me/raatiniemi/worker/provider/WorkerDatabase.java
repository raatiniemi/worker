package me.raatiniemi.worker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.raatiniemi.worker.application.Worker;
import me.raatiniemi.worker.provider.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.provider.WorkerContract.Tables;
import me.raatiniemi.worker.provider.WorkerContract.TimeColumns;

/**
 * Handler for the worker sqlite database.
 */
public class WorkerDatabase extends SQLiteOpenHelper {
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
     * Instantiate the database helper with the application context.
     *
     * @param context Application context.
     */
    public WorkerDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Retrieve the instance for the database helper, instantiate if necessary.
     *
     * @return Instance for the database helper.
     */
    public static synchronized WorkerDatabase getInstance() {
        if (null == mWorkerDatabase) {
            mWorkerDatabase = new WorkerDatabase(Worker.getContext());
        }
        return mWorkerDatabase;
    }

    /**
     * Creates the initial table structure for the database.
     *
     * @param db Instance for the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table structure for the projects.
        db.execSQL("CREATE TABLE " + Tables.PROJECT + " ( " +
            ProjectColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProjectColumns.NAME + " TEXT NOT NULL, " +
            ProjectColumns.DESCRIPTION + " TEXT NULL, " +
            ProjectColumns.ARCHIVED + " INTEGER DEFAULT 0, " +
            "UNIQUE (" + ProjectColumns.NAME + ") ON CONFLICT ROLLBACK)");

        // Table structure for the registered time.
        db.execSQL("CREATE TABLE " + Tables.TIME + " ( " +
            TimeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TimeColumns.PROJECT_ID + " INTEGER NOT NULL, " +
            TimeColumns.START + " INTEGER NOT NULL, " +
            TimeColumns.STOP + " INTEGER DEFAULT 0)");
    }

    /**
     * Handles upgrade events for the database structure.
     *
     * @param db Instance for the database.
     * @param oldVersion Old version of the structure.
     * @param newVersion New version of the structure.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
