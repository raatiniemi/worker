/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.koin.modules

import androidx.room.Room
import me.raatiniemi.worker.data.Database
import me.raatiniemi.worker.data.projects.datasource.ProjectDataSourceFactory
import me.raatiniemi.worker.data.projects.datasource.TimeReportWeekDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val dataAndroidTest = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(androidContext(), Database::class.java)
            .allowMainThreadQueries()
            .build()
    }

    single {
        val factory = get<ProjectDataSourceFactory>()
        factory.create()
    }

    single {
        val factory = get<TimeReportWeekDataSource.Factory>()
        factory.create()
    }
}
