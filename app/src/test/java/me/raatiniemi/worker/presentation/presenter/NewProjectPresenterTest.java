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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewProjectPresenterTest {
    private Context mContext;
    private NewProjectFragment mFragment;
    private CreateProject mCreateProject;

    @Before
    public void setUp() {
        mContext = mock(Context.class);
        mFragment = mock(NewProjectFragment.class);
        mCreateProject = mock(CreateProject.class);
    }

    @Test
    public void createNewProject_withInvalidName() {
        NewProjectPresenter presenter = new NewProjectPresenter(mContext, mCreateProject);
        presenter.attachView(mFragment);

        presenter.createNewProject("");

        // Verify that the display invalid project name error have been invoked.
        verify(mFragment, times(1)).showInvalidNameError();
    }

    @Test
    public void createNewProject_withInvalidNameWithoutAttachedView() {
        NewProjectPresenter presenter = new NewProjectPresenter(mContext, mCreateProject);

        // Failed test will throw `NullPointerException` when attempting to
        // invoke the display error message on the unattached view.
        presenter.createNewProject("");
    }
}
