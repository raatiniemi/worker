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

package me.raatiniemi.worker.feature.projects.all.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.datasource.AllProjectsPagingSource
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.CountProjects
import me.raatiniemi.worker.domain.project.usecase.FindProjects
import me.raatiniemi.worker.domain.project.usecase.RemoveProject
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.ElapsedTimePastAllowedException
import me.raatiniemi.worker.domain.timeinterval.usecase.GetProjectTimeSince
import me.raatiniemi.worker.feature.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import timber.log.Timber
import java.util.*

private const val ALL_PROJECTS_PAGE_SIZE = 6

internal class AllProjectsViewModel(
    private val keyValueStore: KeyValueStore,
    private val usageAnalytics: UsageAnalytics,
    countProjects: CountProjects,
    findProjects: FindProjects,
    getProjectTimeSince: GetProjectTimeSince,
    private val clockIn: ClockIn,
    private val clockOut: ClockOut,
    private val removeProject: RemoveProject
) : ViewModel() {
    val projects = Pager(PagingConfig(pageSize = ALL_PROJECTS_PAGE_SIZE)) {
        AllProjectsPagingSource(
            keyValueStore,
            countProjects,
            findProjects,
            getProjectTimeSince
        )
    }.flow.cachedIn(viewModelScope)

    val viewActions = ConsumableLiveData<AllProjectsViewActions>()

    internal fun createProject() {
        viewActions += AllProjectsViewActions.CreateProject
    }

    internal fun projectCreated() {
        reloadProjects()

        viewActions += AllProjectsViewActions.ProjectCreated
    }

    fun reloadProjects() {
        viewActions += AllProjectsViewActions.ReloadProjects
    }

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

    fun open(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectOpen)

        viewActions += AllProjectsViewActions.OpenProject(item.asProject())
    }

    suspend fun toggle(item: ProjectsItem, date: Date) {
        usageAnalytics.log(Event.TapProjectToggle)

        if (!item.isActive) {
            clockInAt(item.asProject(), date)
            return
        }

        if (keyValueStore.bool(AppKeys.CONFIRM_CLOCK_OUT, true)) {
            viewActions += AllProjectsViewActions.ShowConfirmClockOutMessage(item, date)
            return
        }

        clockOutAt(item.asProject(), date)
    }

    fun at(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectAt)

        viewActions += if (item.isActive) {
            AllProjectsViewActions.ChooseDateAndTimeForClockOut(item)
        } else {
            AllProjectsViewActions.ChooseDateAndTimeForClockIn(item)
        }
    }

    fun remove(item: ProjectsItem) {
        usageAnalytics.log(Event.TapProjectRemove)

        viewActions += AllProjectsViewActions.ShowConfirmRemoveProjectMessage(item)
    }

    suspend fun clockInAt(project: Project, date: Date) {
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

    suspend fun clockOutAt(project: Project, date: Date) {
        try {
            clockOut(project, Milliseconds(date.time))

            usageAnalytics.log(Event.ProjectClockOut)
            viewActions += AllProjectsViewActions.UpdateNotification(project)
            reloadProjects()
        } catch (e: ElapsedTimePastAllowedException) {
            viewActions += AllProjectsViewActions.ShowElapsedTimePastAllowedErrorMessage
        } catch (e: Exception) {
            Timber.w(e, "Unable to clock out project")
            viewActions += AllProjectsViewActions.ShowUnableToClockOutErrorMessage
        }
    }

    suspend fun remove(project: Project) {
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
