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

package me.raatiniemi.worker.features.project.timesheet.viewmodel

import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.features.project.timesheet.model.TimesheetAdapterResult
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.PublishSubject

interface RemoveTimesheetViewModel {
    interface Input {
        fun remove(results: List<TimesheetAdapterResult>)
    }

    interface Output {
        fun success(): Observable<TimesheetAdapterResult>
    }

    interface Error {
        fun errors(): Observable<Throwable>
    }

    class ViewModel internal constructor(private val useCase: RemoveTime) : Input, Output, Error {
        private val remove = PublishSubject.create<List<TimesheetAdapterResult>>()

        private val success = PublishSubject.create<TimesheetAdapterResult>()
        private val errors = PublishSubject.create<Throwable>()

        init {
            remove.switchMap {
                executeUseCase(it)
                        .compose(redirectErrors(errors))
                        .compose(hideErrors())
            }.subscribe(success)
        }

        private fun executeUseCase(results: List<TimesheetAdapterResult>): Observable<TimesheetAdapterResult> {
            return Observable.defer<TimesheetAdapterResult> {
                try {
                    val times = results.map { it.timeInterval }.toList()
                    useCase.execute(times)

                    Observable.from(results.sorted().reversed())
                } catch (e: Exception) {
                    Observable.error(e)
                }
            }
        }

        override fun remove(results: List<TimesheetAdapterResult>) {
            remove.onNext(results)
        }

        override fun success(): Observable<TimesheetAdapterResult> {
            return success
        }

        override fun errors(): Observable<Throwable> {
            return errors
        }
    }
}