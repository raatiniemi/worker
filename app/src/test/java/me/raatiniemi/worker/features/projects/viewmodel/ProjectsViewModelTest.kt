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
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.interactor.GetProjects
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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
    private lateinit var vm: ProjectsViewModel

    @Before
    fun setUp() {
        getProjects = GetProjects(projectRepository)
        getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)
        vm = ProjectsViewModel(keyValueStore, getProjects, getProjectTimeSince)
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
                ProjectsItem.from(project.copy(id = 1), emptyList())
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
                ProjectsItem.from(project1.copy(id = 1), emptyList()),
                ProjectsItem.from(project2.copy(id = 2), emptyList())
        )

        vm.loadProjects()

        vm.projects.observeForever {
            assertEquals(expected, it)
        }
    }
}
