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
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData

class RemoveTimeReportViewModel internal constructor(private val removeTime: RemoveTime) {
    val viewActions = ConsumableLiveData<TimeReportViewActions>()

    suspend fun remove(results: List<TimeReportAdapterResult>) = withContext(Dispatchers.IO) {
        try {
            val timeInterval = results.map { it.timeInterval }.toList()
            removeTime(timeInterval)

            viewActions.postValue(TimeReportViewActions.RemoveRegistered(results.sorted().reversed()))
        } catch (e: Exception) {
            viewActions.postValue(TimeReportViewActions.ShowUnableToDeleteErrorMessage)
        }
    }
}
