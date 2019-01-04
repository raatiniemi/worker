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
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.interactor.GetProjects
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.features.shared.viewmodel.CoroutineScopedViewModel
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import timber.log.Timber

internal class ProjectsViewModel(
        private val keyValueStore: KeyValueStore,
        private val getProjects: GetProjects,
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

    private val _projects = MutableLiveData<List<ProjectsItem>>()
    val projects: LiveData<List<ProjectsItem>> = _projects

    val viewActions = ConsumableLiveData<ProjectsViewActions>()

    suspend fun loadProjects() = withContext(Dispatchers.IO) {
        try {
            val projects = getProjects()
                    .map { project ->
                        val registeredTime = loadRegisteredTimeForProject(project)

                        ProjectsItem.from(project, registeredTime)
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
}
