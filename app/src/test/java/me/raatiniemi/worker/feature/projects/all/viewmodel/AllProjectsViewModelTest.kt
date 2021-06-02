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
import me.raatiniemi.worker.feature.shared.model.observeNonNull
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

    // Refresh active projects

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
            AllProjectsViewActions.UpdateNotification(android)
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
            AllProjectsViewActions.UpdateNotification(android)
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
    fun `clock in at with already active project`() = runBlocking {
        clockIn(android, Milliseconds.now)

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

    // Clock out

    @Test
    fun `clock out without active project`() = runBlocking {
        vm.clockOutAt(android, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun `clock out project when elapsed time is past allowed`() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now - 1.days - 3.minutes)

        vm.clockOutAt(android, Date(now.value))

        assertEquals(emptyList<Event>(), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowElapsedTimePastAllowedErrorMessage, it)
        }
    }

    @Test
    fun `clock out project`() = runBlocking {
        clockIn(android, Milliseconds.now)

        vm.clockOutAt(android, Date())

        assertEquals(listOf(Event.ProjectClockOut), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(android), it)
        }
    }

    // Remove

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
