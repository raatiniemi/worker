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

package me.raatiniemi.worker.feature.projects.createproject.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.domain.project.usecase.FindProject
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectError
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.shared.model.Error
import me.raatiniemi.worker.feature.shared.model.observeNoValue
import me.raatiniemi.worker.feature.shared.model.observeNonNull
import me.raatiniemi.worker.koin.testKoinModules
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(JUnit4::class)
class CreateProjectViewModelTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val usageAnalytics by inject<InMemoryUsageAnalytics>()
    private val findProject by inject<FindProject>()
    private val createProject by inject<CreateProject>()

    private val vm by inject<CreateProjectViewModel>()

    @Before
    fun setUp() {
        startKoin {
            modules(testKoinModules)
        }
    }

    // On name change

    @Test
    fun `on name change with invalid name`() {
        val actualErrors = mutableListOf<Error?>()
        vm.error.observeForever(actualErrors::add)
        val expectedErrors = listOf<Error?>(
            null,
            CreateProjectError.InvalidName
        )

        runBlocking {
            vm.onNameChange("")
        }

        assertEquals(expectedErrors, actualErrors)
        vm.name.observeNonNull {
            assertEquals("", it)
        }
    }

    @Test
    fun `on name change when project already exists`() {
        runBlocking {
            createProject(android.name)
        }
        val actualErrors = mutableListOf<Error?>()
        vm.error.observeForever(actualErrors::add)
        val expectedErrors = listOf<Error?>(
            null,
            CreateProjectError.ProjectAlreadyExists
        )

        runBlocking {
            vm.onNameChange(android.name.value)
        }

        assertEquals(expectedErrors, actualErrors)
        vm.name.observeNonNull {
            assertEquals(android.name.value, it)
        }
    }

    @Test
    fun `on name change with valid name`() {
        val actualErrors = mutableListOf<Error?>()
        vm.error.observeForever(actualErrors::add)
        val expectedErrors = listOf<Error?>(
            null
        )

        runBlocking {
            vm.onNameChange(android.name.value)
        }

        assertEquals(expectedErrors, actualErrors)
        vm.name.observeNonNull {
            assertEquals(android.name.value, it)
        }
    }

    // Create project

    @Test
    fun `create project with empty name`() {
        val actualErrors = mutableListOf<Error?>()
        vm.error.observeForever(actualErrors::add)
        val expectedEvents = emptyList<Event>()
        val expectedErrors = listOf<Error?>(
            CreateProjectError.InvalidName
        )

        runBlocking {
            vm.createProject("")
        }

        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expectedErrors, actualErrors)
        vm.viewActions.observeNoValue()
    }

    @Test
    fun `create project with duplicated name`() {
        runBlocking {
            createProject(android.name)
        }
        val actualErrors = mutableListOf<Error?>()
        vm.error.observeForever(actualErrors::add)
        val expectedEvents = emptyList<Event>()
        val expectedErrors = listOf<Error?>(
            CreateProjectError.ProjectAlreadyExists
        )

        runBlocking {
            vm.createProject(android.name.value)
        }

        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expectedErrors, actualErrors)
        vm.viewActions.observeNoValue()
    }

    @Test
    fun `create project with valid name`() {
        val actualViewActions = mutableListOf<CreateProjectViewActions?>()
        vm.viewActions.observeForever(actualViewActions::add)
        val expectedEvents = listOf(
            Event.ProjectCreate
        )
        val expectedViewActions = listOf(
            CreateProjectViewActions.Created(android)
        )

        runBlocking {
            vm.createProject(android.name.value)
        }

        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expectedViewActions, actualViewActions)
        vm.error.observeNoValue()
        val actual = runBlocking {
            findProject(android.name)
        }
        assertEquals(android, actual)
    }

    // Dismiss

    @Test
    fun dismiss() {
        val actualViewActions = mutableListOf<CreateProjectViewActions?>()
        vm.viewActions.observeForever(actualViewActions::add)
        val expectedViewActions = listOf(
            CreateProjectViewActions.Dismiss
        )

        vm.dismiss()

        assertEquals(expectedViewActions, actualViewActions)
    }
}
