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
import me.raatiniemi.worker.util.RxUtil.hideErrors
import rx.Observable
import rx.Observable.defer
import timber.log.Timber

class ProjectsViewModel(
        private val getProjects: GetProjects,
        private val getProjectTimeSince: GetProjectTimeSince
) {
    private var startingPoint = TimeIntervalStartingPoint.MONTH
    private val projects: Observable<List<ProjectsItem>>

    val viewActions = ConsumableLiveData<ProjectsViewActions>()

    init {
        projects = executeGetProjects()
                .flatMap { Observable.from(it) }
                .map { populateItemWithRegisteredTime(it) }
                .compose(hideErrors())
                .toList()
    }

    private fun executeGetProjects(): Observable<List<Project>> = defer<List<Project>> {
        return@defer try {
            Observable.just(getProjects.execute())
        } catch (e: DomainException) {
            viewActions.postValue(ProjectsViewActions.ShowUnableToGetProjectsErrorMessage)
            Observable.error(e)
        }
    }

    private fun populateItemWithRegisteredTime(project: Project): ProjectsItem {
        val registeredTime = getRegisteredTime(project)

        return ProjectsItem.from(project, registeredTime)
    }

    private fun getRegisteredTime(project: Project): List<TimeInterval> {
        return try {
            getProjectTimeSince.execute(project, startingPoint)
        } catch (e: DomainException) {
            Timber.w(e, "Unable to get registered time for project")
            emptyList()
        }
    }

    fun startingPointForTimeSummary(startingPoint: Int) {
        try {
            this.startingPoint = TimeIntervalStartingPoint.from(startingPoint)
        } catch (e: InvalidStartingPointException) {
            Timber.w(e, "Invalid starting point supplied: %i", startingPoint)
        }
    }

    fun projects(): Observable<List<ProjectsItem>> = projects
}
