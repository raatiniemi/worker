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
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class RefreshActiveProjectsViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var vm: RefreshActiveProjectsViewModel

    private fun getProjectsItem(isActive: Boolean): ProjectsItem {
        val projectsItem = mock(ProjectsItem::class.java)
        `when`(projectsItem.isActive)
                .thenReturn(isActive)

        return projectsItem
    }

    @Before
    fun setUp() {
        vm = RefreshActiveProjectsViewModel()
    }

    @Test
    fun `activePositions without projects`() {
        vm.projects(emptyList())

        vm.activePositions.observeForever {
            assertEquals(emptyList<Int>(), it)
        }
    }

    @Test
    fun `activePositions without active projects`() {
        val projects = listOf(
                getProjectsItem(false)
        )

        vm.projects(projects)

        vm.activePositions.observeForever {
            assertEquals(emptyList<Int>(), it)
        }
    }

    @Test
    fun `activePositions with active project`() {
        val projectItems = listOf(
                getProjectsItem(false),
                getProjectsItem(true)
        )

        vm.projects(projectItems)

        vm.activePositions.observeForever {
            assertEquals(listOf(1), it)
        }
    }
}
