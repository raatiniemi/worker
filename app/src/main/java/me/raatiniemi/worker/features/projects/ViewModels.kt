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

import me.raatiniemi.worker.features.projects.viewmodel.ClockActivityViewModel
import me.raatiniemi.worker.features.projects.viewmodel.ProjectsViewModel
import me.raatiniemi.worker.features.projects.viewmodel.RefreshActiveProjectsViewModel
import me.raatiniemi.worker.features.projects.viewmodel.RemoveProjectViewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ViewModels : KoinComponent {
    val clockActivity: ClockActivityViewModel.ViewModel by inject()
    val projects: ProjectsViewModel.ViewModel by inject()
    val refreshActiveProjects: RefreshActiveProjectsViewModel by inject()
    val removeProject: RemoveProjectViewModel.ViewModel by inject()
}