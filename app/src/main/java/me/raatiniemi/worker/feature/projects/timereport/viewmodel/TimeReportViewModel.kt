/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.timereport.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.datasource.TimeReportWeekDataSource
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timeinterval.usecase.RemoveTime
import me.raatiniemi.worker.domain.timeinterval.usecase.UnableToMarkActiveTimeIntervalAsRegisteredException
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReportWeeks
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReportWeeks
import me.raatiniemi.worker.feature.projects.model.ProjectProvider
import me.raatiniemi.worker.feature.projects.timereport.model.*
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.TracePerformanceEvents
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import me.raatiniemi.worker.util.CoroutineDispatchProvider
import timber.log.Timber

internal class TimeReportViewModel internal constructor(
    private val keyValueStore: KeyValueStore,
    private val usageAnalytics: UsageAnalytics,
    projectProvider: ProjectProvider,
    countTimeReportWeeks: CountTimeReportWeeks,
    findTimeReportWeeks: FindTimeReportWeeks,
    private val markRegisteredTime: MarkRegisteredTime,
    private val removeTime: RemoveTime,
    dispatchProvider: CoroutineDispatchProvider
) : ViewModel(), TimeReportStateManager {
    private val _selectedItems = MutableLiveData<HashSet<TimeInterval>?>()
    private val expandedDays = mutableSetOf<TimeReportDay>()

    var shouldHideRegisteredTime: Boolean
        get() = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)
        set(value) {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, value)
            reloadTimeReport()
        }

    val isSelectionActivated: LiveData<Boolean> = _selectedItems.map(::isSelectionActivated)

    val weeks: LiveData<PagedList<TimeReportWeek>>

    val viewActions = ConsumableLiveData<TimeReportViewActions>()

    init {
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(1)
            .setPrefetchDistance(2)
            .setPageSize(8)
            .setMaxSize(15)
            .setEnablePlaceholders(true)
            .build()

        val factory = TimeReportWeekDataSource.Factory(
            viewModelScope,
            dispatchProvider,
            projectProvider = projectProvider,
            countTimeReportWeeks = countTimeReportWeeks,
            findTimeReportWeeks = findTimeReportWeeks
        )

        weeks = LivePagedListBuilder(factory, config).build()
    }

    fun reloadTimeReport() {
        clearSelection()

        weeks.value?.run {
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
    override fun expanded(day: TimeReportDay): Boolean = expandedDays.contains(day)

    @MainThread
    override fun expand(day: TimeReportDay) {
        expandedDays.add(day)
    }

    @MainThread
    override fun collapse(day: TimeReportDay) {
        expandedDays.remove(day)
    }

    @MainThread
    override fun state(day: TimeReportDay): TimeReportState {
        val selectedItems = _selectedItems.value
        return when {
            isSelected(selectedItems, day.timeIntervals) -> TimeReportState.SELECTED
            day.isRegistered -> TimeReportState.REGISTERED
            else -> TimeReportState.EMPTY
        }
    }

    private fun isSelected(selectedItems: HashSet<TimeInterval>?, items: List<TimeInterval>) =
        selectedItems?.run { containsAll(items) } ?: false

    @MainThread
    override fun state(timeInterval: TimeInterval): TimeReportState {
        val selectedItems = _selectedItems.value
        return when {
            isSelected(selectedItems, timeInterval) -> TimeReportState.SELECTED
            timeInterval is TimeInterval.Registered -> TimeReportState.REGISTERED
            else -> TimeReportState.EMPTY
        }
    }

    private fun isSelected(selectedItems: HashSet<TimeInterval>?, item: TimeInterval) =
        selectedItems?.run { contains(item) } ?: false

    @MainThread
    override fun consume(action: TimeReportSelectAction) {
        try {
            when (action) {
                is TimeReportLongPressAction -> consume(action)
                is TimeReportTapAction -> consume(action)
                else -> throw IllegalArgumentException("Unable to consume unknown select action: $action")
            }
        } catch (e: IllegalArgumentException) {
            Timber.w(e)
        }
    }

    private fun consume(longPress: TimeReportLongPressAction): Boolean {
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

    private fun consume(tap: TimeReportTapAction) {
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

    @AddTrace(name = TracePerformanceEvents.REFRESH_TIME_REPORT)
    fun refreshActiveTimeReportWeek(weeks: List<TimeReportWeek>) {
        val position = findActivePosition(weeks)
        if (position == null) {
            Timber.d("No time report week is active")
            return
        }

        viewActions += TimeReportViewActions.RefreshTimeReportWeek(position)
    }

    private fun findActivePosition(weeks: List<TimeReportWeek>): Int? {
        return weeks.filter(::containsActiveDay)
            .map(weeks::indexOf)
            .firstOrNull()
    }

    private fun containsActiveDay(week: TimeReportWeek?): Boolean {
        if (week == null) {
            return false
        }

        return week.days.firstOrNull { it is TimeReportDay.Active } != null
    }
}
