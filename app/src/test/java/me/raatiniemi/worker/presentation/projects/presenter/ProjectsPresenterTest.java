/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.projects.presenter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import me.raatiniemi.worker.RxSchedulerRule;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItemAdapterResult;
import me.raatiniemi.worker.presentation.projects.view.ProjectsView;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProjectsPresenterTest {
    @Rule
    public final RxSchedulerRule rxSchedulersRule = new RxSchedulerRule();

    private GetProjectTimeSince getProjectTimeSince;
    private ClockActivityChange clockActivityChange;
    private RemoveProject removeProject;
    private ProjectsPresenter presenter;
    private ProjectsView view;

    @Before
    public void setUp() {
        TimeSummaryPreferences timeSummaryPreferences = mock(TimeSummaryPreferences.class);
        getProjectTimeSince = mock(GetProjectTimeSince.class);
        clockActivityChange = mock(ClockActivityChange.class);
        removeProject = mock(RemoveProject.class);
        presenter = new ProjectsPresenter(
                timeSummaryPreferences,
                getProjectTimeSince,
                clockActivityChange,
                removeProject
        );
        view = mock(ProjectsView.class);
    }

    @Test
    public void deleteProject() throws DomainException {
        Project project = Project.builder("Name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        presenter.attachView(view);

        presenter.deleteProject(result);

        verify(view).deleteProjectAtPosition(eq(0));
        verify(view).showDeleteProjectSuccessMessage();
    }

    @Test
    public void deleteProject_withError() throws DomainException {
        Project project = Project.builder("Name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        doThrow(new RuntimeException()).when(removeProject).execute(project);
        presenter.attachView(view);

        presenter.deleteProject(result);

        verify(view).deleteProjectAtPosition(eq(0));
        verify(view).restoreProjectAtPreviousPosition(eq(0), eq(projectsItem));
        verify(view).showDeleteProjectErrorMessage();
    }

    @Test
    public void clockActivityChange_clockOut() throws DomainException {
        Project project = Project.builder("Name")
                .id(1L)
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenReturn(project);
        when(getProjectTimeSince.execute(any(Project.class), anyInt()))
                .thenReturn(anyList());
        presenter.attachView(view);

        presenter.clockActivityChange(result, new Date());

        verify(getProjectTimeSince).execute(any(Project.class), anyInt());
        verify(view).updateNotificationForProject(eq(projectsItem));
        verify(view).updateProject(eq(0), eq(projectsItem));
    }

    @Test
    public void clockActivityChange_withoutAttachedView() throws DomainException {
        Project project = Project.builder("Name")
                .id(1L)
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenReturn(project);

        presenter.clockActivityChange(result, new Date());

        verify(view, never()).updateNotificationForProject(any());
        verify(view, never()).updateProject(anyInt(), any());
    }

    @Test
    public void clockActivityChange_withClockInError() throws DomainException {
        Project project = Project.builder("Name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());
        presenter.attachView(view);

        presenter.clockActivityChange(result, new Date());

        verify(view, never()).showClockOutErrorMessage();
        verify(view).showClockInErrorMessage();
    }

    @Test
    public void clockActivityChange_withClockOutError() throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(true);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());
        presenter.attachView(view);

        presenter.clockActivityChange(result, new Date());

        verify(view).showClockOutErrorMessage();
        verify(view, never()).showClockInErrorMessage();
    }

    @Test
    public void clockActivityChange_withErrorAndWithoutAttachedView() throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(true);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());

        presenter.clockActivityChange(result, new Date());

        verify(view, never()).showClockOutErrorMessage();
        verify(view, never()).showClockInErrorMessage();
    }
}
