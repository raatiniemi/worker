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
import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.*
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

        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun `refresh active projects without active projects`() = runBlocking {
        val items = listOf(
                getProjectsItem(Project(1, "Project Name"))
        )

        vm.refreshActiveProjects(items)

        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun `refresh active projects with active project`() = runBlocking {
        val items = listOf(
                getProjectsItem(Project(1, "Project Name #1")),
                getProjectsItem(Project(2, "Project Name #2"), true)
        )

        vm.refreshActiveProjects(items)

        vm.viewActions.observeForever {
            val expected = ProjectsViewActions.RefreshProjects(positions = listOf(1))
            assertEquals(expected, it)
        }
    }

    @Test
    fun `clock in with already active project`() = runBlocking {
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val item = ProjectsItem(project, listOf(timeInterval))

        vm.clockIn(item, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.ShowUnableToClockInErrorMessage)
        }
    }

    @Test
    fun `clock in project`() = runBlocking {
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock in project with month time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock in project with day time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.DAY.rawValue)
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock in project with invalid time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, -1)
        val item = ProjectsItem(project, emptyList())

        vm.clockIn(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out without active project`() = runBlocking {
        val item = ProjectsItem(project, emptyList())

        vm.clockOut(item, Date())

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
        val item = ProjectsItem(project, listOf(timeInterval))

        vm.clockOut(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out project with month time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val item = ProjectsItem(project, listOf(timeInterval))

        vm.clockOut(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out project with day time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.DAY.rawValue)
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val item = ProjectsItem(project, listOf(timeInterval))

        vm.clockOut(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out project with invalid time interval starting point`() = runBlocking {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, -1)
        val timeInterval = timeInterval {
            stopInMilliseconds = 0
        }
        timeIntervalRepository.add(timeInterval)
        val item = ProjectsItem(project, listOf(timeInterval))

        vm.clockOut(item, Date())

        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `remove project without project id`() = runBlocking {
        val project = Project(1, "Project #1")
        val item = ProjectsItem(project, emptyList())

        vm.remove(item)

        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun `remove project without project`() = runBlocking {
        val project = Project(1, "Project #1")
        val item = ProjectsItem(project, emptyList())

        vm.remove(item)

        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun `remove project with project`() = runBlocking {
        val newProject = NewProject("Project #1")
        val project = Project(1, "Project #1")
        val item = ProjectsItem(project, emptyList())
        projectRepository.add(newProject)

        vm.remove(item)

        val actual = projectRepository.findAll()
        assertEquals(emptyList<Project>(), actual)
        vm.viewActions.observeForever {
            assertNull(it)
        }
    }
}
