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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.validator.ProjectName
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEditTextActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData

class CreateProjectViewModel(private val createProject: CreateProject) : ViewModel() {
    private val _projectName = MutableLiveData<String>().apply {
        value = ""
    }

    var projectName: String
        get() {
            return _projectName.value ?: ""
        }
        set(value) {
            _projectName.value = value
        }

    private val isProjectNameValid = Transformations.map(_projectName) {
        ProjectName.isValid(it)
    }

    val isCreateEnabled: LiveData<Boolean> = isProjectNameValid

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> = _project

    val viewActions = ConsumableLiveData<CreateProjectEditTextActions>()

    suspend fun createProject() {
        withContext(Dispatchers.IO) {
            try {
                val project = executeUseCase()

                _project.postValue(project)
            } catch (e: Exception) {
                handle(exception = e)
            }
        }
    }

    private fun executeUseCase(): Project {
        return createProject(projectName)
    }

    private fun handle(exception: Exception) {
        val action = when (exception) {
            is InvalidProjectNameException -> CreateProjectEditTextActions.InvalidProjectNameErrorMessage
            is ProjectAlreadyExistsException -> CreateProjectEditTextActions.DuplicateNameErrorMessage
            else -> CreateProjectEditTextActions.UnknownErrorMessage
        }

        viewActions.postValue(action)
    }
}
