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

package me.raatiniemi.worker.features.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class RemoveProjectViewModelTest {
    private RemoveProject removeProject;
    private RemoveProjectViewModel.ViewModel vm;

    private TestSubscriber<ProjectsItemAdapterResult> removeProjectSuccess;
    private TestSubscriber<ProjectsItemAdapterResult> removeProjectError;

    @Before
    public void setUp() {
        removeProject = mock(RemoveProject.class);
        vm = new RemoveProjectViewModel.ViewModel(removeProject);

        removeProjectSuccess = new TestSubscriber<>();
        vm.output().removeProjectSuccess().subscribe(removeProjectSuccess);

        removeProjectError = new TestSubscriber<>();
        vm.error().removeProjectError().subscribe(removeProjectError);
    }

    @Test
    public void deleteProject_withError() throws InvalidProjectNameException {
        Project project = Project.builder("Name").build();
        ProjectsItem item = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, item);
        doThrow(RuntimeException.class)
                .when(removeProject).execute(any());

        vm.input().remove(result);

        removeProjectSuccess.assertNoValues();
        removeProjectSuccess.assertNoTerminalEvent();
        removeProjectError.assertValue(result);
    }

    @Test
    public void deleteProject() throws InvalidProjectNameException {
        Project project = Project.builder("Name").build();
        ProjectsItem item = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, item);

        vm.input().remove(result);

        removeProjectSuccess.assertValue(result);
        removeProjectSuccess.assertNoTerminalEvent();
        removeProjectError.assertNoValues();
        removeProjectError.assertNoTerminalEvent();
    }
}
