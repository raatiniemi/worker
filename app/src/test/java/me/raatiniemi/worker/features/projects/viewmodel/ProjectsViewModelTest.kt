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
import me.raatiniemi.worker.domain.interactor.RemoveProject
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsAction
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.features.shared.model.observeNoValue
import me.raatiniemi.worker.features.shared.model.observeNonNull
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ProjectsViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore = InMemoryKeyValueStore()
    private val projectRepository = ProjectInMemoryRepository()
    private val timeIntervalRepository = TimeIntervalInMemoryRepository()

    private lateinit var getProjectTimeSince: GetProjectTimeSince
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var removeProject: RemoveProject
    private lateinit var vm: ProjectsViewModel

    private val project = Project(1L, "Project #1")

    @Before
    fun setUp() {
        getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        removeProject = RemoveProject(projectRepository)
        vm = ProjectsViewModel(
                keyValueStore,
                projectRepository,
                getProjectTimeSince,
                clockIn,
                clockOut,
                removeProject
        )
    }

    private fun getProjectsItem(project: Project, isActive: Boolean = false): ProjectsItem {
        val registeredTime = if (isActive) {
            listOf(
                    timeInterval {
                        projectId = project.id
                        startInMilliseconds = 1
                    }
            )
        } else {
            emptyList()
        }

        return ProjectsItem(project, registeredTime)
    }

    @Test
    fun `refresh active projects without projects`() = runBlocking {
        vm.refreshActiveProjects(emptyList())

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active projects without active projects`() = runBlocking {
        val items = listOf(
                getProjectsItem(Project(1, "Project Name"))
        )

        vm.refreshActiveProjects(items)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active projects with active project`() = runBlocking {
        val items = listOf(
                getProjectsItem(Project(1, "Project Name #1")),
                getProjectsItem(Project(2, "Project Name #2"), true)
        )

        vm.refreshActiveProjects(items)

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.RefreshProjects(listOf(1)), it)
        }
    }

    @Test
    fun `accept project action open`() {
        val projectsItem = getProjectsItem(project)

        vm.accept(ProjectsAction.Open(projectsItem))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.OpenProject(project), it)
        }
    }

    @Test
    fun `accept toggle clock out project with confirm clock out`() = runBlocking {
        val item = getProjectsItem(project, true)
        val date = Date()

        vm.accept(ProjectsAction.Toggle(item, date))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.ShowConfirmClockOutMessage(item, date), it)
        }
    }

    @Test
    fun `accept toggle clock out project with already inactive project`() = runBlocking {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val item = getProjectsItem(project, true)
        val date = Date()

        vm.accept(ProjectsAction.Toggle(item, date))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun `accept toggle clock out project`() = runBlocking {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval { stopInMilliseconds = 0 }
        )
        val item = ProjectsItem(project, timeIntervals)
        val date = Date()

        vm.accept(ProjectsAction.Toggle(item, date))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `accept toggle clock in project with already active project`() {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val item = ProjectsItem(project, emptyList())
        val date = Date()

        vm.accept(ProjectsAction.Toggle(item, date))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.ShowUnableToClockInErrorMessage, it)
        }
    }

    @Test
    fun `accept toggle clock in project`() {
        val item = ProjectsItem(project, emptyList())
        val date = Date()

        vm.accept(ProjectsAction.Toggle(item, date))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `accept remove project`() {
        val item = ProjectsItem(project, emptyList())

        vm.accept(ProjectsAction.Remove(item))

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.ShowConfirmRemoveProjectMessage(item), it)
        }
    }

    @Test
    fun `clock in with already active project`() = runBlocking {
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval { stopInMilliseconds = 0 }
        )
        val item = ProjectsItem(project, timeIntervals)

        vm.clockIn(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.ShowUnableToClockInErrorMessage, it)
        }
    }

    @Test
    fun `clock in project`() = runBlocking {
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock in project with month time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.MONTH.rawValue)
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock in project with day time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.DAY.rawValue)
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock in project with invalid time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY, -1)
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out without active project`() = runBlocking {
        val item = ProjectsItem(project, emptyList())

        vm.clockOut(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun `clock out project`() = runBlocking {
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval { stopInMilliseconds = 0 }
        )
        val item = ProjectsItem(project, timeIntervals)

        vm.clockOut(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out project with month time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.MONTH.rawValue)
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval { stopInMilliseconds = 0 }
        )
        val item = ProjectsItem(project, timeIntervals)

        vm.clockOut(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out project with day time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.DAY.rawValue)
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval { stopInMilliseconds = 0 }
        )
        val item = ProjectsItem(project, timeIntervals)

        vm.clockOut(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out project with invalid time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY, -1)
        val newTimeInterval = newTimeInterval { stopInMilliseconds = 0 }
        timeIntervalRepository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval { stopInMilliseconds = 0 }
        )
        val item = ProjectsItem(project, timeIntervals)

        vm.clockOut(item, Date())

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `remove project without project`() = runBlocking {
        val project = Project(1, "Project #1")
        val item = ProjectsItem(project, emptyList())

        vm.remove(item)

        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.DismissNotification(project), it)
        }
    }

    @Test
    fun `remove project with project`() = runBlocking {
        val newProject = NewProject("Project #1")
        val project = Project(1, "Project #1")
        val item = ProjectsItem(project, emptyList())
        projectRepository.add(newProject)
        val expected = emptyList<Project>()

        vm.remove(item)

        val actual = projectRepository.findAll()
        assertEquals(expected, actual)
        vm.viewActions.observeNonNull {
            assertEquals(ProjectsViewActions.DismissNotification(project), it)
        }
    }
}
