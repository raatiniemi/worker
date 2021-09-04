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

package me.raatiniemi.worker.feature.projects.createproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.domain.project.model.projectName
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.domain.project.usecase.FindProject
import me.raatiniemi.worker.domain.project.usecase.InvalidProjectNameException
import me.raatiniemi.worker.domain.project.usecase.ProjectAlreadyExistsException
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectError
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.Error
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import timber.log.Timber

internal class CreateProjectViewModel(
    private val usageAnalytics: UsageAnalytics,
    private val createProject: CreateProject,
    private val findProject: FindProject
) : ViewModel() {
    private val _name = MutableLiveData<String>()
    var name: LiveData<String> = _name

    private val _error = MutableLiveData<Error?>()
    val error: LiveData<Error?> = _error

    val viewActions = ConsumableLiveData<CreateProjectViewActions>()

    suspend fun onNameChange(name: String) {
        _error += null
        _name += name

        try {
            val project = findProject(projectName(name))
            if (project != null) {
                _error += CreateProjectError.ProjectAlreadyExists
            }
        } catch (e: InvalidProjectNameException) {
            _error += CreateProjectError.InvalidName
        }
    }

    suspend fun createProject(name: String) {
        try {
            val project = createProject(projectName(name))

            usageAnalytics.log(Event.ProjectCreate)
            viewActions += CreateProjectViewActions.Created(project)
        } catch (e: ProjectAlreadyExistsException) {
            Timber.d("Project with name \"$name\" already exists")
            _error += CreateProjectError.ProjectAlreadyExists
        } catch (e: InvalidProjectNameException) {
            Timber.w("Project name \"$name\" is not valid")
            _error += CreateProjectError.InvalidName
        } catch (e: Exception) {
            Timber.w(e, "Unable to create project")
            _error += CreateProjectError.Unknown
        }
    }

    fun dismiss() {
        viewActions += CreateProjectViewActions.Dismiss
    }
}
