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
import me.raatiniemi.worker.presentation.view.fragment.NewProjectFragment;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewProjectPresenterTest {
    @Rule
    public final RxSchedulerRule mRxSchedulersRule = new RxSchedulerRule();

    private CreateProject mCreateProject;
    private NewProjectPresenter mPresenter;
    private NewProjectFragment mView;

    @Before
    public void setUp() {
        mCreateProject = mock(CreateProject.class);
        mPresenter = new NewProjectPresenter(
                mock(Context.class),
                mCreateProject
        );
        mView = mock(NewProjectFragment.class);
    }

    @Test
    public void createNewProject_withInvalidName() {
        mPresenter.attachView(mView);

        mPresenter.createNewProject("");

        verify(mView).showInvalidNameError();
    }

    @Test
    public void createNewProject_withInvalidNameWithoutAttachedView() {
        mPresenter.createNewProject("");

        verify(mView, never()).showInvalidNameError();
    }

    @Test
    public void createNewProject() throws DomainException {
        mPresenter.attachView(mView);

        mPresenter.createNewProject("Name");

        verify(mView).createProjectSuccessful(any(Project.class));
    }

    @Test
    public void createNewProject_withoutAttachedView() throws DomainException {
        mPresenter.createNewProject("Name");

        verify(mView, never()).createProjectSuccessful(any(Project.class));
    }

    @Test
    public void createNewProject_withDuplicateName() throws DomainException {
        when(mCreateProject.execute(any(Project.class)))
                .thenThrow(new ProjectAlreadyExistsException(""));

        mPresenter.attachView(mView);
        mPresenter.createNewProject("Name");

        verify(mView).showDuplicateNameError();
        verify(mView, never()).showUnknownError();
    }

    @Test
    public void createNewProject_withUnknownError() throws DomainException {
        when(mCreateProject.execute(any(Project.class)))
                .thenThrow(new RuntimeException());

        mPresenter.attachView(mView);
        mPresenter.createNewProject("Name");

        verify(mView, never()).showDuplicateNameError();
        verify(mView).showUnknownError();
    }

    @Test
    public void createNewProject_withUnknownErrorAndWithoutAttachedView() throws DomainException {
        when(mCreateProject.execute(any(Project.class)))
                .thenThrow(new RuntimeException());

        mPresenter.createNewProject("Name");

        verify(mView, never()).showDuplicateNameError();
        verify(mView, never()).showUnknownError();
    }
}
