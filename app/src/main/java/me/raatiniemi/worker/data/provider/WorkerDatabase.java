/*
 * Copyright (C) 2017 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.data.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import me.raatiniemi.worker.WorkerApplication;

/**
 * A helper class to manage database creation and version management.
 */
public class WorkerDatabase extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 2;

    private WorkerDatabase(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    /**
     * Constructor.
     *
     * @param context Context used with the database.
     */
    public WorkerDatabase(Context context) {
        this(context, WorkerApplication.DATABASE_NAME);
    }

    public static WorkerDatabase inMemory(Context context) {
        return new WorkerDatabase(context, null);
    }

    /**
     * Check if column exists in table.
     *
     * @param db     The database.
     * @param table  Name of the table.
     * @param column Name of the column.
     * @return true if column exists, otherwise false.
     */
    private static boolean columnExists(SQLiteDatabase db, String table, String column) {
        boolean exists = false;

        // get the table structure and check if the column exists.
        Cursor cursor = db.rawQuery("pragma table_info(" + table + ")", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name.equalsIgnoreCase(column)) {
                exists = true;
                break;
            }
        }
        cursor.close();

        return exists;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the structure for the `project`-table.
        db.execSQL("CREATE TABLE " + ProviderContract.TABLE_PROJECT + " ( " +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProviderContract.COLUMN_PROJECT_NAME + " TEXT NOT NULL, " +
                ProviderContract.COLUMN_PROJECT_DESCRIPTION + " TEXT NULL, " +
                ProviderContract.COLUMN_PROJECT_ARCHIVED + " INTEGER DEFAULT 0, " +
                "UNIQUE (" + ProviderContract.COLUMN_PROJECT_NAME + ") ON CONFLICT ROLLBACK)");

        // Create the structure for the `time`-table.
        db.execSQL("CREATE TABLE " + ProviderContract.TABLE_TIME + " ( " +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProviderContract.COLUMN_TIME_PROJECT_ID + " INTEGER NOT NULL, " +
                ProviderContract.COLUMN_TIME_START + " INTEGER NOT NULL, " +
                ProviderContract.COLUMN_TIME_STOP + " INTEGER DEFAULT 0, " +
                ProviderContract.COLUMN_TIME_REGISTERED + " INTEGER NOT NULL DEFAULT 0)");
    }

    /**
     * Upgrade the database.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     * @throws IllegalArgumentException If oldVersion is less than 1.
     * @throws IllegalArgumentException If newVersion is more than `WorkerApplication.DATABASE_VERSION`.
     * @throws IllegalArgumentException If newVersion is less than oldVersion, i.e. downgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        checkVersionsForUpgrade(oldVersion, newVersion);

        // since sqlite is unable to remove columns we need to check if the
        // column already exists before adding it.
        if (!columnExists(db, ProviderContract.TABLE_TIME, ProviderContract.COLUMN_TIME_REGISTERED)) {
            // Add the `registered`-column to the `time`-table.
            db.execSQL("ALTER TABLE " + ProviderContract.TABLE_TIME +
                    " ADD COLUMN " + ProviderContract.COLUMN_TIME_REGISTERED +
                    " INTEGER NOT NULL DEFAULT 0");
        }
    }

    private static void checkVersionsForUpgrade(int oldVersion, int newVersion) {
        boolean isOldVersionLessThanFirstVersion = 1 > oldVersion;
        if (isOldVersionLessThanFirstVersion) {
            throw new IllegalArgumentException(
                    "oldVersion cannot be less than 1"
            );
        }

        boolean isNewVersionHigherThanLatestVersion = DATABASE_VERSION < newVersion;
        if (isNewVersionHigherThanLatestVersion) {
            throw new IllegalArgumentException(
                    "newVersion cannot be more than " + DATABASE_VERSION
            );
        }

        boolean isOldVersionHigherThanNewVersion = oldVersion > newVersion;
        if (isOldVersionHigherThanNewVersion) {
            throw new IllegalArgumentException(
                    "newVersion cannot be less than oldVersion"
            );
        }
    }

    /**
     * Downgrade the database.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     * @throws IllegalArgumentException If newVersion is less than 1.
     * @throws IllegalArgumentException If oldVersion is less than newVersion, i.e. upgrade.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        checkVersionsForDowngrade(oldVersion, newVersion);
    }

    private static void checkVersionsForDowngrade(int oldVersion, int newVersion) {
        boolean isNewVersionLessThanFirstVersion = 1 > newVersion;
        if (isNewVersionLessThanFirstVersion) {
            throw new IllegalArgumentException(
                    "newVersion cannot be less than 1"
            );
        }

        boolean isOldVersionLessThanNewVersion = oldVersion < newVersion;
        if (isOldVersionLessThanNewVersion) {
            throw new IllegalArgumentException(
                    "oldVersion cannot be less than newVersion"
            );
        }
    }
}
