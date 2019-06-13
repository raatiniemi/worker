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

package me.raatiniemi.worker.features.projects.createproject.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.interactor.FindProject
import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.projectName
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.features.shared.model.observeNoValue
import me.raatiniemi.worker.features.shared.model.observeNonNull
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CreateProjectViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val debounceDurationInMilliseconds: Long = 300

    private val repository = ProjectInMemoryRepository()
    private val usageAnalytics = InMemoryUsageAnalytics()

    private lateinit var findProject: FindProject
    private lateinit var createProject: CreateProject
    private lateinit var vm: CreateProjectViewModel

    @Before
    fun setUp() {
        findProject = FindProject(repository)
        createProject = CreateProject(findProject, repository)
        vm = CreateProjectViewModel(usageAnalytics, createProject, findProject)
    }

    @Test
    fun `is create enabled with initial value`() = runBlocking {
        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertFalse(it)
        }
        vm.viewActions.observeNoValue(timeOutInMilliseconds = debounceDurationInMilliseconds)
    }

    @Test
    fun `is create enabled with empty name`() = runBlocking {
        repository.add(NewProject(projectName("Name")))
        vm.name = ""

        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertFalse(it)
        }
        vm.viewActions.observeNoValue(timeOutInMilliseconds = debounceDurationInMilliseconds)
    }

    @Test
    fun `is create enabled with duplicated name`() = runBlocking {
        vm.name = "Name"

        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertTrue(it)
        }
        vm.viewActions.observeNoValue(timeOutInMilliseconds = debounceDurationInMilliseconds)
    }

    @Test
    fun `is create enabled with valid name`() = runBlocking {
        vm.name = "Name"

        vm.isCreateEnabled.observeNonNull(timeOutInMilliseconds = debounceDurationInMilliseconds) {
            assertTrue(it)
        }
        vm.viewActions.observeNoValue(timeOutInMilliseconds = debounceDurationInMilliseconds)
    }

    @Test
    fun `create project with empty name`() = runBlocking {
        vm.name = ""

        vm.createProject()

        assertEquals(emptyList<Event>(), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(CreateProjectViewActions.InvalidProjectNameErrorMessage, it)
        }
    }

    @Test
    fun `create project with duplicated name`() = runBlocking {
        repository.add(NewProject(projectName("Name")))
        vm.name = "Name"

        vm.createProject()

        assertEquals(emptyList<Event>(), usageAnalytics.events)
        vm.viewActions.observeNonNull {
            assertEquals(CreateProjectViewActions.DuplicateNameErrorMessage, it)
        }
    }

    @Test
    fun `create project with valid name`() = runBlocking {
        vm.name = "Name"

        vm.createProject()

        assertEquals(listOf(Event.ProjectCreate), usageAnalytics.events)
        val project = Project(id = 1, name = projectName("Name"))
        vm.viewActions.observeNonNull {
            assertEquals(CreateProjectViewActions.CreatedProject(project), it)
        }
        val actual = repository.findAll()
        assertEquals(listOf(project), actual)
    }
}
