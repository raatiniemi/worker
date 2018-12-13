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
    private lateinit var useCase: CreateProject
    private lateinit var vm: CreateProjectViewModel.ViewModel

    @Before
    fun setUp() {
        findProject = FindProject(repository)
        useCase = CreateProject(findProject, repository)
        vm = CreateProjectViewModel.ViewModel(useCase)
    }

    @Test
    fun `createProject with empty name`() = runBlocking {
        vm.input.projectName.value = ""

        vm.input.createProject()

        vm.error.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.InvalidProjectNameErrorMessage)
        }
    }

    @Test
    fun `createProject with duplicated name`() = runBlocking {
        repository.add(Project(id = null, name = "Name"))
        vm.input.projectName.value = "Name"

        vm.input.createProject()

        vm.error.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.DuplicateNameErrorMessage)
        }
    }

    @Test
    fun `createProject with valid name`() = runBlocking {
        vm.input.projectName.value = "Name"

        vm.input.createProject()

        vm.error.viewActions.observeForever {
            assertTrue(it is CreateProjectEditTextActions.DuplicateNameErrorMessage)
        }
        val actual = repository.findAll()
        assertEquals(listOf(Project(id = 1, name = "Name")), actual)
    }

    @Test
    fun `isCreateEnabled with initial value`() {
        vm.output.isCreateEnabled.observeForever {
            assertFalse(it)
        }
    }

    @Test
    fun `isCreateEnabled with empty name`() {
        vm.input.projectName.value = ""

        vm.output.isCreateEnabled.observeForever {
            assertFalse(it)
        }
    }

    @Test
    fun `isCreateEnabled with valid name`() {
        vm.input.projectName.value = "Name"

        vm.output.isCreateEnabled.observeForever {
            assertTrue(it)
        }
    }
}
