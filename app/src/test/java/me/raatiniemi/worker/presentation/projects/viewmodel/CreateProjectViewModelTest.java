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
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateProjectViewModelTest {
    private CreateProject useCase;
    private CreateProjectViewModel vm;
    private TestSubscriber<Project> test;

    @Before
    public void setUp() {
        useCase = mock(CreateProject.class);
        vm = new CreateProjectViewModel(useCase);
        test = new TestSubscriber<>();
    }

    @Test
    public void createProject_withNull() {
        vm.output.onCreateProject().subscribe(test);

        vm.input.projectName(null);
        vm.input.createProject();

        test.assertError(InvalidProjectNameException.class);
    }

    @Test
    public void createProject_withEmptyName() {
        vm.output.onCreateProject().subscribe(test);

        vm.input.projectName("");
        vm.input.createProject();

        test.assertError(InvalidProjectNameException.class);
    }

    @Test
    public void createProject_withDuplicateName() throws DomainException {
        when(useCase.execute(any(Project.class)))
                .thenThrow(ProjectAlreadyExistsException.class);
        vm.output.onCreateProject().subscribe(test);

        vm.input.projectName("Name");
        vm.input.createProject();

        test.assertError(ProjectAlreadyExistsException.class);
    }

    @Test
    public void createProject_withUnknownError() throws DomainException {
        when(useCase.execute(any(Project.class)))
                .thenThrow(RuntimeException.class);
        vm.output.onCreateProject().subscribe(test);

        vm.input.projectName("Name");
        vm.input.createProject();

        test.assertError(RuntimeException.class);
    }

    @Test
    public void createProject_withValidName() {
        vm.output.onCreateProject().subscribe(test);

        vm.input.projectName("Name");
        vm.input.createProject();

        test.assertValueCount(1);
    }
}
