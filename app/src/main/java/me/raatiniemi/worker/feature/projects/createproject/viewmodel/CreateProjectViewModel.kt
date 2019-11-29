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

package me.raatiniemi.worker.feature.projects.createproject.viewmodel

import androidx.lifecycle.*
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.project.model.isValid
import me.raatiniemi.worker.domain.project.model.projectName
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.domain.project.usecase.FindProject
import me.raatiniemi.worker.domain.project.usecase.InvalidProjectNameException
import me.raatiniemi.worker.domain.project.usecase.ProjectAlreadyExistsException
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.combineLatest
import me.raatiniemi.worker.feature.shared.model.debounce
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.TracePerformanceEvents
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import timber.log.Timber

internal class CreateProjectViewModel(
    private val usageAnalytics: UsageAnalytics,
    private val createProject: CreateProject,
    private val findProject: FindProject,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    val name = MutableLiveData<String>()

    private val isNameValid = name.map { isValid(it) }

    private val isNameAvailable = viewModelScope.debounce(name)
        .switchMap { name ->
            liveData(viewModelScope.coroutineContext + dispatcher) {
                emit(checkForAvailability(name))
            }
        }

    val isCreateEnabled: LiveData<Boolean> = combineLatest(isNameValid, isNameAvailable)
        .map { it.first && it.second }

    val viewActions = ConsumableLiveData<CreateProjectViewActions>()

    private suspend fun checkForAvailability(value: String): Boolean {
        return try {
            findProject(projectName(value)) ?: return true

            viewActions += CreateProjectViewActions.DuplicateNameErrorMessage
            false
        } catch (e: InvalidProjectNameException) {
            false
        }
    }

    @AddTrace(name = TracePerformanceEvents.CREATE_PROJECT)
    suspend fun createProject() = withContext(Dispatchers.IO) {
        val viewAction: CreateProjectViewActions = try {
            createProject(projectName(name.value))

            usageAnalytics.log(Event.ProjectCreate)
            CreateProjectViewActions.CreatedProject
        } catch (e: Exception) {
            when (e) {
                is InvalidProjectNameException -> CreateProjectViewActions.InvalidProjectNameErrorMessage
                is ProjectAlreadyExistsException -> CreateProjectViewActions.DuplicateNameErrorMessage
                else -> {
                    Timber.w(e, "Unable to create project")
                    CreateProjectViewActions.UnknownErrorMessage
                }
            }
        }
        viewActions += viewAction
    }
}
