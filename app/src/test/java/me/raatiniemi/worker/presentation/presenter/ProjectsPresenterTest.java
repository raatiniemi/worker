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

package me.raatiniemi.worker.presentation.presenter;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.RxSchedulerRule;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.model.ProjectsModel;
import me.raatiniemi.worker.presentation.view.ProjectsView;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectsPresenterTest {
    @Rule
    public final RxSchedulerRule mRxSchedulersRule = new RxSchedulerRule();

    private EventBus mEventBus;
    private GetProjects mGetProjects;
    private RemoveProject mRemoveProject;
    private ProjectsPresenter mPresenter;
    private ProjectsView mView;

    @Before
    public void setUp() {
        mEventBus = mock(EventBus.class);
        mGetProjects = mock(GetProjects.class);
        ClockActivityChange clockActivityChange = mock(ClockActivityChange.class);
        mRemoveProject = mock(RemoveProject.class);
        mPresenter = new ProjectsPresenter(
                mock(Context.class),
                mEventBus,
                mGetProjects,
                clockActivityChange,
                mRemoveProject
        );
        mView = mock(ProjectsView.class);
    }

    @Test
    public void attachView_registerEventBus() {
        mPresenter.attachView(mView);

        verify(mEventBus).register(mPresenter);
    }

    @Test
    public void detachView_unregisterEventBus() {
        mPresenter.detachView();

        verify(mEventBus).unregister(mPresenter);
    }

    @Test
    public void getProjects() throws DomainException {
        List<Project> projects = new ArrayList<>();
        projects.add(
                new Project.Builder("Name")
                        .build()
        );
        when(mGetProjects.execute()).thenReturn(projects);
        mPresenter.attachView(mView);

        mPresenter.getProjects();

        verify(mView).addProjects(anyListOf(ProjectsModel.class));
    }

    @Test
    public void getProjects_withoutAttachedView() throws DomainException {
        List<Project> projects = new ArrayList<>();
        when(mGetProjects.execute()).thenReturn(projects);

        mPresenter.getProjects();

        verify(mView, never()).addProjects(anyListOf(ProjectsModel.class));
    }

    @Test
    public void getProjects_withError() throws DomainException {
        when(mGetProjects.execute()).thenThrow(new RuntimeException());
        mPresenter.attachView(mView);

        mPresenter.getProjects();

        verify(mView).showGetProjectsErrorMessage();
    }

    @Test
    public void getProjects_withErrorAndWithoutAttachedView() throws DomainException {
        when(mGetProjects.execute()).thenThrow(new ClockOutBeforeClockInException());

        mPresenter.getProjects();

        verify(mView, never()).showGetProjectsErrorMessage();
    }

    @Test
    public void deleteProject() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        when(mView.getProjects()).thenReturn(projects);
        mPresenter.attachView(mView);

        mPresenter.deleteProject(project);

        verify(mView).deleteProjectAtPosition(0);
        verify(mView).showDeleteProjectSuccessMessage();
    }

    @Test
    public void deleteProject_withError() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        when(mView.getProjects()).thenReturn(projects);
        doThrow(new RuntimeException()).when(mRemoveProject).execute(project);
        mPresenter.attachView(mView);

        mPresenter.deleteProject(project);

        verify(mView).deleteProjectAtPosition(0);
        verify(mView).restoreProjectAtPreviousPosition(0, project);
        verify(mView).showDeleteProjectErrorMessage();
    }
}
