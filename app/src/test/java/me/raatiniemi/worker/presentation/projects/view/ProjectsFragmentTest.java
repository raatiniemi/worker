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

import java.util.Collections;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.projects.viewmodel.ProjectsViewModel;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectsFragmentTest extends RobolectricTestCase {
    private final EventBus eventBus = new EventBus();

    private ProjectsAdapter adapter;
    private ProjectsFragment fragment;

    @Before
    public void setUp() throws Exception {
        TimeSummaryPreferences timeSummaryPreferences = mock(TimeSummaryPreferences.class);
        when(timeSummaryPreferences.getStartingPointForTimeSummary())
                .thenReturn(GetProjectTimeSince.MONTH);

        GetProjects getProjects = mock(GetProjects.class);
        when(getProjects.execute())
                .thenReturn(Collections.emptyList());

        GetProjectTimeSince getProjectTimeSince = mock(GetProjectTimeSince.class);

        ProjectsViewModel.ViewModel viewModel = new ProjectsViewModel.ViewModel(getProjects, getProjectTimeSince);
        adapter = mock(ProjectsAdapter.class);

        fragment = new ProjectsFragment();
        fragment.timeSummaryPreferences = timeSummaryPreferences;
        fragment.viewModel = viewModel;
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
        verify(adapter).add(eq(Collections.emptyList()));
    }

    @Test
    public void onEventMainThread_timeSummaryStartingPointChangeEvent() {
        eventBus.post(new TimeSummaryStartingPointChangeEvent());

        verify(adapter).clear();
        verify(adapter).add(eq(Collections.emptyList()));
    }
}
