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

package me.raatiniemi.worker.presentation.projects.view;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.projects.presenter.ProjectsPresenter;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectsFragmentTest extends RobolectricTestCase {
    private final EventBus eventBus = new EventBus();

    private ProjectsPresenter presenter;
    private ProjectsAdapter adapter;
    private ProjectsFragment fragment;

    @Before
    public void setUp() {
        presenter = mock(ProjectsPresenter.class);
        adapter = mock(ProjectsAdapter.class);

        fragment = new ProjectsFragment();
        fragment.presenter = presenter;
        fragment.adapter = adapter;

        eventBus.register(fragment);
    }

    @After
    public void tearDown() {
        eventBus.unregister(fragment);
    }

    @Test
    public void onEventMainThread_ongoingNotificationActionEvent() {
        eventBus.post(new OngoingNotificationActionEvent(1L));

        verify(adapter).clear();
        verify(presenter).getProjects();
    }

    @Test
    public void onEventMainThread_timeSummaryStartingPointChangeEvent() {
        eventBus.post(new TimeSummaryStartingPointChangeEvent());

        verify(adapter).clear();
        verify(presenter).getProjects();
    }
}
