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

import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.ClockOut
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class ClockActivityViewModel(
        private val keyValueStore: KeyValueStore,
        private val clockIn: ClockIn,
        private val clockOut: ClockOut,
        private val getProjectTimeSince: GetProjectTimeSince
) : ViewModel() {
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

    private val clockInResult = PublishSubject.create<ProjectsItemAdapterResult>()
    private val clockInDate = PublishSubject.create<Date>()

    val clockInSuccess: PublishSubject<ProjectsItemAdapterResult> = PublishSubject.create<ProjectsItemAdapterResult>()
    val clockInError: PublishSubject<Throwable> = PublishSubject.create<Throwable>()

    private val clockOutResult = PublishSubject.create<ProjectsItemAdapterResult>()
    private val clockOutDate = PublishSubject.create<Date>()

    val clockOutSuccess: PublishSubject<ProjectsItemAdapterResult> = PublishSubject.create<ProjectsItemAdapterResult>()
    val clockOutError: PublishSubject<Throwable> = PublishSubject.create<Throwable>()

    init {
        Observable.zip(clockInResult, clockInDate) { result, date -> CombinedResult(result, date) }
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .switchMap { result ->
                    executeUseCase(Action.CLOCK_IN, result)
                            .compose(redirectErrors(clockInError))
                            .compose(hideErrors())
                }
                .subscribe(clockInSuccess)

        Observable.zip(clockOutResult, clockOutDate) { result, date -> CombinedResult(result, date) }
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .switchMap { result ->
                    executeUseCase(Action.CLOCK_OUT, result)
                            .compose(redirectErrors(clockOutError))
                            .compose(hideErrors())
                }
                .subscribe(clockOutSuccess)
    }

    private fun executeUseCase(action: Action, combinedResult: CombinedResult): Observable<ProjectsItemAdapterResult> {
        return try {
            val project = combinedResult.result.projectsItem.asProject()
            val date = combinedResult.date

            val projectId = project.id
                    ?: throw NullPointerException("No project id is available")

            when (action) {
                Action.CLOCK_IN -> clockIn.execute(projectId, date)
                Action.CLOCK_OUT -> clockOut.execute(projectId, date)
            }
            val registeredTime = getRegisteredTimeForProject(project)

            val projectsItem = ProjectsItem.from(project, registeredTime)
            Observable.just(buildResult(combinedResult.result, projectsItem))
        } catch (e: Exception) {
            Observable.error(e)
        }
    }

    private fun getRegisteredTimeForProject(project: Project): List<TimeInterval> {
        return getProjectTimeSince(project, startingPoint)
    }

    private fun buildResult(
            result: ProjectsItemAdapterResult,
            projectsItem: ProjectsItem
    ): ProjectsItemAdapterResult {
        return ProjectsItemAdapterResult(result.position, projectsItem)
    }

    fun clockIn(result: ProjectsItemAdapterResult, date: Date) {
        this.clockInResult.onNext(result)
        this.clockInDate.onNext(date)
    }

    fun clockOut(result: ProjectsItemAdapterResult, date: Date) {
        this.clockOutResult.onNext(result)
        this.clockOutDate.onNext(date)
    }

    class CombinedResult constructor(val result: ProjectsItemAdapterResult, val date: Date)

    enum class Action {
        CLOCK_IN,
        CLOCK_OUT
    }
}
