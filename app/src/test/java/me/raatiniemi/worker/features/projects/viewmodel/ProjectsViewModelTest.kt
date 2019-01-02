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
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.interactor.GetProjects
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class ProjectsViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val projects: TestSubscriber<List<ProjectsItem>> = TestSubscriber()
    private val keyValueStore = InMemoryKeyValueStore()

    private lateinit var getProjects: GetProjects
    private lateinit var getProjectTimeSince: GetProjectTimeSince
    private lateinit var vm: ProjectsViewModel

    private fun getProjects(): List<Project> {
        val project = Project.from("Name")

        return listOf(project)
    }

    @Before
    fun setUp() {
        getProjects = mock(GetProjects::class.java)
        getProjectTimeSince = mock(GetProjectTimeSince::class.java)
        vm = ProjectsViewModel(keyValueStore, getProjects, getProjectTimeSince)
    }

    @Test
    fun projects_withGetProjectsError() {
        `when`(getProjects.execute())
                .thenThrow(DomainException::class.java)

        vm.projects().subscribe(projects)

        projects.assertValueCount(1)
        projects.assertCompleted()
        vm.viewActions.observeForever {
            assertEquals(ProjectsViewActions.ShowUnableToGetProjectsErrorMessage, it)
        }
    }

    @Test
    fun projects_withGetProjectTimeSinceError() {
        `when`(getProjects.execute())
                .thenReturn(getProjects())
        `when`(getProjectTimeSince.execute(any(Project::class.java), eq(TimeIntervalStartingPoint.MONTH)))
                .thenThrow(ClockOutBeforeClockInException::class.java)

        vm.projects().subscribe(projects)

        projects.assertValueCount(1)
        projects.assertCompleted()
        vm.viewActions.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun projects() {
        `when`(getProjects.execute())
                .thenReturn(getProjects())
        `when`(getProjectTimeSince.execute(any(Project::class.java), any(TimeIntervalStartingPoint::class.java)))
                .thenReturn(emptyList<TimeInterval>())

        vm.projects().subscribe(projects)

        projects.assertValueCount(1)
        projects.assertCompleted()
        vm.viewActions.observeForever {
            assertNull(it)
        }
        verify(getProjectTimeSince)
                .execute(any(Project::class.java), eq(TimeIntervalStartingPoint.MONTH))
    }

    @Test
    fun projects_withWeekAsTimeSummaryStartingPoint() {
        `when`(getProjects.execute())
                .thenReturn(getProjects())
        `when`(getProjectTimeSince.execute(any(Project::class.java), any(TimeIntervalStartingPoint::class.java)))
                .thenReturn(emptyList<TimeInterval>())
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.WEEK.rawValue)

        vm.projects().subscribe(projects)

        projects.assertValueCount(1)
        projects.assertCompleted()
        vm.viewActions.observeForever {
            assertNull(it)
        }
        verify(getProjectTimeSince)
                .execute(any(Project::class.java), eq(TimeIntervalStartingPoint.WEEK))
    }

    @Test
    fun projects_withInvalidTimeSummaryStartingPoint() {
        `when`(getProjects.execute())
                .thenReturn(getProjects())
        `when`(getProjectTimeSince.execute(any(Project::class.java), any(TimeIntervalStartingPoint::class.java)))
                .thenReturn(emptyList<TimeInterval>())
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, -1)

        vm.projects().subscribe(projects)

        projects.assertValueCount(1)
        projects.assertCompleted()
        vm.viewActions.observeForever {
            assertNull(it)
        }
        verify(getProjectTimeSince)
                .execute(any(Project::class.java), eq(TimeIntervalStartingPoint.MONTH))
    }

    @Test
    fun projects_withoutProjects() {
        `when`(getProjects.execute())
                .thenReturn(emptyList())

        vm.projects().subscribe(projects)

        projects.assertValueCount(1)
        projects.assertCompleted()
        vm.viewActions.observeForever {
            assertNull(it)
        }
    }
}
