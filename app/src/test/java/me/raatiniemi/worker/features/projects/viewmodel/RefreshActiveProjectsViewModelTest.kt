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

import me.raatiniemi.worker.features.projects.model.ProjectsItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class RefreshActiveProjectsViewModelTest {
    private val positionsForActiveProjects: TestSubscriber<List<Int>> = TestSubscriber()

    private lateinit var vm: RefreshActiveProjectsViewModel.ViewModel

    private fun getProjectsItem(isActive: Boolean): ProjectsItem {
        val projectsItem = mock(ProjectsItem::class.java)
        `when`(projectsItem.isActive)
                .thenReturn(isActive)

        return projectsItem
    }

    @Before
    fun setUp() {
        vm = RefreshActiveProjectsViewModel.ViewModel()
    }

    @Test
    fun `positionsForActiveProjects without projects`() {
        vm.output().positionsForActiveProjects()
                .subscribe(positionsForActiveProjects)

        vm.input().projects(emptyList())

        positionsForActiveProjects.assertValue(emptyList())
        positionsForActiveProjects.assertNotCompleted()
    }

    @Test
    fun `positionsForActiveProjects without active projects`() {
        vm.output().positionsForActiveProjects()
                .subscribe(positionsForActiveProjects)

        vm.input().projects(listOf(getProjectsItem(false)))

        positionsForActiveProjects.assertValue(emptyList())
        positionsForActiveProjects.assertNotCompleted()
    }

    @Test
    fun `positionsForActiveProjects with active project`() {
        vm.output().positionsForActiveProjects()
                .subscribe(positionsForActiveProjects)
        val projectItems = listOf(
                getProjectsItem(false),
                getProjectsItem(true)
        )

        vm.input().projects(projectItems)

        positionsForActiveProjects.assertValue(listOf(1))
        positionsForActiveProjects.assertNotCompleted()
    }
}
