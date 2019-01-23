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

package me.raatiniemi.worker.features.project.timereport.viewmodel

import me.raatiniemi.worker.domain.interactor.GetTimeReport
import me.raatiniemi.worker.features.project.timereport.model.TimeReportGroup
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.PublishSubject

class GetTimeReportViewModel internal constructor(private val useCase: GetTimeReport) {
    // TODO: Remove mutable state from ViewModel.
    private var shouldHideRegisteredTime = false
    private val fetch = PublishSubject.create<Request>()

    private val success = PublishSubject.create<TimeReportGroup>()
    private val errors = PublishSubject.create<Throwable>()

    init {
        fetch.switchMap {
            executeUseCase(it)
                    .compose(redirectErrors(errors))
                    .compose(hideErrors())
        }.subscribe(success)
    }

    private fun executeUseCase(request: Request): Observable<TimeReportGroup> {
        return Observable.defer<TimeReportGroup> {
            try {
                val items = useCase.execute(request.id, request.offset, shouldHideRegisteredTime)
                        .entries
                        .map { TimeReportGroup.build(it.key, it.value) }

                Observable.from(items)
            } catch (e: Exception) {
                Observable.error(e)
            }
        }
    }

    fun hideRegisteredTime() {
        shouldHideRegisteredTime = true
    }

    fun showRegisteredTime() {
        shouldHideRegisteredTime = false
    }

    fun fetch(id: Long, offset: Int) {
        fetch.onNext(Request(id, offset))
    }

    fun success(): Observable<TimeReportGroup> {
        return success
    }

    fun errors(): Observable<Throwable> {
        return errors
    }

    private data class Request(val id: Long, val offset: Int)
}
