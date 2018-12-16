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
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.interactor.FindProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEditTextActions
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CreateProjectViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val repository = ProjectInMemoryRepository()

    private lateinit var findProject: FindProject
    private lateinit var createProject: CreateProject
    private lateinit var vm: CreateProjectViewModel

    @Before
    fun setUp() {
        findProject = FindProject(repository)
        createProject = CreateProject(findProject, repository)
        vm = CreateProjectViewModel(createProject, findProject)
    }

    @Test
    fun `createProject with empty name`() = runBlocking {
        vm.projectName = ""

        vm.createProject()

        vm.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.InvalidProjectNameErrorMessage)
        }
    }

    @Test
    fun `createProject with duplicated name`() = runBlocking {
        repository.add(Project(id = null, name = "Name"))
        vm.projectName = "Name"

        vm.createProject()

        vm.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.DuplicateNameErrorMessage)
        }
    }

    @Test
    fun `createProject with valid name`() = runBlocking {
        vm.projectName = "Name"

        vm.createProject()

        vm.viewActions.observeForever {
            assertNull(it)
        }
        val actual = repository.findAll()
        assertEquals(listOf(Project(id = 1, name = "Name")), actual)
    }

    @Test
    fun `isCreateEnabled with initial value`() {
        vm.isCreateEnabled.observeForever {
            assertFalse(it)
        }
    }

    @Test
    fun `isCreateEnabled with empty name`() {
        vm.projectName = ""

        vm.isCreateEnabled.observeForever {
            assertFalse(it)
        }
    }

    @Test
    fun `isCreateEnabled with valid name`() {
        vm.projectName = "Name"

        vm.isCreateEnabled.observeForever {
            assertTrue(it)
        }
    }

    @Test
    fun `isCreateEnabled with invalid name`() = runBlocking {
        vm.projectName = ""

        vm.createProject()

        vm.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.InvalidProjectNameErrorMessage)
        }
        vm.isCreateEnabled.observeForever {
            assertFalse(it)
        }
    }

    @Test
    fun `isCreateEnabled with duplicated name`() = runBlocking {
        repository.add(Project(id = null, name = "Name"))
        vm.projectName = "Name"

        vm.createProject()

        vm.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.DuplicateNameErrorMessage)
        }
        vm.isCreateEnabled.observeForever {
            assertFalse(it)
        }
    }
}
