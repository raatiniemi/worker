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

package me.raatiniemi.worker.features.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

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
    private RemoveProjectViewModel vm;

    private TestSubscriber<ProjectsItemAdapterResult> removeProjectSuccess;
    private TestSubscriber<ProjectsItemAdapterResult> removeProjectError;

    @Before
    public void setUp() {
        removeProject = mock(RemoveProject.class);
        vm = new RemoveProjectViewModel(removeProject);

        removeProjectSuccess = new TestSubscriber<>();
        vm.removeProjectSuccess().subscribe(removeProjectSuccess);

        removeProjectError = new TestSubscriber<>();
        vm.removeProjectError().subscribe(removeProjectError);
    }

    @Test
    public void deleteProject_withError() {
        Project project = Project.from("Name");
        ProjectsItem item = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, item);
        doThrow(RuntimeException.class)
                .when(removeProject).execute(any());

        vm.remove(result);

        removeProjectSuccess.assertNoValues();
        removeProjectSuccess.assertNoTerminalEvent();
        removeProjectError.assertValue(result);
    }

    @Test
    public void deleteProject() {
        Project project = Project.from("Name");
        ProjectsItem item = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, item);

        vm.remove(result);

        removeProjectSuccess.assertValue(result);
        removeProjectSuccess.assertNoTerminalEvent();
        removeProjectError.assertNoValues();
        removeProjectError.assertNoTerminalEvent();
    }
}
