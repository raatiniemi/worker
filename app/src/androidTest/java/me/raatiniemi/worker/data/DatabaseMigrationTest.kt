/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import me.raatiniemi.worker.data.migrations.Migration1To2
import me.raatiniemi.worker.data.migrations.Migration2To3
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    @Rule
    lateinit var migrationHelper: MigrationTestHelper

    @Before
    fun setUp() {
        migrationHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            Database::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
        )
        prepareDatabaseWithData()
    }

    private fun prepareDatabaseWithData() {
        val database = migrationHelper.createDatabase(DATABASE_NAME, 1)

        database.insert("project", CONFLICT_ABORT, buildProject())
        database.insert("time", CONFLICT_ABORT, buildTimeInterval())

        database.close()
    }

    private fun buildProject(configure: (ContentValues.() -> Unit)? = null): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("_id", 1)
        contentValues.put("name", "Name #1")
        configure?.let { contentValues.it() }

        return contentValues
    }

    private fun buildTimeInterval(configure: (ContentValues.() -> Unit)? = null): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("_id", 1)
        contentValues.put("project_id", 1)
        contentValues.put("start", 1)
        contentValues.put("stop", 2)
        configure?.let { contentValues.it() }

        return contentValues
    }

    @Test
    fun migration1To2() {
        migrationHelper.runMigrationsAndValidate(
            DATABASE_NAME,
            2,
            true,
            Migration1To2()
        )
    }

    @Test
    fun migration1To3() {
        migrationHelper.runMigrationsAndValidate(
            DATABASE_NAME,
            3,
            true,
            Migration1To2(),
            Migration2To3()
        )
    }

    companion object {
        private const val DATABASE_NAME = "migration_test"
    }
}
