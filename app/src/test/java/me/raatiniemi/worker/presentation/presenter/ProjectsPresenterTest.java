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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.presentation.view.ProjectsView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectsPresenterTest {
    private EventBus mEventBus;
    private ProjectsPresenter mPresenter;
    private ProjectsView mView;

    @Before
    public void setUp() {
        mEventBus = mock(EventBus.class);
        GetProjects getProjects = mock(GetProjects.class);
        ClockActivityChange clockActivityChange = mock(ClockActivityChange.class);
        RemoveProject removeProject = mock(RemoveProject.class);
        mPresenter = new ProjectsPresenter(
                mock(Context.class),
                mEventBus,
                getProjects,
                clockActivityChange,
                removeProject
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
}
