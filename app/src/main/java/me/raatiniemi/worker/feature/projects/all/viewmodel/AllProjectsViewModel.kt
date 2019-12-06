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

package me.raatiniemi.worker.feature.projects.all.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.datasource.ProjectDataSource
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.CountProjects
import me.raatiniemi.worker.domain.project.usecase.FindProjects
import me.raatiniemi.worker.domain.project.usecase.RemoveProject
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.GetProjectTimeSince
import me.raatiniemi.worker.domain.timeinterval.usecase.InvalidStartingPointException
import me.raatiniemi.worker.feature.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import me.raatiniemi.worker.feature.projects.all.view.AllProjectsActionListener
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.TracePerformanceEvents
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import me.raatiniemi.worker.util.CoroutineDispatchProvider
import me.raatiniemi.worker.util.DefaultCoroutineDispatchProvider
import timber.log.Timber
import java.util.*

internal class AllProjectsViewModel(
    private val keyValueStore: KeyValueStore,
    private val usageAnalytics: UsageAnalytics,
    countProjects: CountProjects,
    findProjects: FindProjects,
    private val getProjectTimeSince: GetProjectTimeSince,
    private val clockIn: ClockIn,
    private val clockOut: ClockOut,
    private val removeProject: RemoveProject,
    dispatcherProvider: CoroutineDispatchProvider = DefaultCoroutineDispatchProvider()
) : ViewModel(), AllProjectsActionListener {
    private val startingPoint: TimeIntervalStartingPoint
        get() {
            val defaultValue = TimeIntervalStartingPoint.MONTH
            val startingPoint = keyValueStore.int(
                AppKeys.TIME_SUMMARY,
                defaultValue.rawValue
            )

            return try {
                TimeIntervalStartingPoint.from(startingPoint)
            } catch (e: InvalidStartingPointException) {
                Timber.w(e, "Invalid starting point supplied: %i", startingPoint)
                defaultValue
            }
        }

    val projects: LiveData<PagedList<ProjectsItem>>

    val viewActions = ConsumableLiveData<AllProjectsViewActions>()

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()

        val factory = ProjectDataSource.Factory(
            viewModelScope,
            dispatcherProvider,
            countProjects,
            findProjects
        )
        val builder = LivePagedListBuilder(
            factory.map(::buildProjectsItem),
            config
        )
        projects = builder.build()
    }

    private fun buildProjectsItem(project: Project): ProjectsItem {
        val registeredTime = loadRegisteredTimeForProject(project)

        return ProjectsItem(project, registeredTime)
    }

    private fun loadRegisteredTimeForProject(project: Project): List<TimeInterval> {
        return try {
            getProjectTimeSince(project, startingPoint)
        } catch (e: DomainException) {
            Timber.w(e, "Unable to get registered time for project")
            emptyList()
        }
    }

    internal fun createProject() {
        viewActions += AllProjectsViewActions.CreateProject
    }

    internal fun projectCreated() {
        reloadProjects()

        viewActions += AllProjectsViewActions.ProjectCreated
    }

    fun reloadProjects() {
        projects.value?.run {
            dataSource.invalidate()
        }
    }

    @AddTrace(name = TracePerformanceEvents.REFRESH_PROJECTS)
    suspend fun refreshActiveProjects(projects: List<ProjectsItem?>) {
        withContext(Dispatchers.Default) {
            val positions = projects.filterNotNull()
                .filter { it.isActive }
                .map { projects.indexOf(it) }

            if (positions.isEmpty()) {
                return@withContext
            }

            viewActions += AllProjectsViewActions.RefreshProjects(positions)
        }
    }

    override fun open(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectOpen)

        viewActions += AllProjectsViewActions.OpenProject(item.asProject())
    }

    override fun toggle(item: ProjectsItem, date: Date) {
        usageAnalytics.log(Event.TapProjectToggle)

        viewModelScope.launch(Dispatchers.IO) {
            if (!item.isActive) {
                clockInAt(item.asProject(), date)
                return@launch
            }

            if (keyValueStore.bool(AppKeys.CONFIRM_CLOCK_OUT, true)) {
                viewActions += AllProjectsViewActions.ShowConfirmClockOutMessage(item, date)
                return@launch
            }

            clockOutAt(item.asProject(), date)
        }
    }

    override fun at(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectAt)

        viewActions += if (item.isActive) {
            AllProjectsViewActions.ChooseDateAndTimeForClockOut(item)
        } else {
            AllProjectsViewActions.ChooseDateAndTimeForClockIn(item)
        }
    }

    override fun remove(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectRemove)

        viewActions += AllProjectsViewActions.ShowConfirmRemoveProjectMessage(item)
    }

    @AddTrace(name = TracePerformanceEvents.CLOCK_IN)
    suspend fun clockInAt(project: Project, date: Date) = withContext(Dispatchers.IO) {
        try {
            clockIn(project, Milliseconds(date.time))

            usageAnalytics.log(Event.ProjectClockIn)
            viewActions += AllProjectsViewActions.UpdateNotification(project)
            reloadProjects()
        } catch (e: Exception) {
            Timber.w(e, "Unable to clock in project")
            viewActions += AllProjectsViewActions.ShowUnableToClockInErrorMessage
        }
    }

    @AddTrace(name = TracePerformanceEvents.CLOCK_OUT)
    suspend fun clockOutAt(project: Project, date: Date) = withContext(Dispatchers.IO) {
        try {
            clockOut(project, Milliseconds(date.time))

            usageAnalytics.log(Event.ProjectClockOut)
            viewActions += AllProjectsViewActions.UpdateNotification(project)
            reloadProjects()
        } catch (e: Exception) {
            Timber.w(e, "Unable to clock out project")
            viewActions += AllProjectsViewActions.ShowUnableToClockOutErrorMessage
        }
    }

    @AddTrace(name = TracePerformanceEvents.REMOVE_PROJECT)
    suspend fun remove(project: Project) = withContext(Dispatchers.IO) {
        try {
            removeProject(project)

            usageAnalytics.log(Event.ProjectRemove)
            viewActions += AllProjectsViewActions.DismissNotification(project)
            reloadProjects()
        } catch (e: Exception) {
            Timber.w(e, "Unable to remove project")
            viewActions += AllProjectsViewActions.ShowUnableToDeleteProjectErrorMessage
        }
    }
}
