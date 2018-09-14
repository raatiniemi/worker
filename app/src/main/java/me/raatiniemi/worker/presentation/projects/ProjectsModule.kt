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

package me.raatiniemi.worker.presentation.projects

import me.raatiniemi.worker.domain.interactor.*
import me.raatiniemi.worker.presentation.projects.viewmodel.*
import org.koin.dsl.module.module

val projectsModule = module {
    single {
        val getProjects = GetProjects(get(), get())
        val getProjectTimeSince = GetProjectTimeSince(get())

        ProjectsViewModel.ViewModel(getProjects, getProjectTimeSince)
    }

    single {
        val clockIn = ClockIn(get())
        val clockOut = ClockOut(get())
        val clockActivityChange = ClockActivityChange(get(), get(), clockIn, clockOut)
        val getProjectTimeSince = GetProjectTimeSince(get())

        ClockActivityViewModel.ViewModel(clockActivityChange, getProjectTimeSince)
    }

    single {
        RemoveProjectViewModel.ViewModel(RemoveProject(get()))
    }

    single { RefreshActiveProjectsViewModel.ViewModel() }

    single {
        CreateProjectViewModel.ViewModel(CreateProject(get()))
    }
}
