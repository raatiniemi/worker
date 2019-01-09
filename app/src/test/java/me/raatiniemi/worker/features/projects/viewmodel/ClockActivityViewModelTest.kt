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

package me.raatiniemi.worker.features.projects.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.ClockOut
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ClockActivityViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore = InMemoryKeyValueStore()
    private val timeIntervalRepository = TimeIntervalInMemoryRepository()
    private val clockIn = ClockIn(timeIntervalRepository)
    private val clockOut = ClockOut(timeIntervalRepository)
    private val getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)
    private val project = Project.from(1L, "Project #1")

    private lateinit var vm: ClockActivityViewModel

    @Before
    fun setUp() {
        vm = ClockActivityViewModel(
                keyValueStore,
                clockIn,
                clockOut,
                getProjectTimeSince
        )
    }

    @Test
    fun `clock in with already active project`() = runBlocking {
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.ShowUnableToClockInErrorMessage)
        }
    }

    @Test
    fun `clock in project`() = runBlocking {
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, true)
            }
        }
    }

    @Test
    fun `clock in project with month time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, true)
            }
        }
    }

    @Test
    fun `clock in project with day time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.DAY.rawValue)
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, true)
            }
        }
    }

    @Test
    fun `clock in project with invalid time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, -1)
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, true)
            }
        }
    }

    @Test
    fun `clock out without active project`() = runBlocking {
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockOut(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.ShowUnableToClockOutErrorMessage)
        }
    }

    @Test
    fun `clock out project`() = runBlocking {
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockOut(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, false)
            }
        }
    }

    @Test
    fun `clock out project with month time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockOut(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, false)
            }
        }
    }

    @Test
    fun `clock out project with day time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.DAY.rawValue)
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockOut(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, false)
            }
        }
    }

    @Test
    fun `clock out project with invalid time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, -1)
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockOut(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.UpdateProject)

            if (it is ProjectsViewActions.UpdateProject) {
                assertActiveState(it.result, false)
            }
        }
    }

    private fun assertActiveState(result: ProjectsItemAdapterResult, expected: Boolean) {
        val projectsItem = result.projectsItem

        assertEquals(expected, projectsItem.isActive)
    }
}
