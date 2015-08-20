package me.raatiniemi.worker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.raatiniemi.worker.provider.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.provider.WorkerContract.Tables;
import me.raatiniemi.worker.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.util.Worker;

/**
 * Handler for the worker sqlite database.
 */
public class WorkerDatabase extends SQLiteOpenHelper {
    /**
     * Instantiate the database helper with the application context.
     *
     * @param context Application context.
     */
    public WorkerDatabase(Context context) {
        super(context, Worker.DATABASE_NAME, null, Worker.DATABASE_VERSION);
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
     * @throws IllegalArgumentException If oldVersion is less than 1.
     * @throws IllegalArgumentException If newVersion is more than `Worker.DATABASE_VERSION`.
     * @throws IllegalArgumentException If newVersion is less than oldVersion, i.e. downgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since the first version of the structure was 1, upgrading from any
        // value less than 1 is not allowed.
        if (1 > oldVersion) {
            throw new IllegalArgumentException(
                "oldVersion cannot be less than 1"
            );
        }

        // Check the state of the newVersion, we cannot allow to upgrade past
        // the latest available version (i.e. `Worker.DATABASE_VERSION`).
        if (Worker.DATABASE_VERSION < newVersion) {
            throw new IllegalArgumentException(
                "newVersion cannot be more than " + Worker.DATABASE_VERSION
            );
        }

        // Check the relation between oldVersion and newVersion, downgrade via
        // the `onUpgrade`-method is not allowed.
        if (oldVersion > newVersion) {
            throw new IllegalArgumentException(
                "newVersion cannot be less than oldVersion"
            );
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since the first version of the structure was 1, downgrading to any
        // value less than 1 is not allowed.
        if (1 > newVersion) {
            throw new IllegalArgumentException(
                "newVersion cannot be less than 1"
            );
        }

        // Check the relation between oldVersion and newVersion, upgrade via
        // the `onDowngrade`-method is not allowed.
        if (oldVersion < newVersion) {
            throw new IllegalArgumentException(
                "oldVersion cannot be less than newVersion"
            );
        }
    }
}
