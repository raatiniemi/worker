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

package me.raatiniemi.worker.features.projects

import me.raatiniemi.worker.domain.usecase.*
import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.projects.all.viewmodel.AllProjectsViewModel
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import me.raatiniemi.worker.features.projects.model.ProjectProvider
import me.raatiniemi.worker.features.projects.timereport.viewmodel.TimeReportViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val projectsModule = module {
    viewModel {
        val getProjectTimeSince = GetProjectTimeSince(get())
        val clockIn = ClockIn(get())
        val clockOut = ClockOut(get())
        val removeProject = RemoveProject(get())

        AllProjectsViewModel(
            keyValueStore = get(),
            usageAnalytics = get(),
            projectDataSourceFactory = get(),
            getProjectTimeSince = getProjectTimeSince,
            clockIn = clockIn,
            clockOut = clockOut,
            removeProject = removeProject
        )
    }

    viewModel {
        val findProject = FindProject(get())
        val createProject = CreateProject(findProject, get())

        CreateProjectViewModel(get(), createProject, findProject)
    }

    single {
        ProjectHolder()
    }

    single<ProjectProvider> {
        get<ProjectHolder>()
    }

    single<HoursMinutesFormat> {
        DigitalHoursMinutesIntervalFormat()
    }

    viewModel {
        TimeReportViewModel(
            keyValueStore = get(),
            usageAnalytics = get(),
            timeReportDataSourceFactory = get(),
            markRegisteredTime = MarkRegisteredTime(get()),
            removeTime = RemoveTime(get())
        )
    }
}
