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
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.Observable.defer
import rx.subjects.PublishSubject
import timber.log.Timber

interface ProjectsViewModel {
    interface Input {
        fun startingPointForTimeSummary(startingPoint: Int)
    }

    interface Output {
        fun projects(): Observable<List<ProjectsItem>>
    }

    interface Error {
        fun projectsError(): Observable<Throwable>
    }

    class ViewModel(
            private val getProjects: GetProjects,
            private val getProjectTimeSince: GetProjectTimeSince
    ) : Input, Output, Error {
        private val input: Input
        private val output: Output
        private val error: Error

        private var startingPoint = TimeIntervalStartingPoint.MONTH
        private val projects: Observable<List<ProjectsItem>>
        private val projectsError = PublishSubject.create<Throwable>()

        init {
            input = this
            output = this
            error = this

            projects = executeGetProjects()
                    .flatMap { Observable.from(it) }
                    .map { populateItemWithRegisteredTime(it) }
                    .compose(redirectErrors(projectsError))
                    .compose(hideErrors())
                    .toList()
        }

        private fun executeGetProjects(): Observable<List<Project>> = defer<List<Project>> {
            return@defer try {
                Observable.just(getProjects.execute())
            } catch (e: DomainException) {
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

        override fun startingPointForTimeSummary(startingPoint: Int) {
            try {
                this.startingPoint = TimeIntervalStartingPoint.from(startingPoint)
            } catch (e: InvalidStartingPointException) {
                Timber.w(e, "Invalid starting point supplied: %i", startingPoint)
            }
        }

        override fun projects(): Observable<List<ProjectsItem>> = projects

        override fun projectsError(): Observable<Throwable> = projectsError

        fun input(): Input = input

        fun output(): Output = output

        fun error(): Error = error
    }
}
