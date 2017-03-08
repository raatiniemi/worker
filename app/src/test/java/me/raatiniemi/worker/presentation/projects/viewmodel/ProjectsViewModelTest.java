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

import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProjectsViewModelTest {
    private TestSubscriber<List<ProjectsItem>> projects;

    private GetProjects getProjects;
    private GetProjectTimeSince getProjectTimeSince;
    private ProjectsViewModel.ViewModel vm;

    private List<Project> getProjects() throws InvalidProjectNameException {
        Project project = Project.builder("Name")
                .build();

        return Collections.singletonList(project);
    }

    @Before
    public void setUp() {
        projects = new TestSubscriber<>();

        getProjects = mock(GetProjects.class);
        getProjectTimeSince = mock(GetProjectTimeSince.class);
        vm = new ProjectsViewModel.ViewModel(getProjects, getProjectTimeSince);
    }

    @Test
    public void projects_withGetProjectsError() throws DomainException {
        when(getProjects.execute())
                .thenThrow(DomainException.class);

        vm.output.projects().subscribe(projects);

        projects.assertError(DomainException.class);
    }

    @Test
    public void projects_withGetProjectTimeSinceError() throws DomainException {
        when(getProjects.execute())
                .thenReturn(getProjects());
        when(getProjectTimeSince.execute(any(Project.class), eq(GetProjectTimeSince.MONTH)))
                .thenThrow(ClockOutBeforeClockInException.class);

        vm.output.projects().subscribe(projects);

        projects.assertValueCount(1);
        projects.assertCompleted();
    }

    @Test
    public void projects() throws DomainException {
        when(getProjects.execute())
                .thenReturn(getProjects());
        when(getProjectTimeSince.execute(any(Project.class), anyInt()))
                .thenReturn(Collections.emptyList());

        vm.output.projects().subscribe(projects);

        projects.assertValueCount(1);
        projects.assertCompleted();
        verify(getProjectTimeSince)
                .execute(any(Project.class), eq(GetProjectTimeSince.MONTH));
    }

    @Test
    public void projects_withoutProjects() throws DomainException {
        when(getProjects.execute())
                .thenReturn(Collections.emptyList());

        vm.output.projects().subscribe(projects);

        projects.assertValueCount(1);
        projects.assertCompleted();
    }
}
