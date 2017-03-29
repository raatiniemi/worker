/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.presentation.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateProjectViewModelTest {
    private TestSubscriber<String> invalidProjectNameError;
    private TestSubscriber<String> duplicateNameError;
    private TestSubscriber<String> createProjectError;
    private TestSubscriber<Project> createProjectSuccess;

    private CreateProject useCase;
    private CreateProjectViewModel.ViewModel vm;

    @Before
    public void setUp() {
        invalidProjectNameError = new TestSubscriber<>();
        duplicateNameError = new TestSubscriber<>();
        createProjectError = new TestSubscriber<>();
        createProjectSuccess = new TestSubscriber<>();

        useCase = mock(CreateProject.class);
        vm = new CreateProjectViewModel.ViewModel(useCase);
    }

    @Test
    public void createProject_withNull() {
        vm.error.invalidProjectNameError().subscribe(invalidProjectNameError);

        //noinspection ConstantConditions
        vm.input.projectName(null);
        vm.input.createProject();

        invalidProjectNameError.assertValueCount(1);
        duplicateNameError.assertNoValues();
        createProjectError.assertNoValues();
        createProjectSuccess.assertNotCompleted();
    }

    @Test
    public void createProject_withEmptyName() {
        vm.error.invalidProjectNameError().subscribe(invalidProjectNameError);

        vm.input.projectName("");
        vm.input.createProject();

        invalidProjectNameError.assertValueCount(1);
        duplicateNameError.assertNoValues();
        createProjectError.assertNoValues();
        createProjectSuccess.assertNotCompleted();
    }

    @Test
    public void createProject_withDuplicateName() throws DomainException {
        when(useCase.execute(any(Project.class)))
                .thenThrow(ProjectAlreadyExistsException.class);
        vm.error.duplicateProjectNameError().subscribe(duplicateNameError);

        vm.input.projectName("Name");
        vm.input.createProject();

        invalidProjectNameError.assertNoValues();
        duplicateNameError.assertValueCount(1);
        createProjectError.assertNoValues();
        createProjectSuccess.assertNotCompleted();
    }

    @Test
    public void createProject_withUnknownError() throws DomainException {
        when(useCase.execute(any(Project.class)))
                .thenThrow(RuntimeException.class);
        vm.error.createProjectError().subscribe(createProjectError);

        vm.input.projectName("Name");
        vm.input.createProject();

        invalidProjectNameError.assertNoValues();
        duplicateNameError.assertNoValues();
        createProjectError.assertValueCount(1);
        createProjectSuccess.assertNotCompleted();
    }

    @Test
    public void createProject_withValidName() {
        vm.output.createProjectSuccess().subscribe(createProjectSuccess);

        vm.input.projectName("Name");
        vm.input.createProject();

        createProjectSuccess.assertValueCount(1);
        createProjectSuccess.assertNotCompleted();
    }

    @Test
    public void isProjectNameValid_withNull() {
        TestSubscriber<Boolean> isProjectNameValid = new TestSubscriber<>();
        vm.output.isProjectNameValid().subscribe(isProjectNameValid);

        //noinspection ConstantConditions
        vm.input.projectName(null);

        isProjectNameValid.assertValues(Boolean.FALSE, Boolean.FALSE);
    }

    @Test
    public void isProjectNameValid_withEmptyName() {
        TestSubscriber<Boolean> isProjectNameValid = new TestSubscriber<>();
        vm.output.isProjectNameValid().subscribe(isProjectNameValid);

        vm.input.projectName("");

        isProjectNameValid.assertValues(Boolean.FALSE, Boolean.FALSE);
    }

    @Test
    public void isProjectNameValid_withValidName() {
        TestSubscriber<Boolean> isProjectNameValid = new TestSubscriber<>();
        vm.output.isProjectNameValid().subscribe(isProjectNameValid);

        vm.input.projectName("Name");

        isProjectNameValid.assertValues(Boolean.FALSE, Boolean.TRUE);
    }
}
