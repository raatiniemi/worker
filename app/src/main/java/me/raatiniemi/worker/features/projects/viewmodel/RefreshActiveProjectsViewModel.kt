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

package me.raatiniemi.worker.features.projects.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.features.projects.model.ProjectsItem

class RefreshActiveProjectsViewModel: ViewModel() {
    private val _projects = MutableLiveData<List<ProjectsItem>>()
    val projects: LiveData<List<ProjectsItem>> = _projects

    val activePositions: LiveData<List<Int>> = Transformations.map(projects) { items ->
        items.filter { it.isActive }
                .map { items.indexOf(it) }
    }

    fun projects(projects: List<ProjectsItem>) {
        _projects.postValue(projects)
    }
}
