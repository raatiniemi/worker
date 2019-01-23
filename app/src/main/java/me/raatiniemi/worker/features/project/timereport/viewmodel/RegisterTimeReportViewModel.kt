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

import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.PublishSubject

class RegisterTimeReportViewModel internal constructor(private val useCase: MarkRegisteredTime) {
    private val register = PublishSubject.create<List<TimeReportAdapterResult>>()

    private val success = PublishSubject.create<TimeReportAdapterResult>()
    private val errors = PublishSubject.create<Throwable>()

    init {
        register.switchMap {
            executeUseCase(it)
                    .compose(redirectErrors(errors))
                    .compose(hideErrors())
        }.subscribe(success)
    }

    private fun executeUseCase(results: List<TimeReportAdapterResult>): Observable<TimeReportAdapterResult> {
        return Observable.defer<TimeReportAdapterResult> {
            try {
                val times = results.map { it.timeInterval }.toList()
                val items = useCase.execute(times)
                        .map {
                            mapUpdateToSelectedItems(it, results)
                        }
                        .toList()

                Observable.from(items.sorted().reversed())
            } catch (e: Exception) {
                Observable.error(e)
            }
        }
    }

    private fun mapUpdateToSelectedItems(time: TimeInterval, selectedItems: List<TimeReportAdapterResult>): TimeReportAdapterResult {
        return selectedItems
                .filter { it.timeInterval.id == time.id }
                .map {
                    val item = TimeReportItem.with(time)

                    TimeReportAdapterResult(it.group, it.child, item)
                }
                .first()
    }

    fun register(results: List<TimeReportAdapterResult>) {
        register.onNext(results)
    }

    fun success(): Observable<TimeReportAdapterResult> {
        return success
    }

    fun errors(): Observable<Throwable> {
        return errors
    }
}
