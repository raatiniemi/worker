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

import me.raatiniemi.worker.RxSchedulerRule;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.view.NewProjectView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NewProjectPresenterTest {
    @Rule
    public final RxSchedulerRule rxSchedulersRule = new RxSchedulerRule();

    private CreateProject createProject;
    private NewProjectPresenter presenter;
    private NewProjectView view;

    private Project buildProject() {
        try {
            return new Project.Builder("Name").build();
        } catch (InvalidProjectNameException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Before
    public void setUp() {
        createProject = mock(CreateProject.class);
        presenter = new NewProjectPresenter(createProject);
        view = mock(NewProjectView.class);
    }

    @Test
    public void createNewProject_withInvalidName() {
        when(view.getProjectName()).thenReturn("");
        presenter.attachView(view);

        presenter.createNewProject();

        verify(view).showInvalidNameError();
    }

    @Test
    public void createNewProject_withInvalidNameWithoutAttachedView() {
        when(view.getProjectName()).thenReturn("");

        presenter.createNewProject();

        verify(view, never()).showInvalidNameError();
    }

    @Test
    public void createNewProject() throws DomainException {
        when(view.getProjectName()).thenReturn("Name");
        when(createProject.execute(any(Project.class)))
                .thenReturn(buildProject());

        presenter.attachView(view);
        presenter.createNewProject();

        verify(view).createProjectSuccessful(any(Project.class));
    }

    @Test
    public void createNewProject_withoutAttachedView() throws DomainException {
        when(view.getProjectName()).thenReturn("Name");

        presenter.createNewProject();

        verify(view, never()).createProjectSuccessful(any(Project.class));
    }

    @Test
    public void createNewProject_withDuplicateName() throws DomainException {
        when(view.getProjectName()).thenReturn("Name");
        when(createProject.execute(any(Project.class)))
                .thenThrow(new ProjectAlreadyExistsException(""));

        presenter.attachView(view);
        presenter.createNewProject();

        verify(view).showDuplicateNameError();
        verify(view, never()).showUnknownError();
    }

    @Test
    public void createNewProject_withUnknownError() throws DomainException {
        when(view.getProjectName()).thenReturn("Name");
        when(createProject.execute(any(Project.class)))
                .thenThrow(new RuntimeException());

        presenter.attachView(view);
        presenter.createNewProject();

        verify(view, never()).showDuplicateNameError();
        verify(view).showUnknownError();
    }

    @Test
    public void createNewProject_withUnknownErrorAndWithoutAttachedView() throws DomainException {
        when(view.getProjectName()).thenReturn("Name");
        when(createProject.execute(any(Project.class)))
                .thenThrow(new RuntimeException());

        presenter.createNewProject();

        verify(view, never()).showDuplicateNameError();
        verify(view, never()).showUnknownError();
    }
}
