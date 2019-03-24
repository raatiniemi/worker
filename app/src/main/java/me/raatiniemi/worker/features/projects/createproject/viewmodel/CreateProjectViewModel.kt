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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.interactor.FindProject
import me.raatiniemi.worker.domain.validator.ProjectName
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.features.shared.model.*
import me.raatiniemi.worker.features.shared.viewmodel.CoroutineScopedViewModel

class CreateProjectViewModel(
        private val createProject: CreateProject,
        private val findProject: FindProject
) : CoroutineScopedViewModel() {
    private val _name = MutableLiveData<String>().apply {
        value = ""
    }

    private val isNameValid = _name.map { ProjectName.isValid(it) }

    private val isNameAvailable = _name.debounce(this)
            .map {
                if (it.isNullOrBlank()) {
                    return@map true
                }

                findProject(it) ?: return@map true

                viewActions.postValue(CreateProjectViewActions.DuplicateNameErrorMessage)
                false
            }

    var name: String
        get() {
            return _name.value ?: ""
        }
        set(value) {
            _name.value = value
        }

    val isCreateEnabled: LiveData<Boolean> = combineLatest(isNameValid, isNameAvailable)
            .map { it.first && it.second }

    val viewActions = ConsumableLiveData<CreateProjectViewActions>()

    suspend fun createProject() = withContext(Dispatchers.IO) {
        val viewAction: CreateProjectViewActions = try {
            val project = createProject(name)

            CreateProjectViewActions.CreatedProject(project)
        } catch (e: Exception) {
            when (e) {
                is InvalidProjectNameException -> CreateProjectViewActions.InvalidProjectNameErrorMessage
                is ProjectAlreadyExistsException -> CreateProjectViewActions.DuplicateNameErrorMessage
                else -> CreateProjectViewActions.UnknownErrorMessage
            }
        }
        viewActions += viewAction
    }
}
