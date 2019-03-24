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

package me.raatiniemi.worker.features.projects.all.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.projects.datasource.ProjectDataSourceFactory
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.ClockOut
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.interactor.RemoveProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.repository.ProjectRepository
import me.raatiniemi.worker.features.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.features.projects.all.model.ProjectsItem
import me.raatiniemi.worker.features.projects.all.view.AllProjectsActionListener
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.features.shared.model.plusAssign
import me.raatiniemi.worker.features.shared.viewmodel.CoroutineScopedViewModel
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import timber.log.Timber
import java.util.*

internal class AllProjectsViewModel(
        private val keyValueStore: KeyValueStore,
        private val usageAnalytics: UsageAnalytics,
        projectRepository: ProjectRepository,
        private val getProjectTimeSince: GetProjectTimeSince,
        private val clockIn: ClockIn,
        private val clockOut: ClockOut,
        private val removeProject: RemoveProject
) : CoroutineScopedViewModel(), AllProjectsActionListener {
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

    private val factory = ProjectDataSourceFactory(projectRepository)
            .map { buildProjectsItem(it) }

    val viewActions = ConsumableLiveData<AllProjectsViewActions>()

    init {
        val config = PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(true)
                .build()

        projects = LivePagedListBuilder(factory, config).build()
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

    fun reloadProjects() {
        projects.value?.run {
            dataSource.invalidate()
        }
    }

    suspend fun refreshActiveProjects(projects: List<ProjectsItem?>) = withContext(Dispatchers.Default) {
        val positions = projects.filterNotNull()
                .filter { it.isActive }
                .map { projects.indexOf(it) }

        if (positions.isEmpty()) {
            return@withContext
        }

        viewActions += AllProjectsViewActions.RefreshProjects(positions)
    }

    override fun open(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectOpen)

        viewActions += AllProjectsViewActions.OpenProject(item.asProject())
    }

    override fun toggle(item: ProjectsItem, date: Date) {
        usageAnalytics.log(Event.TapProjectToggle)

        launch {
            if (!item.isActive) {
                clockIn(item.asProject(), date)
                return@launch
            }

            if (keyValueStore.bool(AppKeys.CONFIRM_CLOCK_OUT, true)) {
                viewActions += AllProjectsViewActions.ShowConfirmClockOutMessage(item, date)
                return@launch
            }

            clockOut(item.asProject(), date)
        }
    }

    override fun at(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectAt)

        viewActions += AllProjectsViewActions.ShowChooseTimeForClockActivity(item)
    }

    override fun remove(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectRemove)

        viewActions += AllProjectsViewActions.ShowConfirmRemoveProjectMessage(item)
    }

    suspend fun clockIn(project: Project, date: Date) = withContext(Dispatchers.IO) {
        try {
            clockIn(project.id, date)

            usageAnalytics.log(Event.ProjectClockIn)
            viewActions += AllProjectsViewActions.UpdateNotification(project)
            reloadProjects()
        } catch (e: Exception) {
            Timber.w(e, "Unable to clock in project")
            viewActions += AllProjectsViewActions.ShowUnableToClockInErrorMessage
        }
    }

    suspend fun clockOut(project: Project, date: Date) = withContext(Dispatchers.IO) {
        try {
            clockOut(project.id, date)

            usageAnalytics.log(Event.ProjectClockOut)
            viewActions += AllProjectsViewActions.UpdateNotification(project)
            reloadProjects()
        } catch (e: Exception) {
            Timber.w(e, "Unable to clock out project")
            viewActions += AllProjectsViewActions.ShowUnableToClockOutErrorMessage
        }
    }

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
