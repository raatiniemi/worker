/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.domain.project.usecase.FindProject
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.days
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.feature.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import me.raatiniemi.worker.feature.shared.model.observeNoValue
import me.raatiniemi.worker.koin.testKoinModules
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import me.raatiniemi.worker.util.CoroutineDispatchProvider
import me.raatiniemi.worker.util.CoroutineTestRule
import me.raatiniemi.worker.util.TestCoroutineDispatchProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class AllProjectsViewModelTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    private val dispatchProviderModule = module(override = true) {
        single<CoroutineDispatchProvider> {
            TestCoroutineDispatchProvider(coroutineTestRule.testDispatcher)
        }
    }

    private val createProject by inject<CreateProject>()
    private val findProject by inject<FindProject>()

    private val keyValueStore by inject<KeyValueStore>()
    private val usageAnalytics by inject<InMemoryUsageAnalytics>()
    private val clockIn by inject<ClockIn>()

    private val vm by inject<AllProjectsViewModel>()

    @Before
    fun setUp() {
        startKoin {
            modules(testKoinModules + dispatchProviderModule)
        }
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
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            AllProjectsViewActions.CreateProject
        )

        vm.createProject()

        assertEquals(expected, actual)
    }

    @Test
    fun `project created`() {
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            AllProjectsViewActions.ReloadProjects,
            AllProjectsViewActions.ProjectCreated
        )

        vm.projectCreated()

        assertEquals(expected, actual)
    }

    // Refresh active projects

    @Test
    fun `refresh active projects without projects`() {
        val items = emptyList<ProjectsItem>()
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)

        runBlocking {
            vm.refreshActiveProjects(items)
        }

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active projects without active projects`() {
        val items = listOf(
            getProjectsItem(android)
        )

        runBlocking {
            vm.refreshActiveProjects(items)
        }

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active projects with active project`() {
        val items = listOf(
            getProjectsItem(android),
            getProjectsItem(ios, true)
        )
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            AllProjectsViewActions.RefreshProjects(
                listOf(1)
            )
        )

        runBlocking {
            vm.refreshActiveProjects(items)
        }

        assertEquals(expected, actual)
    }

    // Open

    @Test
    fun `open project`() {
        val item = ProjectsItem(android, emptyList())
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectOpen
        )
        val expected = listOf(
            AllProjectsViewActions.OpenProject(android)
        )

        vm.open(item)

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Toggle

    @Test
    fun `toggle clock in with inactive project`() {
        val item = ProjectsItem(android, emptyList())
        val date = Date()
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectToggle,
            Event.ProjectClockIn
        )
        val expected = listOf(
            AllProjectsViewActions.UpdateNotification(android),
            AllProjectsViewActions.ReloadProjects
        )

        runBlocking {
            vm.toggle(item, date)
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle clock in with active project`() {
        runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val date = Date()
        val item = ProjectsItem(android, emptyList())
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectToggle
        )
        val expected = listOf(
            AllProjectsViewActions.ShowUnableToClockInErrorMessage
        )

        runBlocking {
            vm.toggle(item, date)
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle clock out with confirm clock out`() {
        val item = getProjectsItem(android, true)
        val date = Date()
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(Event.TapProjectToggle)
        val expected = listOf(
            AllProjectsViewActions.ShowConfirmClockOutMessage(item, date)
        )

        runBlocking {
            vm.toggle(item, date)
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle clock out project without confirm clock out with active project`() {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val item = getProjectsItem(android, true)
        val date = Date()
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectToggle,
            Event.ProjectClockOut
        )
        val expected = listOf(
            AllProjectsViewActions.UpdateNotification(android),
            AllProjectsViewActions.ReloadProjects
        )

        runBlocking {
            vm.toggle(item, date)
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle clock out project without confirm clock out and active project`() {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val item = getProjectsItem(android, true)
        val date = Date()
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectToggle
        )
        val expected = listOf(
            AllProjectsViewActions.ShowUnableToClockOutErrorMessage
        )

        runBlocking {
            vm.toggle(item, date)
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle clock out project when elapsed time is past allowed`() {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val now = Milliseconds.now
        val timeInterval = runBlocking {
            clockIn(android, now - 1.days - 3.minutes)
        }
        val item = ProjectsItem(
            android,
            listOf(
                timeInterval
            )
        )
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(Event.TapProjectToggle)
        val expected = listOf(
            AllProjectsViewActions.ShowElapsedTimePastAllowedErrorMessage
        )

        runBlocking {
            vm.toggle(item, Date(now.value))
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // At

    @Test
    fun `at with inactive project`() {
        val item = ProjectsItem(android, emptyList())
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectAt
        )
        val expected = listOf(
            AllProjectsViewActions.ChooseDateAndTimeForClockIn(item)
        )

        vm.at(item)

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `at with active project`() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds.now
        }
        val item = ProjectsItem(
            android,
            listOf(
                timeInterval
            )
        )
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(Event.TapProjectAt)
        val expected = listOf(
            AllProjectsViewActions.ChooseDateAndTimeForClockOut(item)
        )

        vm.at(item)

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Remove

    @Test
    fun `remove project`() {
        val item = ProjectsItem(android, emptyList())
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.TapProjectRemove
        )
        val expected = listOf(
            AllProjectsViewActions.ShowConfirmRemoveProjectMessage(item)
        )

        vm.remove(item)

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Clock in

    @Test
    fun `clock in at with already active project`() {
        runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            AllProjectsViewActions.ShowUnableToClockInErrorMessage
        )

        runBlocking {
            vm.clockInAt(android, Date())
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `clock in at project`() {
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(Event.ProjectClockIn)
        val expected = listOf(
            AllProjectsViewActions.UpdateNotification(android),
            AllProjectsViewActions.ReloadProjects
        )

        runBlocking {
            vm.clockInAt(android, Date())
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Clock out

    @Test
    fun `clock out without active project`() {
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            AllProjectsViewActions.ShowUnableToClockOutErrorMessage
        )

        runBlocking {
            vm.clockOutAt(android, Date())
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `clock out project when elapsed time is past allowed`() {
        val now = Milliseconds.now
        runBlocking {
            clockIn(android, now - 1.days - 3.minutes)
        }
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = emptyList<Event>()
        val expected = listOf(
            AllProjectsViewActions.ShowElapsedTimePastAllowedErrorMessage
        )

        runBlocking {
            vm.clockOutAt(android, Date(now.value))
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `clock out project`() {
        runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.ProjectClockOut
        )
        val expected = listOf(
            AllProjectsViewActions.UpdateNotification(android),
            AllProjectsViewActions.ReloadProjects
        )

        runBlocking {
            vm.clockOutAt(android, Date())
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Remove

    @Test
    fun `remove project without project`() {
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.ProjectRemove
        )
        val expected = listOf(
            AllProjectsViewActions.DismissNotification(android),
            AllProjectsViewActions.ReloadProjects
        )

        runBlocking {
            vm.remove(android)
        }

        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove project with project`() {
        runBlocking {
            createProject(android.name)
        }
        val actual = mutableListOf<AllProjectsViewActions>()
        vm.viewActions.observeForever(actual::add)
        val events = listOf(
            Event.ProjectRemove
        )
        val expected = listOf(
            AllProjectsViewActions.DismissNotification(android),
            AllProjectsViewActions.ReloadProjects
        )

        val project = runBlocking {
            vm.remove(android)
            findProject(android.name)
        }

        assertNull(project)
        assertEquals(events, usageAnalytics.events)
        assertEquals(expected, actual)
    }
}
