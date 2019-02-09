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

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
import me.raatiniemi.worker.features.project.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.project.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.util.KeyValueStore

class TimeReportViewModel internal constructor(
        projectHolder: ProjectHolder,
        keyValueStore: KeyValueStore,
        repository: TimeReportRepository,
        private val markRegisteredTime: MarkRegisteredTime,
        private val removeTime: RemoveTime
) : ViewModel(), TimeReportSelectionManager {
    private val factory = TimeReportDataSourceFactory(
            projectHolder.project,
            keyValueStore,
            repository
    )

    val timeReport: LiveData<PagedList<TimeReportDay>>

    val viewActions = ConsumableLiveData<TimeReportViewActions>()

    init {
        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(1)
                .setPrefetchDistance(2)
                .setPageSize(8)
                .setMaxSize(15)
                .setEnablePlaceholders(true)
                .build()

        timeReport = LivePagedListBuilder(factory, config).build()
    }

    fun reloadTimeReport() {
        _selectedItems.postValue(HashSet())

        timeReport.value?.run {
            dataSource.invalidate()
        }
    }

    suspend fun registerSelectedItems() = withContext(Dispatchers.IO) {
        try {
            val selectedItems = _selectedItems.value ?: return@withContext
            val timeIntervals = selectedItems.map { it.asTimeInterval() }

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

    private val _selectedItems = MutableLiveData<HashSet<TimeReportItem>?>()
    val isSelectionActivated: LiveData<Boolean> = Transformations.map(_selectedItems) {
        isSelectionActivated(it)
    }

    private fun isSelectionActivated(items: Set<TimeReportItem>?): Boolean {
        return !items.isNullOrEmpty()
    }

    @MainThread
    override fun consume(longPress: TimeReportLongPressAction): Boolean {
        val selectedItems = _selectedItems.value ?: HashSet()
        if (isSelectionActivated(selectedItems)) {
            return false
        }

        if (selectedItems.containsAll(longPress.items)) {
            return false
        }

        _selectedItems.value = selectedItems.apply {
            addAll(longPress.items)
        }
        return true
    }

    @MainThread
    override fun consume(tap: TimeReportTapAction) {
        val selectedItems = _selectedItems.value ?: HashSet()
        if (!isSelectionActivated(selectedItems)) {
            return
        }

        if (selectedItems.containsAll(tap.items)) {
            selectedItems.removeAll(tap.items)
            _selectedItems.value = selectedItems
            return
        }

        selectedItems.addAll(tap.items)
        _selectedItems.value = selectedItems
    }
}
