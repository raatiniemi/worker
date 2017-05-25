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
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.data.provider.ProviderContract.ProjectColumns;
import me.raatiniemi.worker.data.provider.ProviderContract.Tables;
import me.raatiniemi.worker.data.provider.ProviderContract.TimeColumns;

import static me.raatiniemi.worker.util.NullUtil.nonNull;
import static org.mockito.Mockito.mock;

public class WorkerDatabaseTest extends RobolectricTestCase {
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        // Create an in-memory database used for running unit tests.
        database = SQLiteDatabase.openDatabase(
                ":memory:",
                null,
                SQLiteDatabase.CREATE_IF_NECESSARY
        );
    }

    @After
    public void tearDown() {
        // If needed close the in-memory database.
        if (nonNull(database) && database.isOpen()) {
            database.close();
        }
        database = null;
    }

    @Test
    public void onCreate_createStructure() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onCreate(database);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onUpgrade_oldVersionIsLessThan1() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onUpgrade(database, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onUpgrade_newVersionIsMoreThanLatestVersion() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        int newVersion = WorkerDatabase.DATABASE_VERSION + 1;
        helper.onUpgrade(database, 1, newVersion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onUpgrade_newVersionIsLessThanOldVersion() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onUpgrade(database, 2, 1);
    }

    @Test
    public void onUpgrade_upgradeFromBaseToLatest() {
        Context context = mock(Context.class);
        WorkerBaseDatabase helper = new WorkerBaseDatabase(context);
        helper.onCreate(database);

        // If no exceptions have been thrown the test should be considered OK.
        helper.onUpgrade(database, 1, WorkerDatabase.DATABASE_VERSION);
    }

    @Test
    public void onUpgrade_upgradeAfterDowngrade() {
        Context context = mock(Context.class);
        WorkerBaseDatabase helper = new WorkerBaseDatabase(context);
        helper.onCreate(database);

        // If no exceptions have been thrown the test should be considered OK.
        helper.onUpgrade(database, 1, WorkerDatabase.DATABASE_VERSION);
        helper.onDowngrade(database, WorkerDatabase.DATABASE_VERSION, 1);
        helper.onUpgrade(database, 1, WorkerDatabase.DATABASE_VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onDowngrade_newVersionIsLessThan1() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onDowngrade(database, 1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onDowngrade_oldVersionIsLessThanNewVersion() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onDowngrade(database, 1, 2);
    }

    /**
     * Represent the base database, used primarily for upgrade related tests.
     */
    private class WorkerBaseDatabase extends WorkerDatabase {
        public WorkerBaseDatabase(Context context) {
            super(context);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create the structure for the `project`-table.
            db.execSQL("CREATE TABLE " + Tables.PROJECT + " ( " +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ProjectColumns.NAME + " TEXT NOT NULL, " +
                    ProjectColumns.DESCRIPTION + " TEXT NULL, " +
                    ProjectColumns.ARCHIVED + " INTEGER DEFAULT 0, " +
                    "UNIQUE (" + ProjectColumns.NAME + ") ON CONFLICT ROLLBACK)");

            // Create the structure for the `time`-table.
            db.execSQL("CREATE TABLE " + Tables.TIME + " ( " +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TimeColumns.PROJECT_ID + " INTEGER NOT NULL, " +
                    TimeColumns.START + " INTEGER NOT NULL, " +
                    TimeColumns.STOP + " INTEGER DEFAULT 0)");
        }
    }
}
