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
import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.ClockOut
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.interactor.RemoveProject
import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.features.projects.all.model.ProjectsItem
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
class AllProjectsViewModelTest {
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
    private lateinit var vm: AllProjectsViewModel

    private val project = Project(1L, "Project #1")

    @Before
    fun setUp() {
        getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        removeProject = RemoveProject(projectRepository)
        vm = AllProjectsViewModel(
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
            assertEquals(AllProjectsViewActions.RefreshProjects(listOf(1)), it)
        }
    }

    @Test
    fun `open project`() {
        val projectsItem = ProjectsItem(project, emptyList())

        vm.open(projectsItem)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.OpenProject(project), it)
        }
    }

    @Test
    fun `toggle clock in with inactive project`() {
        val item = ProjectsItem(project, emptyList())
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `toggle clock in with active project`() {
        timeIntervalRepository.add(newTimeInterval {
            stopInMilliseconds = 0
        })
        val item = ProjectsItem(project, emptyList())
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockInErrorMessage, it)
        }
    }

    @Test
    fun `toggle clock out with confirm clock out`() = runBlocking {
        val item = getProjectsItem(project, true)
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowConfirmClockOutMessage(item, date), it)
        }
    }

    @Test
    fun `toggle clock out project without confirm clock out with active project`() = runBlocking {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        timeIntervalRepository.add(newTimeInterval {
            stopInMilliseconds = 0
        })
        val item = getProjectsItem(project, true)
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `toggle clock out project without confirm clock out and active project`() = runBlocking {
        keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, false)
        val item = getProjectsItem(project, true)
        val date = Date()

        vm.toggle(item, date)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun at() {
        val item = ProjectsItem(project, emptyList())

        vm.at(item)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowChooseTimeForClockActivity(item), it)
        }
    }

    @Test
    fun `remove project`() {
        val item = ProjectsItem(project, emptyList())

        vm.remove(item)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowConfirmRemoveProjectMessage(item), it)
        }
    }

    @Test
    fun `clock in with already active project`() = runBlocking {
        timeIntervalRepository.add(newTimeInterval {
            stopInMilliseconds = 0
        })

        vm.clockIn(project, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockInErrorMessage, it)
        }
    }

    @Test
    fun `clock in project`() = runBlocking {
        vm.clockIn(project, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `clock out without active project`() = runBlocking {
        vm.clockOut(project, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.ShowUnableToClockOutErrorMessage, it)
        }
    }

    @Test
    fun `clock out project`() = runBlocking {
        timeIntervalRepository.add(newTimeInterval {
            stopInMilliseconds = 0
        })

        vm.clockOut(project, Date())

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.UpdateNotification(project), it)
        }
    }

    @Test
    fun `remove project without project`() = runBlocking {
        vm.remove(project)

        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.DismissNotification(project), it)
        }
    }

    @Test
    fun `remove project with project`() = runBlocking {
        projectRepository.add(NewProject("Project #1"))
        val project = Project(1, "Project #1")
        val expected = emptyList<Project>()

        vm.remove(project)

        val actual = projectRepository.findAll()
        assertEquals(expected, actual)
        vm.viewActions.observeNonNull {
            assertEquals(AllProjectsViewActions.DismissNotification(project), it)
        }
    }
}
