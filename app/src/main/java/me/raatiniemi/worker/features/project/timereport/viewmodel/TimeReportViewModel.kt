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

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.projects.datasource.TimeReportDataSourceFactory
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.project.model.ProjectHolder
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.util.KeyValueStore

class TimeReportViewModel internal constructor(
        projectHolder: ProjectHolder,
        keyValueStore: KeyValueStore,
        repository: TimeReportRepository,
        private val markRegisteredTime: MarkRegisteredTime,
        private val removeTime: RemoveTime
) : ViewModel() {
    private val factory = TimeReportDataSourceFactory(
            projectHolder.project,
            keyValueStore,
            repository
    )

    val timeReport: LiveData<PagedList<TimeReportDay>>

    val viewActions = ConsumableLiveData<TimeReportViewActions>()

    init {
        val config = PagedList.Config.Builder()
                .setPageSize(15)
                .setEnablePlaceholders(true)
                .build()

        timeReport = LivePagedListBuilder(factory, config).build()
    }

    fun reloadTimeReport() {
        timeReport.value?.run {
            dataSource.invalidate()
        }
    }

    suspend fun register(timeReportItems: List<TimeReportItem>) = withContext(Dispatchers.IO) {
        try {
            val timeIntervals = timeReportItems.map {
                it.asTimeInterval()
            }
            markRegisteredTime(timeIntervals)

            reloadTimeReport()
        } catch (e: Exception) {
            viewActions.postValue(TimeReportViewActions.ShowUnableToRegisterErrorMessage)
        }
    }

    suspend fun remove(timeReportItems: List<TimeReportItem>) = withContext(Dispatchers.IO) {
        try {
            val timeIntervals = timeReportItems.map {
                it.asTimeInterval()
            }
            removeTime(timeIntervals)

            reloadTimeReport()
        } catch (e: Exception) {
            viewActions.postValue(TimeReportViewActions.ShowUnableToDeleteErrorMessage)
        }
    }
}
