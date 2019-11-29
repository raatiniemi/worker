/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.createproject.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.domain.project.usecase.FindProject
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.shared.model.observeNoValue
import me.raatiniemi.worker.feature.shared.model.observeNonNull
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.koin.testKoinModules
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CreateProjectViewModelTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val debounceDurationInMilliseconds: Long = 300

    private val testDispatcher = TestCoroutineDispatcher()

    private val usageAnalytics by inject<InMemoryUsageAnalytics>()
    private val findProject by inject<FindProject>()
    private val createProject by inject<CreateProject>()

    private lateinit var vm: CreateProjectViewModel

    @Before
    fun setUp() {
        startKoin {
            modules(testKoinModules)
        }

        Dispatchers.setMain(testDispatcher)
        vm = CreateProjectViewModel(
            usageAnalytics = usageAnalytics,
            createProject = createProject,
            findProject = findProject,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `is create enabled with empty name`() = runBlocking(testDispatcher) {
        vm.name += ""

        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertFalse(it)
        }
        vm.viewActions.observeNoValue(timeOutInMilliseconds = debounceDurationInMilliseconds)
    }

    @Test
    fun `is create enabled with duplicated name`() = runBlocking(testDispatcher) {
        createProject(android.name)
        vm.name += android.name.value

        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertFalse(it)
        }
        vm.viewActions.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertEquals(CreateProjectViewActions.DuplicateNameErrorMessage, it)
        }
    }

    @Test
    fun `is create enabled with valid name`() = runBlocking(testDispatcher) {
        vm.name += android.name.value

        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertTrue(it)
        }
        vm.viewActions.observeNoValue(timeOutInMilliseconds = debounceDurationInMilliseconds)
    }

    @Test
    fun `create project with empty name`() = runBlocking(testDispatcher) {
        vm.name += ""

        vm.createProject()

        assertEquals(emptyList<Event>(), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(CreateProjectViewActions.InvalidProjectNameErrorMessage, it)
        }
    }

    @Test
    fun `create project with duplicated name`() = runBlocking(testDispatcher) {
        createProject(android.name)
        vm.name += android.name.value

        vm.createProject()

        assertEquals(emptyList<Event>(), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(CreateProjectViewActions.DuplicateNameErrorMessage, it)
        }
    }

    @Test
    fun `create project with valid name`() = runBlocking(testDispatcher) {
        vm.name += android.name.value

        vm.createProject()

        assertEquals(listOf(Event.ProjectCreate), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(CreateProjectViewActions.CreatedProject, it)
        }
        val actual = findProject(android.name)
        assertEquals(android, actual)
    }
}
