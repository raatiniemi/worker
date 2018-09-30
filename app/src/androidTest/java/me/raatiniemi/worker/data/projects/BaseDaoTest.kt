/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.data.projects

import android.content.Context
import androidx.annotation.CallSuper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import me.raatiniemi.worker.data.Database
import org.junit.After
import org.junit.Before

open class BaseDaoTest {
    private lateinit var database: Database

    @CallSuper
    @Before
    open fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    internal val projects: ProjectDao by lazy {
        database.projects()
    }

    internal val timeIntervals: TimeIntervalDao by lazy {
        database.timeIntervals()
    }
}
