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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.validator.ProjectName
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEditTextActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData

interface CreateProjectViewModel {
    interface Input {
        val projectName: MutableLiveData<String>

        suspend fun createProject()
    }

    interface Output {
        val isCreateEnabled: LiveData<Boolean>

        val project: LiveData<Project>
    }

    interface Error {
        val viewActions: ConsumableLiveData<CreateProjectEditTextActions>
    }

    class ViewModel(private val useCase: CreateProject) : Input, Output, Error, androidx.lifecycle.ViewModel() {
        val input: Input = this
        val output: Output = this
        val error: Error = this

        override val projectName = MutableLiveData<String>().apply {
            value = ""
        }

        override val isCreateEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
            addSource(projectName) {
                value = ProjectName.isValid(it)
            }
        }

        private val _project = MutableLiveData<Project>()
        override val project: LiveData<Project> = _project

        override val viewActions = ConsumableLiveData<CreateProjectEditTextActions>()

        override suspend fun createProject() {
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
            val project = Project.from(projectName.value ?: "")

            return useCase.execute(project)
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
}
