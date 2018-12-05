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

import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEditTextActions
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class CreateProjectViewModelTest {
    private val invalidProjectNameError: TestSubscriber<CreateProjectEditTextActions.InvalidProjectNameErrorMessage> = TestSubscriber()
    private val duplicateNameError: TestSubscriber<CreateProjectEditTextActions.DuplicateNameErrorMessage> = TestSubscriber()
    private val createProjectError: TestSubscriber<CreateProjectEditTextActions.UnknownErrorMessage> = TestSubscriber()
    private val createProjectSuccess: TestSubscriber<Project> = TestSubscriber()

    private lateinit var useCase: CreateProject
    private lateinit var vm: CreateProjectViewModel.ViewModel

    @Before
    fun setUp() {
        useCase = mock(CreateProject::class.java)
        vm = CreateProjectViewModel.ViewModel(useCase)
    }

    @Test
    fun createProject_withEmptyName() {
        vm.error.invalidProjectNameError.subscribe(invalidProjectNameError)

        vm.input.projectName("")
        vm.input.createProject()

        invalidProjectNameError.assertValueCount(1)
        duplicateNameError.assertNoValues()
        createProjectError.assertNoValues()
        createProjectSuccess.assertNotCompleted()
    }

    @Test
    fun createProject_withDuplicateName() {
        `when`(useCase.execute(any(Project::class.java)))
                .thenThrow(ProjectAlreadyExistsException::class.java)
        vm.error.duplicateProjectNameError.subscribe(duplicateNameError)

        vm.input.projectName("Name")
        vm.input.createProject()

        invalidProjectNameError.assertNoValues()
        duplicateNameError.assertValueCount(1)
        createProjectError.assertNoValues()
        createProjectSuccess.assertNotCompleted()
    }

    @Test
    fun createProject_withUnknownError() {
        `when`(useCase.execute(any(Project::class.java)))
                .thenThrow(RuntimeException::class.java)
        vm.error.createProjectError.subscribe(createProjectError)

        vm.input.projectName("Name")
        vm.input.createProject()

        invalidProjectNameError.assertNoValues()
        duplicateNameError.assertNoValues()
        createProjectError.assertValueCount(1)
        createProjectSuccess.assertNotCompleted()
    }

    @Test
    fun createProject_withValidName() {
        vm.output.createProjectSuccess.subscribe(createProjectSuccess)

        vm.input.projectName("Name")
        vm.input.createProject()

        createProjectSuccess.assertValueCount(1)
        createProjectSuccess.assertNotCompleted()
    }

    @Test
    fun isProjectNameValid_withEmptyName() {
        val isProjectNameValid = TestSubscriber<Boolean>()
        vm.output.isProjectNameValid.subscribe(isProjectNameValid)

        vm.input.projectName("")

        isProjectNameValid.assertValues(false, false)
    }

    @Test
    fun isProjectNameValid_withValidName() {
        val isProjectNameValid = TestSubscriber<Boolean>()
        vm.output.isProjectNameValid.subscribe(isProjectNameValid)

        vm.input.projectName("Name")

        isProjectNameValid.assertValues(false, true)
    }
}
