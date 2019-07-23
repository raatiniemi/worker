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

package me.raatiniemi.worker.features.projects.timereport.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.projects.datasource.TimeReportDataSourceFactory
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.interactor.UnableToMarkActiveTimeIntervalAsRegisteredException
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.isActive
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.projects.model.ProjectProvider
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportState
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.features.shared.model.plusAssign
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import timber.log.Timber

internal class TimeReportViewModel internal constructor(
    private val usageAnalytics: UsageAnalytics,
    projectProvider: ProjectProvider,
    private val keyValueStore: KeyValueStore,
    repository: TimeReportRepository,
    private val markRegisteredTime: MarkRegisteredTime,
    private val removeTime: RemoveTime
) : ViewModel(), TimeReportStateManager {
    private val factory = TimeReportDataSourceFactory(
        projectProvider,
        keyValueStore,
        repository
    )

    private val _selectedItems = MutableLiveData<HashSet<TimeInterval>?>()
    private val expandedDays = mutableSetOf<Int>()

    var shouldHideRegisteredTime: Boolean
        get() = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)
        set(value) {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, value)
            reloadTimeReport()
        }

    val isSelectionActivated: LiveData<Boolean> = _selectedItems.map { isSelectionActivated(it) }

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
        clearSelection()

        timeReport.value?.run {
            dataSource.invalidate()
        }
    }

    fun clearSelection() {
        _selectedItems.postValue(HashSet())
    }

    suspend fun toggleRegisteredStateForSelectedItems() = withContext(Dispatchers.IO) {
        try {
            val selectedItems = _selectedItems.value ?: return@withContext
            val timeIntervals = selectedItems.toList()

            markRegisteredTime(timeIntervals)

            usageAnalytics.log(Event.TimeReportToggle(timeIntervals.size))
            reloadTimeReport()
        } catch (e: UnableToMarkActiveTimeIntervalAsRegisteredException) {
            Timber.i(e, "Unable to toggle registered state with active items")
            viewActions += TimeReportViewActions.ShowUnableToMarkActiveTimeIntervalsAsRegisteredErrorMessage
        } catch (e: Exception) {
            Timber.e(e, "Unable to toggle registered state with selected items")
            viewActions.postValue(TimeReportViewActions.ShowUnableToRegisterErrorMessage)
        }
    }

    suspend fun removeSelectedItems() = withContext(Dispatchers.IO) {
        try {
            val selectedItems = _selectedItems.value ?: return@withContext
            val timeIntervals = selectedItems.toList()

            removeTime(timeIntervals)

            usageAnalytics.log(Event.TimeReportRemove(timeIntervals.size))
            reloadTimeReport()
        } catch (e: Exception) {
            Timber.e(e, "Unable to remove selected items")
            viewActions.postValue(TimeReportViewActions.ShowUnableToDeleteErrorMessage)
        }
    }

    @MainThread
    override fun expanded(position: Int): Boolean = expandedDays.contains(position)

    @MainThread
    override fun expand(position: Int) {
        expandedDays.add(position)
    }

    @MainThread
    override fun collapse(position: Int) {
        expandedDays.remove(position)
    }

    @MainThread
    override fun state(day: TimeReportDay): TimeReportState {
        val selectedItems = _selectedItems.value
        if (isSelected(selectedItems, day.timeIntervals)) {
            return TimeReportState.SELECTED
        }

        if (day.isRegistered) {
            return TimeReportState.REGISTERED
        }

        return TimeReportState.EMPTY
    }

    private fun isSelected(selectedItems: HashSet<TimeInterval>?, items: List<TimeInterval>) =
        selectedItems?.run { containsAll(items) } ?: false

    @MainThread
    override fun state(timeInterval: TimeInterval): TimeReportState {
        val selectedItems = _selectedItems.value
        if (isSelected(selectedItems, timeInterval)) {
            return TimeReportState.SELECTED
        }

        if (timeInterval is TimeInterval.Registered) {
            return TimeReportState.REGISTERED
        }

        return TimeReportState.EMPTY
    }

    private fun isSelected(selectedItems: HashSet<TimeInterval>?, item: TimeInterval) =
        selectedItems?.run { contains(item) } ?: false

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

    private fun isSelectionActivated(items: Set<TimeInterval>?): Boolean {
        return !items.isNullOrEmpty()
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

    suspend fun refreshActiveTimeReportDay(timeReportDays: List<TimeReportDay>) =
        withContext(Dispatchers.Default) {
            val positions = timeReportDays.filter(::isActive)
                .map { timeReportDays.indexOf(it) }

            if (positions.isEmpty()) {
                return@withContext
            }

            viewActions += TimeReportViewActions.RefreshTimeReportDays(positions)
        }

    private fun isActive(day: TimeReportDay) = day.timeIntervals.any { isActive(it) }
}
