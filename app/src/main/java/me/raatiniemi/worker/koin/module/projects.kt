/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.koin.module

import me.raatiniemi.worker.domain.date.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.feature.projects.all.viewmodel.AllProjectsViewModel
import me.raatiniemi.worker.feature.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.feature.projects.model.ProjectHolder
import me.raatiniemi.worker.feature.projects.model.ProjectProvider
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val projects = module {
    viewModel {
        AllProjectsViewModel(
            keyValueStore = get(),
            usageAnalytics = get(),
            countProjects = get(),
            findProjects = get(),
            getProjectTimeSince = get(),
            clockIn = get(),
            clockOut = get(),
            removeProject = get()
        )
    }

    viewModel {
        CreateProjectViewModel(
            usageAnalytics = get(),
            createProject = get(),
            findProject = get(),
            dispatchProvider = get()
        )
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
            projectProvider = get(),
            countTimeReportWeeks = get(),
            findTimeReportWeeks = get(),
            markRegisteredTime = get(),
            removeTime = get()
        )
    }
}
