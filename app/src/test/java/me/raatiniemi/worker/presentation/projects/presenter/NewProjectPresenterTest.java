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

import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.RxSchedulerRule;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.view.NewProjectFragment;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class NewProjectPresenterTest {
    @Rule
    public final RxSchedulerRule rxSchedulersRule = new RxSchedulerRule();

    private CreateProject createProject;
    private NewProjectPresenter presenter;
    private NewProjectFragment view;

    @Before
    public void setUp() {
        createProject = mock(CreateProject.class);
        presenter = new NewProjectPresenter(
                mock(Context.class),
                createProject
        );
        view = mock(NewProjectFragment.class);
    }

    @Test
    public void createNewProject_withInvalidName() {
        presenter.attachView(view);

        presenter.createNewProject("");

        verify(view).showInvalidNameError();
    }

    @Test
    public void createNewProject_withInvalidNameWithoutAttachedView() {
        presenter.createNewProject("");

        verify(view, never()).showInvalidNameError();
    }

    @Test
    public void createNewProject() throws DomainException {
        presenter.attachView(view);

        presenter.createNewProject("Name");

        verify(view).createProjectSuccessful(any(Project.class));
    }

    @Test
    public void createNewProject_withoutAttachedView() throws DomainException {
        presenter.createNewProject("Name");

        verify(view, never()).createProjectSuccessful(any(Project.class));
    }

    @Test
    public void createNewProject_withDuplicateName() throws DomainException {
        when(createProject.execute(any(Project.class)))
                .thenThrow(new ProjectAlreadyExistsException(""));

        presenter.attachView(view);
        presenter.createNewProject("Name");

        verify(view).showDuplicateNameError();
        verify(view, never()).showUnknownError();
    }

    @Test
    public void createNewProject_withUnknownError() throws DomainException {
        when(createProject.execute(any(Project.class)))
                .thenThrow(new RuntimeException());

        presenter.attachView(view);
        presenter.createNewProject("Name");

        verify(view, never()).showDuplicateNameError();
        verify(view).showUnknownError();
    }

    @Test
    public void createNewProject_withUnknownErrorAndWithoutAttachedView() throws DomainException {
        when(createProject.execute(any(Project.class)))
                .thenThrow(new RuntimeException());

        presenter.createNewProject("Name");

        verify(view, never()).showDuplicateNameError();
        verify(view, never()).showUnknownError();
    }
}
