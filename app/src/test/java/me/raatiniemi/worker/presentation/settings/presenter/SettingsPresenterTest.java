/*
 * Copyright (C) 2015-2016 Worker Project
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

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.presentation.settings.model.Backup;
import me.raatiniemi.worker.presentation.settings.model.BackupSuccessfulEvent;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.settings.view.SettingsView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsPresenterTest {
    private EventBus eventBus;
    private SettingsPresenter presenter;
    private SettingsView view;

    @Before
    public void setUp() throws Exception {
        Context context = mock(Context.class);
        eventBus = mock(EventBus.class);
        presenter = new SettingsPresenter(context, eventBus);
        view = mock(SettingsView.class);
    }

    @Test
    public void attachView_registerEventBus() {
        presenter.attachView(view);

        verify(eventBus).register(presenter);
    }

    @Test
    public void detachView_unregisterEventBus() {
        presenter.detachView();

        verify(eventBus).unregister(presenter);
    }

    @Test
    public void onEventMainThread_successfulBackupEvent() {
        Backup backup = new Backup(new File("backup-file"));
        BackupSuccessfulEvent event = new BackupSuccessfulEvent(backup);
        presenter.attachView(view);

        presenter.onEventMainThread(event);

        verify(view).setLatestBackup(backup);
    }

    @Test
    public void onEventMainThread_successfulBackupEventWithoutView() {
        Backup backup = new Backup(new File("backup-file"));
        BackupSuccessfulEvent event = new BackupSuccessfulEvent(backup);

        presenter.onEventMainThread(event);

        verify(view, never()).setLatestBackup(backup);
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
