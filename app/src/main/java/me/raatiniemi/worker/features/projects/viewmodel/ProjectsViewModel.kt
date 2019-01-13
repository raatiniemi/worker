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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.exception.NoProjectIdException
import me.raatiniemi.worker.domain.interactor.*
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
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

internal class ProjectsViewModel(
        private val keyValueStore: KeyValueStore,
        private val getProjects: GetProjects,
        private val getProjectTimeSince: GetProjectTimeSince,
        private val clockIn: ClockIn,
        private val clockOut: ClockOut,
        private val removeProject: RemoveProject
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

    private val _projects = MutableLiveData<List<ProjectsItem>>()
    val projects: LiveData<List<ProjectsItem>> = _projects

    val viewActions = ConsumableLiveData<ProjectsViewActions>()

    suspend fun loadProjects() = withContext(Dispatchers.IO) {
        try {
            val projects = getProjects()
                    .map { project ->
                        val registeredTime = loadRegisteredTimeForProject(project)

                        ProjectsItem(project, registeredTime)
                    }

            _projects.postValue(projects)
        } catch (e: DomainException) {
            viewActions.postValue(ProjectsViewActions.ShowUnableToGetProjectsErrorMessage)
        }
    }

    private fun loadRegisteredTimeForProject(project: Project): List<TimeInterval> {
        return try {
            getProjectTimeSince(project, startingPoint)
        } catch (e: DomainException) {
            Timber.w(e, "Unable to get registered time for project")
            emptyList()
        }
    }

    suspend fun refreshActiveProjects(projects: List<ProjectsItem>) = withContext(Dispatchers.Default) {
        val positions = projects.filter { it.isActive }
                .map { projects.indexOf(it) }

        if (positions.isEmpty()) {
            return@withContext
        }

        val viewAction = ProjectsViewActions.RefreshProjects(positions)
        viewActions.postValue(viewAction)
    }

    suspend fun clockIn(result: ProjectsItemAdapterResult, date: Date) = withContext(Dispatchers.IO) {
        try {
            val project = result.project
            val projectId = project.id ?: throw NoProjectIdException()

            clockIn.execute(projectId, date)

            val viewAction = ProjectsViewActions.UpdateProject(rebuildResult(result))
            viewActions.postValue(viewAction)
        } catch (e: Exception) {
            viewActions.postValue(ProjectsViewActions.ShowUnableToClockInErrorMessage)
        }
    }

    suspend fun clockOut(result: ProjectsItemAdapterResult, date: Date) = withContext(Dispatchers.IO) {
        try {
            val project = result.project
            val projectId = project.id ?: throw NoProjectIdException()

            clockOut.execute(projectId, date)

            val viewAction = ProjectsViewActions.UpdateProject(rebuildResult(result))
            viewActions.postValue(viewAction)
        } catch (e: Exception) {
            viewActions.postValue(ProjectsViewActions.ShowUnableToClockOutErrorMessage)
        }
    }

    private fun rebuildResult(result: ProjectsItemAdapterResult): ProjectsItemAdapterResult {
        val project = result.project
        val registeredTime = getProjectTimeSince(project, startingPoint)

        return result.copy(projectsItem = ProjectsItem(project, registeredTime))
    }

    suspend fun remove(result: ProjectsItemAdapterResult) = withContext(Dispatchers.IO) {
        try {
            val (_, projectsItem) = result

            removeProject(projectsItem.asProject())
        } catch (e: NoProjectIdException) {
            Timber.w(e, "Unable to remove project without id")
        } catch (e: Exception) {
            val viewAction = ProjectsViewActions.RestoreProject(result)
            viewActions.postValue(viewAction)
        }
    }
}
