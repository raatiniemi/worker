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

package me.raatiniemi.worker.presentation.presenter;

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
import me.raatiniemi.worker.presentation.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.model.backup.Backup;
import me.raatiniemi.worker.presentation.model.backup.BackupSuccessfulEvent;
import me.raatiniemi.worker.presentation.view.SettingsView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsPresenterTest {
    private EventBus mEventBus;
    private SettingsPresenter mPresenter;
    private SettingsView mView;

    @Before
    public void setUp() throws Exception {
        Context context = mock(Context.class);
        mEventBus = mock(EventBus.class);
        mPresenter = new SettingsPresenter(context, mEventBus);
        mView = mock(SettingsView.class);
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

    @Test
    public void onEventMainThread_successfulBackupEvent() {
        Backup backup = new Backup(new File("backup-file"));
        BackupSuccessfulEvent event = new BackupSuccessfulEvent(backup);
        mPresenter.attachView(mView);

        mPresenter.onEventMainThread(event);

        verify(mView).setLatestBackup(backup);
    }

    @Test
    public void onEventMainThread_successfulBackupEventWithoutView() {
        Backup backup = new Backup(new File("backup-file"));
        BackupSuccessfulEvent event = new BackupSuccessfulEvent(backup);

        mPresenter.onEventMainThread(event);

        verify(mView, never()).setLatestBackup(backup);
    }

    @Test
    public void changeTimeSummaryStartingPoint_withMonth() {
        mPresenter.attachView(mView);

        mPresenter.changeTimeSummaryStartingPoint(
                GetProjectTimeSince.sMonth
        );

        verify(mEventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(mView).showChangeTimeSummaryStartingPointSuccessMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withWeek() {
        mPresenter.attachView(mView);

        mPresenter.changeTimeSummaryStartingPoint(
                GetProjectTimeSince.sWeek
        );

        verify(mEventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(mView).showChangeTimeSummaryStartingPointSuccessMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withoutAttachedView() {
        mPresenter.changeTimeSummaryStartingPoint(
                GetProjectTimeSince.sMonth
        );

        verify(mEventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(mView, never()).showChangeTimeSummaryStartingPointSuccessMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_invalidStartingPoint() {
        mPresenter.attachView(mView);

        mPresenter.changeTimeSummaryStartingPoint(0);

        verify(mView).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_invalidStartingPointWithoutAttachedView() {
        mPresenter.changeTimeSummaryStartingPoint(0);

        verify(mView, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }
}
