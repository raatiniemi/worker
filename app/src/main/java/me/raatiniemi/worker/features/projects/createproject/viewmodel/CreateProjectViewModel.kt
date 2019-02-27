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

package me.raatiniemi.worker.features.projects.createproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.interactor.FindProject
import me.raatiniemi.worker.domain.validator.ProjectName
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.features.shared.model.debounce
import me.raatiniemi.worker.features.shared.model.map
import me.raatiniemi.worker.features.shared.viewmodel.CoroutineScopedViewModel

class CreateProjectViewModel(
        private val createProject: CreateProject,
        private val findProject: FindProject
) : CoroutineScopedViewModel() {
    private val _projectName = MutableLiveData<String>().apply {
        value = ""
    }

    var projectName: String
        get() {
            return _projectName.value ?: ""
        }
        set(value) {
            _projectName.value = value
            _viewActions.value = null
        }

    private val isProjectNameValid = _projectName.map { ProjectName.isValid(it) }

    private val isProjectNameAvailable: LiveData<CreateProjectViewActions?> =
            Transformations.map(_projectName.debounce(context = this)) {
                if (it.isNullOrBlank()) {
                    return@map null
                }

                findProject(it) ?: return@map null

                CreateProjectViewActions.DuplicateNameErrorMessage
            }

    private val _viewActions = MutableLiveData<CreateProjectViewActions?>()
    val viewActions: LiveData<CreateProjectViewActions?> =
            MediatorLiveData<CreateProjectViewActions?>().apply {
                addSource(_viewActions) {
                    value = it
                }

                addSource(isProjectNameAvailable) {
                    value = it
                }
            }

    val isCreateEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(isProjectNameValid) {
            value = it && viewActions.value == null
        }

        addSource(viewActions) {
            value = it == null && isProjectNameValid.value ?: false
        }
    }

    suspend fun createProject() = withContext(Dispatchers.IO) {
        try {
            val project = createProject(projectName)

            val viewAction = CreateProjectViewActions.CreatedProject(project)
            _viewActions.postValue(viewAction)
        } catch (e: Exception) {
            val viewAction: CreateProjectViewActions = when (e) {
                is InvalidProjectNameException -> CreateProjectViewActions.InvalidProjectNameErrorMessage
                is ProjectAlreadyExistsException -> CreateProjectViewActions.DuplicateNameErrorMessage
                else -> CreateProjectViewActions.UnknownErrorMessage
            }

            _viewActions.postValue(viewAction)
        }
    }
}
