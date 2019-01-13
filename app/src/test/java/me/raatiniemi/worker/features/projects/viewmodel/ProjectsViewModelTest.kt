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
import me.raatiniemi.worker.domain.interactor.*
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
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

    private lateinit var getProjects: GetProjects
    private lateinit var getProjectTimeSince: GetProjectTimeSince
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var removeProject: RemoveProject
    private lateinit var vm: ProjectsViewModel

    private val project = Project.from(1L, "Project #1")

    @Before
    fun setUp() {
        getProjects = GetProjects(projectRepository)
        getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        removeProject = RemoveProject(projectRepository)
        vm = ProjectsViewModel(
                keyValueStore,
                getProjects,
                getProjectTimeSince,
                clockIn,
                clockOut,
                removeProject
        )
    }

    @Test
    fun `load projects without projects`() = runBlocking {
        vm.loadProjects()

        vm.projects.observeForever {
            assertEquals(emptyList<ProjectsItem>(), it)
        }
    }

    @Test
    fun `load projects with project`() = runBlocking {
        val project = Project.from("Project #1")
        projectRepository.add(project)
        val expected = listOf(
                ProjectsItem(project.copy(id = 1), emptyList())
        )

        vm.loadProjects()

        vm.projects.observeForever {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `load projects with projects`() = runBlocking {
        val project1 = Project.from("Project #1")
        val project2 = Project.from("Project #2")
        projectRepository.add(project1)
        projectRepository.add(project2)
        val expected = listOf(
                ProjectsItem(project1.copy(id = 1), emptyList()),
                ProjectsItem(project2.copy(id = 2), emptyList())
        )

        vm.loadProjects()

        vm.projects.observeForever {
            assertEquals(expected, it)
        }
    }

    private fun getProjectsItem(project: Project, isActive: Boolean = false): ProjectsItem {
        val registeredTime = if (isActive) {
            listOf(
                    timeInterval {
                        projectId = project.id ?: 0
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
        val projectsItem = ProjectsItem(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.ShowUnableToClockInErrorMessage)
        }
    }

    @Test
    fun `clock in project without id`() = runBlocking {
        val project = Project(null, "Project name")
        val projectsItem = ProjectsItem(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockIn(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.ShowUnableToClockInErrorMessage)
        }
    }

    @Test
    fun `clock in project`() = runBlocking {
        val projectsItem = ProjectsItem(project, emptyList())
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
        val projectsItem = ProjectsItem(project, emptyList())
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
        val projectsItem = ProjectsItem(project, emptyList())
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
        val projectsItem = ProjectsItem(project, emptyList())
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
        val projectsItem = ProjectsItem(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.clockOut(result, Date())

        vm.viewActions.observeForever {
            assertTrue(it is ProjectsViewActions.ShowUnableToClockOutErrorMessage)
        }
    }

    @Test
    fun `clock out project without id`() = runBlocking {
        val project = Project(null, "Project name")
        val projectsItem = ProjectsItem(project, emptyList())
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
        val projectsItem = ProjectsItem(project, listOf(timeInterval))
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
        val projectsItem = ProjectsItem(project, listOf(timeInterval))
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
        val projectsItem = ProjectsItem(project, listOf(timeInterval))
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
        val projectsItem = ProjectsItem(project, listOf(timeInterval))
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

    @Test
    fun `remove project without project id`() = runBlocking {
        val project = Project(null, "Project #1")
        val item = ProjectsItem(project, emptyList())
        val result = ProjectsItemAdapterResult(0, item)

        vm.remove(result)

        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun `remove project without project`() = runBlocking {
        val project = Project(1, "Project #1")
        val item = ProjectsItem(project, emptyList())
        val result = ProjectsItemAdapterResult(0, item)

        vm.remove(result)

        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun `remove project with project`() = runBlocking {
        val project = Project(null, "Project #1")
        val item = ProjectsItem(project.copy(1), emptyList())
        val result = ProjectsItemAdapterResult(0, item)
        projectRepository.add(project)

        vm.remove(result)

        val actual = projectRepository.findAll()
        assertEquals(emptyList<Project>(), actual)
        vm.viewActions.observeForever {
            assertNull(it)
        }
    }
}
