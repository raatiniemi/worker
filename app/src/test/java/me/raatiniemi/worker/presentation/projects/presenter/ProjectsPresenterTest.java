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

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.RxSchedulerRule;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.view.ProjectsView;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
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

    private EventBus eventBus;
    private GetProjects getProjects;
    private GetProjectTimeSince getProjectTimeSince;
    private ClockActivityChange clockActivityChange;
    private RemoveProject removeProject;
    private ProjectsPresenter presenter;
    private ProjectsView view;

    @Before
    public void setUp() {
        TimeSummaryPreferences timeSummaryPreferences = mock(TimeSummaryPreferences.class);
        eventBus = mock(EventBus.class);
        getProjects = mock(GetProjects.class);
        getProjectTimeSince = mock(GetProjectTimeSince.class);
        clockActivityChange = mock(ClockActivityChange.class);
        removeProject = mock(RemoveProject.class);
        presenter = new ProjectsPresenter(
                timeSummaryPreferences,
                eventBus,
                getProjects,
                getProjectTimeSince,
                clockActivityChange,
                removeProject
        );
        view = mock(ProjectsView.class);
    }

    @Test
    public void attachView_registerEventBus() {
        presenter.attachView(view);

        verify(eventBus).register(presenter);
    }

    @Test
    public void detachView_unregisterEventBus() {
        presenter.detachView();

        verify(eventBus).unregister(presenter);
    }

    @Test
    public void beginRefreshingActiveProjects() throws DomainException {
        Project activeProject = mock(Project.class);
        when(activeProject.isActive()).thenReturn(true);
        List<ProjectsItem> projects = new ArrayList<>();
        projects.add(new ProjectsItem(new Project.Builder("Name").build()));
        projects.add(new ProjectsItem(activeProject));
        when(view.getProjects()).thenReturn(projects);
        presenter.attachView(view);

        presenter.beginRefreshingActiveProjects();
        rxSchedulersRule.advanceTimeTo(60, TimeUnit.SECONDS);
        presenter.stopRefreshingActiveProjects();

        List<Integer> positions = new ArrayList<>();
        positions.add(1);
        verify(view).refreshPositions(positions);
    }

    @Test
    public void refreshActiveProjects() throws DomainException {
        Project activeProject = mock(Project.class);
        when(activeProject.isActive()).thenReturn(true);
        List<ProjectsItem> projects = new ArrayList<>();
        projects.add(new ProjectsItem(new Project.Builder("Name").build()));
        projects.add(new ProjectsItem(activeProject));
        when(view.getProjects()).thenReturn(projects);
        presenter.attachView(view);

        presenter.refreshActiveProjects();

        List<Integer> positions = new ArrayList<>();
        positions.add(1);
        verify(view).refreshPositions(positions);
    }

    @Test
    public void refreshActiveProjects_withoutAttachedView() throws DomainException {
        Project activeProject = mock(Project.class);
        when(activeProject.isActive()).thenReturn(true);
        List<ProjectsItem> projects = new ArrayList<>();
        projects.add(new ProjectsItem(new Project.Builder("Name").build()));
        projects.add(new ProjectsItem(activeProject));
        when(view.getProjects()).thenReturn(projects);

        presenter.refreshActiveProjects();

        verify(view, never()).refreshPositions(anyList());
    }

    @Test
    public void getProjects() throws DomainException {
        List<Project> projects = new ArrayList<>();
        projects.add(
                new Project.Builder("Name")
                        .build()
        );
        when(getProjects.execute()).thenReturn(projects);
        when(getProjectTimeSince.execute(any(Project.class), anyInt()))
                .thenReturn(anyList());
        presenter.attachView(view);

        presenter.getProjects();

        verify(getProjectTimeSince).execute(any(Project.class), anyInt());
        verify(view).addProjects(anyList());
    }

    @Test
    public void getProjects_failureToGetRegisteredTime() throws DomainException {
        List<Project> projects = new ArrayList<>();
        projects.add(
                new Project.Builder("Name")
                        .build()
        );
        when(getProjects.execute()).thenReturn(projects);
        when(getProjectTimeSince.execute(any(Project.class), anyInt()))
                .thenThrow(new ClockOutBeforeClockInException());
        presenter.attachView(view);

        presenter.getProjects();

        verify(getProjectTimeSince).execute(any(Project.class), anyInt());
        verify(view).addProjects(anyList());
    }

    @Test
    public void getProjects_withoutAttachedView() throws DomainException {
        List<Project> projects = new ArrayList<>();
        when(getProjects.execute()).thenReturn(projects);

        presenter.getProjects();

        verify(view, never()).addProjects(anyList());
    }

    @Test
    public void getProjects_withError() throws DomainException {
        when(getProjects.execute()).thenThrow(new RuntimeException());
        presenter.attachView(view);

        presenter.getProjects();

        verify(getProjectTimeSince, never()).execute(any(Project.class), anyInt());
        verify(view).showGetProjectsErrorMessage();
    }

    @Test
    public void getProjects_withErrorAndWithoutAttachedView() throws DomainException {
        when(getProjects.execute()).thenThrow(new ClockOutBeforeClockInException());

        presenter.getProjects();

        verify(view, never()).showGetProjectsErrorMessage();
    }

    @Test
    public void deleteProject() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        List<ProjectsItem> projects = new ArrayList<>();
        projects.add(new ProjectsItem(project));
        when(view.getProjects()).thenReturn(projects);
        presenter.attachView(view);

        presenter.deleteProject(projectsItem);

        verify(view).deleteProjectAtPosition(0);
        verify(view).showDeleteProjectSuccessMessage();
    }

    @Test
    public void deleteProject_withError() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        List<ProjectsItem> projects = new ArrayList<>();
        projects.add(new ProjectsItem(project));
        when(view.getProjects()).thenReturn(projects);
        doThrow(new RuntimeException()).when(removeProject).execute(project);
        presenter.attachView(view);

        presenter.deleteProject(projectsItem);

        verify(view).deleteProjectAtPosition(0);
        verify(view).restoreProjectAtPreviousPosition(0, projectsItem);
        verify(view).showDeleteProjectErrorMessage();
    }

    @Test
    public void clockActivityChange_clockOut() throws DomainException {
        Project project = new Project.Builder("Name")
                .id(1L)
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenReturn(project);
        when(getProjectTimeSince.execute(any(Project.class), anyInt()))
                .thenReturn(anyList());
        presenter.attachView(view);

        presenter.clockActivityChange(projectsItem, new Date());

        verify(getProjectTimeSince).execute(any(Project.class), anyInt());
        verify(view).updateNotificationForProject(eq(projectsItem));
        verify(view).updateProject(projectsItem);
    }

    @Test
    public void clockActivityChange_withoutAttachedView() throws DomainException {
        Project project = new Project.Builder("Name")
                .id(1L)
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenReturn(project);

        presenter.clockActivityChange(projectsItem, new Date());

        verify(view, never()).updateNotificationForProject(any());
        verify(view, never()).updateProject(projectsItem);
    }

    @Test
    public void clockActivityChange_withClockInError() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());
        presenter.attachView(view);

        presenter.clockActivityChange(projectsItem, new Date());

        verify(view, never()).showClockOutErrorMessage();
        verify(view).showClockInErrorMessage();
    }

    @Test
    public void clockActivityChange_withClockOutError() throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(true);
        ProjectsItem projectsItem = new ProjectsItem(project);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());
        presenter.attachView(view);

        presenter.clockActivityChange(projectsItem, new Date());

        verify(view).showClockOutErrorMessage();
        verify(view, never()).showClockInErrorMessage();
    }

    @Test
    public void clockActivityChange_withErrorAndWithoutAttachedView() throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(true);
        ProjectsItem projectsItem = new ProjectsItem(project);
        when(clockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());

        presenter.clockActivityChange(projectsItem, new Date());

        verify(view, never()).showClockOutErrorMessage();
        verify(view, never()).showClockInErrorMessage();
    }

    @Test
    public void onEventMainThread_changeTimeSummaryStartingPoint() {
        presenter.attachView(view);

        presenter.onEventMainThread(new TimeSummaryStartingPointChangeEvent());

        verify(view).reloadProjects();
    }

    @Test
    public void onEventMainThread_changeTimeSummaryStartingPointWithoutAttachedView() {
        presenter.onEventMainThread(new TimeSummaryStartingPointChangeEvent());

        verify(view, never()).reloadProjects();
    }

    @Test
    public void onEventMainThread_ongoingNotification() {
        presenter.attachView(view);

        presenter.onEventMainThread(new OngoingNotificationActionEvent(1));

        verify(view).reloadProjects();
    }

    @Test
    public void onEventMainThread_ongoingNotificationWithoutAttachedView() {
        presenter.onEventMainThread(new OngoingNotificationActionEvent(1));

        verify(view, never()).reloadProjects();
    }
}
