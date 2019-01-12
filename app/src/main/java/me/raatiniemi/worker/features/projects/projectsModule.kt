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

import me.raatiniemi.worker.domain.interactor.*
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.features.projects.viewmodel.ClockActivityViewModel
import me.raatiniemi.worker.features.projects.viewmodel.ProjectsViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val projectsModule = module {
    viewModel {
        val getProjects = GetProjects(get())
        val getProjectTimeSince = GetProjectTimeSince(get())
        val removeProject = RemoveProject(get())

        ProjectsViewModel(
                keyValueStore = get(),
                getProjects = getProjects,
                getProjectTimeSince = getProjectTimeSince,
                removeProject = removeProject
        )
    }

    viewModel {
        val clockIn = ClockIn(get())
        val clockOut = ClockOut(get())
        val getProjectTimeSince = GetProjectTimeSince(get())

        ClockActivityViewModel(get(), clockIn, clockOut, getProjectTimeSince)
    }

    viewModel {
        val findProject = FindProject(get())
        val createProject = CreateProject(findProject, get())

        CreateProjectViewModel(createProject, findProject)
    }
}
