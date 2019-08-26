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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.data.projects.datasource.ProjectDataSourceFactory
import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.project.usecase.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.GetProjectTimeSince
import me.raatiniemi.worker.features.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.features.projects.all.model.ProjectsItem
import me.raatiniemi.worker.features.shared.model.observeNoValue
import me.raatiniemi.worker.features.shared.model.observeNonNull
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import me.raatiniemi.worker.util.KeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class AllProjectsViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()
    private val usageAnalytics = InMemoryUsageAnalytics()

    private lateinit var findProject: FindProject
    private lateinit var createProject: CreateProject

    private lateinit var countProjects: CountProjects
    private lateinit var findProjects: FindProjects

    private lateinit var projectDataSourceFactory: ProjectDataSourceFactory
    private lateinit var getProjectTimeSince: GetProjectTimeSince
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var removeProject: RemoveProject
    private lateinit var vm: AllProjectsViewModel

    @Before
    fun setUp() {
        val projectRepository = ProjectInMemoryRepository()
        val timeIntervalRepository = TimeIntervalInMemoryRepository()

        findProject = FindProject(projectRepository)
        createProject = CreateProject(findProject, projectRepository)

        countProjects = countProjects(projectRepository)
        findProjects = findProjects(projectRepository)

        projectDataSourceFactory = ProjectDataSourceFactory(countProjects, findProjects)
        getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        removeProject = RemoveProject(projectRepository)

        vm = AllProjectsViewModel(
            keyValueStore,
            usageAnalytics,
            projectDataSourceFactory,
            getProjectTimeSince,
            clockIn,
            clockOut,
            removeProject
        )
    }

    private fun getProjectsItem(project: Project, isActive: Boolean = false): ProjectsItem {
        val registeredTime = if (isActive) {
            listOf(
                timeInterval(android.id) { builder ->
                    builder.id = TimeIntervalId(1)
                    builder.start = Milliseconds(1)
                }
            )
        } else {
            emptyList()
        }

        return ProjectsItem(project, registeredTime)
    }

    @Test
    fun `create project`() {
        vm.createProject()

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.CreateProject, it)
        }
    }

    @Test
    fun `project created`() {
        vm.projectCreated()

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ProjectCreated, it)
        }
    }

    @Test
    fun `refresh active projects without projects`() = runBlocking {
        vm.refreshActiveProjects(emptyList())

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active projects without active projects`() = runBlocking {
        val items = listOf(getProjectsItem(android))

        vm.refreshActiveProjects(items)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active projects with active project`() = runBlocking {
        val items = listOf(
            getProjectsItem(android),
            getProjectsItem(ios, true)
        )

        vm.refreshActiveProjects(items)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.RefreshProjects(listOf(1)), it)
        }
    }

    @Test
    fun `open project`() {
        val projectsItem = ProjectsItem(android, emptyList())

        vm.open(projectsItem)

        assertEquals(listOf(Event.TapProjectOpen), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.OpenProject(android), it)
        }
    }

    @Test
    fun `toggle clock in with inactive project`() {
        val item = ProjectsItem(android, emptyList())
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(
                listOf(Event.TapProjectToggle, Event.ProjectClockIn),
                usageAnalytics.events
            )
            assertEquals(AllProjectsViewActions.UpdateNotification(android), it)
        }
    }

    @Test
    fun `toggle clock in with active project`() {
        clockIn(android, Date())
        val item = ProjectsItem(android, emptyList())
        val date = Date()

        vm.toggle(item, date)

        assertEquals(listOf(Event.TapProjectToggle), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockInErrorMessage, it)
        }
    }

    @Test
    fun `toggle clock out with confirm clock out`() = runBlocking {
        val item = getProjectsItem(android, true)
        val date = Date()

        vm.toggle(item, date)

        assertEquals(listOf(Event.TapProjectToggle), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowConfirmClockOutMessage(item, date), it)
        }
    }

    @Test
    fun `toggle clock out project without confirm clock out with active project`() = runBlocking {
        clockIn(android, Date())
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val item = getProjectsItem(android, true)
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(
                listOf(Event.TapProjectToggle, Event.ProjectClockOut),
                usageAnalytics.events
            )
            assertEquals(AllProjectsViewActions.UpdateNotification(android), it)
        }
    }

    @Test
    fun `toggle clock out project without confirm clock out and active project`() = runBlocking {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val item = getProjectsItem(android, true)
        val date = Date()

        vm.toggle(item, date)

        assertEquals(listOf(Event.TapProjectToggle), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun at() {
        val item = ProjectsItem(android, emptyList())

        vm.at(item)

        assertEquals(listOf(Event.TapProjectAt), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowChooseTimeForClockActivity(item), it)
        }
    }

    @Test
    fun `remove project`() {
        val item = ProjectsItem(android, emptyList())

        vm.remove(item)

        assertEquals(listOf(Event.TapProjectRemove), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowConfirmRemoveProjectMessage(item), it)
        }
    }

    @Test
    fun `clock in at with already active project`() = runBlocking {
        clockIn(android, Date())

        vm.clockInAt(android, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockInErrorMessage, it)
        }
    }

    @Test
    fun `clock in at project`() = runBlocking {
        vm.clockInAt(android, Date())

        assertEquals(listOf(Event.ProjectClockIn), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(android), it)
        }
    }

    @Test
    fun `clock out without active project`() = runBlocking {
        vm.clockOutAt(android, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun `clock out project`() = runBlocking {
        clockIn(android, Date())

        vm.clockOutAt(android, Date())

        assertEquals(listOf(Event.ProjectClockOut), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(android), it)
        }
    }

    @Test
    fun `remove project without project`() = runBlocking {
        vm.remove(android)

        assertEquals(listOf(Event.ProjectRemove), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.DismissNotification(android), it)
        }
    }

    @Test
    fun `remove project with project`() = runBlocking {
        createProject(android.name)

        vm.remove(android)

        assertEquals(listOf(Event.ProjectRemove), usageAnalytics.events)
        val actual = findProject(android.name)
        assertNull(actual)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.DismissNotification(android), it)
        }
    }
}
