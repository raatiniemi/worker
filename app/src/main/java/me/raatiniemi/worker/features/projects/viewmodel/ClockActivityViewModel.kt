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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.exception.NoProjectIdException
import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.ClockOut
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.features.shared.viewmodel.CoroutineScopedViewModel
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import timber.log.Timber
import java.util.*

class ClockActivityViewModel(
        private val keyValueStore: KeyValueStore,
        private val clockIn: ClockIn,
        private val clockOut: ClockOut,
        private val getProjectTimeSince: GetProjectTimeSince
) : CoroutineScopedViewModel() {
    private val startingPoint: TimeIntervalStartingPoint
        get() {
            val defaultValue = TimeIntervalStartingPoint.MONTH
            val startingPoint = keyValueStore.int(
                    AppKeys.TIME_SUMMARY.rawValue,
                    defaultValue.rawValue
            )

            return try {
                TimeIntervalStartingPoint.from(startingPoint)
            } catch (e: InvalidStartingPointException) {
                Timber.w(e, "Invalid starting point supplied: %i", startingPoint)
                defaultValue
            }
        }

    val viewActions = ConsumableLiveData<ProjectsViewActions>()

    private fun executeUseCase(action: Action) {
        try {
            val project = action.project
            val date = action.date

            val projectId = project.id ?: throw NoProjectIdException()

            when (action) {
                is Action.ClockIn -> clockIn.execute(projectId, date)
                is Action.ClockOut -> clockOut.execute(projectId, date)
            }
            val registeredTime = getProjectTimeSince(project, startingPoint)
            val projectsItem = ProjectsItem.from(project, registeredTime)

            val result = ProjectsItemAdapterResult(action.position, projectsItem)
            viewActions.postValue(ProjectsViewActions.UpdateProject(result))
        } catch (e: Exception) {
            val viewAction: ProjectsViewActions = when (action) {
                is Action.ClockIn -> ProjectsViewActions.ShowUnableToClockInErrorMessage
                is Action.ClockOut -> ProjectsViewActions.ShowUnableToClockOutErrorMessage
            }
            viewActions.postValue(viewAction)
        }
    }

    suspend fun clockIn(result: ProjectsItemAdapterResult, date: Date) = withContext(Dispatchers.IO) {
        executeUseCase(Action.ClockIn(result, date))
    }

    suspend fun clockOut(result: ProjectsItemAdapterResult, date: Date) = withContext(Dispatchers.IO) {
        executeUseCase(Action.ClockOut(result, date))
    }

    sealed class Action(result: ProjectsItemAdapterResult, val date: Date) {
        val position: Int = result.position
        val project: Project = result.projectsItem.asProject()

        class ClockIn(result: ProjectsItemAdapterResult, date: Date) : Action(result, date)

        class ClockOut(result: ProjectsItemAdapterResult, date: Date) : Action(result, date)
    }
}
