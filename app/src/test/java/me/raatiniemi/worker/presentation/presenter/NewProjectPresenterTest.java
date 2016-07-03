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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.presentation.view.fragment.NewProjectFragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewProjectPresenterTest {
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
}
