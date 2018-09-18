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

package me.raatiniemi.worker.features.project.viewmodel

import me.raatiniemi.worker.domain.interactor.GetTimesheet
import me.raatiniemi.worker.features.project.model.TimesheetGroup
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.PublishSubject

interface GetTimesheetViewModel {
    interface Input {
        fun hideRegisteredTime()
        fun showRegisteredTime()

        // TODO: Improve method name.
        // The method do not fetch anything, it just pushes the "event"
        // to onto an observable which will handle the actual fetching.
        fun fetch(id: Long, offset: Int)
    }

    interface Output {
        fun success(): Observable<TimesheetGroup>
    }

    interface Error {
        fun errors(): Observable<Throwable>
    }

    class ViewModel internal constructor(private val useCase: GetTimesheet) : Input, Output, Error {
        // TODO: Remove mutable state from ViewModel.
        private var shouldHideRegisteredTime = false
        private val fetch = PublishSubject.create<Request>()

        private val success = PublishSubject.create<TimesheetGroup>()
        private val errors = PublishSubject.create<Throwable>()

        init {
            fetch.switchMap {
                executeUseCase(it)
                        .compose(redirectErrors(errors))
                        .compose(hideErrors())
            }.subscribe(success)
        }

        private fun executeUseCase(request: Request): Observable<TimesheetGroup> {
            return Observable.defer<TimesheetGroup> {
                try {
                    val items = useCase.execute(request.id, request.offset, shouldHideRegisteredTime)
                            .entries
                            .map { TimesheetGroup.build(it.key, it.value) }

                    Observable.from(items)
                } catch (e: Exception) {
                    Observable.error(e)
                }
            }
        }

        override fun hideRegisteredTime() {
            shouldHideRegisteredTime = true
        }

        override fun showRegisteredTime() {
            shouldHideRegisteredTime = false
        }

        override fun fetch(id: Long, offset: Int) {
            fetch.onNext(Request(id, offset))
        }

        override fun success(): Observable<TimesheetGroup> {
            return success
        }

        override fun errors(): Observable<Throwable> {
            return errors
        }

        private data class Request(val id: Long, val offset: Int)
    }
}
