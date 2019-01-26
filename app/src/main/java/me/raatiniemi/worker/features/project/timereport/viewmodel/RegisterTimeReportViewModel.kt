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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData

class RegisterTimeReportViewModel internal constructor(private val useCase: MarkRegisteredTime) {
    val viewActions = ConsumableLiveData<TimeReportViewActions>()

    suspend fun register(results: List<TimeReportAdapterResult>) = withContext(Dispatchers.IO) {
        try {
            val timeIntervals = results.map { it.timeInterval }
            val items = useCase.execute(timeIntervals)
                    .map { mapUpdateToSelectedItems(it, results) }
                    .sorted()
                    .reversed()

            viewActions.postValue(TimeReportViewActions.UpdateRegistered(items))
        } catch (e: Exception) {
            viewActions.postValue(TimeReportViewActions.ShowUnableToRegisterErrorMessage)
        }
    }

    private fun mapUpdateToSelectedItems(
            timeInterval: TimeInterval,
            selectedItems: List<TimeReportAdapterResult>
    ): TimeReportAdapterResult {
        return selectedItems.filter { it.timeInterval.id == timeInterval.id }
                .map {
                    val item = TimeReportItem.with(timeInterval)

                    TimeReportAdapterResult(it.group, it.child, item)
                }
                .first()
    }
}
