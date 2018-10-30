/*
 * Copyright (C) 2017 Worker Project
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

import me.raatiniemi.worker.domain.interactor.ClockActivityChange
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

interface ClockActivityViewModel {
    interface Input {
        fun startingPointForTimeSummary(startingPoint: Int)

        fun clockIn(result: ProjectsItemAdapterResult, date: Date)

        fun clockOut(result: ProjectsItemAdapterResult, date: Date)
    }

    interface Output {
        fun clockInSuccess(): Observable<ProjectsItemAdapterResult>

        fun clockOutSuccess(): Observable<ProjectsItemAdapterResult>
    }

    interface Error {
        fun clockInError(): Observable<Throwable>

        fun clockOutError(): Observable<Throwable>
    }

    class ViewModel(
            private val clockActivityChange: ClockActivityChange,
            private val getProjectTimeSince: GetProjectTimeSince
    ) : Input, Output, Error {
        private val input: Input
        private val output: Output
        private val error: Error

        private var startingPoint = GetProjectTimeSince.MONTH

        private val clockInResult = PublishSubject.create<ProjectsItemAdapterResult>()
        private val clockInDate = PublishSubject.create<Date>()

        private val clockInSuccess = PublishSubject.create<ProjectsItemAdapterResult>()
        private val clockInError = PublishSubject.create<Throwable>()

        private val clockOutResult = PublishSubject.create<ProjectsItemAdapterResult>()
        private val clockOutDate = PublishSubject.create<Date>()

        private val clockOutSuccess = PublishSubject.create<ProjectsItemAdapterResult>()
        private val clockOutError = PublishSubject.create<Throwable>()

        init {
            input = this
            output = this
            error = this

            Observable.zip(clockInResult, clockInDate) { result, date -> CombinedResult(result, date) }
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .switchMap { result ->
                        executeUseCase(result)
                                .compose(redirectErrors(clockInError))
                                .compose(hideErrors())
                    }
                    .subscribe(clockInSuccess)

            Observable.zip(clockOutResult, clockOutDate) { result, date -> CombinedResult(result, date) }
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .switchMap { result ->
                        executeUseCase(result)
                                .compose(redirectErrors(clockOutError))
                                .compose(hideErrors())
                    }
                    .subscribe(clockOutSuccess)
        }

        private fun executeUseCase(combinedResult: CombinedResult): Observable<ProjectsItemAdapterResult> {
            return try {
                val project = executeClockActivityChange(combinedResult)
                val registeredTime = getRegisteredTimeForProject(project)

                val projectsItem = ProjectsItem.from(project, registeredTime)
                Observable.just(buildResult(combinedResult.result, projectsItem))
            } catch (e: Exception) {
                Observable.error(e)
            }
        }

        private fun executeClockActivityChange(combinedResult: CombinedResult): Project {
            val projectsItem = combinedResult.result.projectsItem

            return clockActivityChange.execute(projectsItem.asProject(), combinedResult.date)
        }

        private fun getRegisteredTimeForProject(project: Project): List<TimeInterval> {
            return getProjectTimeSince.execute(project, startingPoint)
        }

        private fun buildResult(
                result: ProjectsItemAdapterResult,
                projectsItem: ProjectsItem
        ): ProjectsItemAdapterResult {
            return ProjectsItemAdapterResult.build(result.position, projectsItem)
        }

        override fun clockIn(result: ProjectsItemAdapterResult, date: Date) {
            this.clockInResult.onNext(result)
            this.clockInDate.onNext(date)
        }

        override fun clockOut(result: ProjectsItemAdapterResult, date: Date) {
            this.clockOutResult.onNext(result)
            this.clockOutDate.onNext(date)
        }

        override fun clockInSuccess(): Observable<ProjectsItemAdapterResult> {
            return clockInSuccess
        }

        override fun clockOutSuccess(): Observable<ProjectsItemAdapterResult> {
            return clockOutSuccess
        }

        override fun startingPointForTimeSummary(startingPoint: Int) {
            when (startingPoint) {
                GetProjectTimeSince.MONTH, GetProjectTimeSince.WEEK, GetProjectTimeSince.DAY -> this.startingPoint = startingPoint
                else -> Timber.d("Invalid starting point supplied: %i", startingPoint)
            }
        }

        override fun clockInError(): Observable<Throwable> {
            return clockInError
        }

        override fun clockOutError(): Observable<Throwable> {
            return clockOutError
        }

        fun input(): Input {
            return input
        }

        fun output(): Output {
            return output
        }

        fun error(): Error {
            return error
        }

        class CombinedResult constructor(val result: ProjectsItemAdapterResult, val date: Date)
    }
}
