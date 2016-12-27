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

package me.raatiniemi.worker.presentation.settings.presenter;

import android.os.Build;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.settings.view.ProjectView;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ProjectPresenterTest {
    private EventBus eventBus;
    private ProjectPresenter presenter;
    private ProjectView view;

    @Before
    public void setUp() throws Exception {
        TimeSummaryPreferences preferences = mock(TimeSummaryPreferences.class);
        when(preferences.getStartingPointForTimeSummary())
                .thenReturn(GetProjectTimeSince.MONTH);
        eventBus = mock(EventBus.class);
        presenter = new ProjectPresenter(preferences, eventBus);
        view = mock(ProjectView.class);
    }

    @Test
    public void changeTimeSummaryStartingPoint_withWeek() {
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(
                GetProjectTimeSince.WEEK
        );

        verify(eventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view).showChangeTimeSummaryStartingPointToWeekSuccessMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withPreviousValue() {
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(
                GetProjectTimeSince.MONTH
        );

        verify(eventBus, never()).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withoutAttachedView() {
        presenter.changeTimeSummaryStartingPoint(
                GetProjectTimeSince.WEEK
        );

        verify(eventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_invalidStartingPoint() {
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(0);

        verify(view).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_invalidStartingPointWithoutAttachedView() {
        presenter.changeTimeSummaryStartingPoint(0);

        verify(view, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }
}
