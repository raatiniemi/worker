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
import me.raatiniemi.worker.data.datasource.ProjectDataSource
import me.raatiniemi.worker.data.datasource.TimeReportWeekDataSource
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.data.room.migration.Migration1To2
import me.raatiniemi.worker.data.room.migration.Migration2To3
import me.raatiniemi.worker.data.room.repository.ProjectRoomRepository
import me.raatiniemi.worker.data.room.repository.TimeIntervalRoomRepository
import me.raatiniemi.worker.data.room.repository.TimeReportRoomRepository
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.project.usecase.countProjects
import me.raatiniemi.worker.domain.project.usecase.findProjects
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val data = module {
    single {
        Room.databaseBuilder(androidContext(), Database::class.java, "worker")
            .allowMainThreadQueries()
            .addMigrations(Migration1To2(), Migration2To3())
            .build()
    }

    single<ProjectRepository> {
        val database: Database = get()

        ProjectRoomRepository(database.projects())
    }

    single<TimeIntervalRepository> {
        val database: Database = get()

        TimeIntervalRoomRepository(database.timeIntervals())
    }

    single<TimeReportRepository> {
        val database: Database = get()

        TimeReportRoomRepository(database.timeReport(), database.timeIntervals())
    }

    single {
        val repository = get<ProjectRepository>()
        val countProjects = countProjects(repository)
        val findProjects = findProjects(repository)

        ProjectDataSource.Factory(countProjects, findProjects)
    }

    single {
        TimeReportWeekDataSource.Factory(
            projectProvider = get(),
            countTimeReportWeeks = get(),
            findTimeReportWeeks = get()
        )
    }
}
