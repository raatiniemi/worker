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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.presentation.model.backup.Backup;
import me.raatiniemi.worker.presentation.model.backup.BackupSuccessfulEvent;
import me.raatiniemi.worker.presentation.view.SettingsView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsPresenterTest {
    @Test
    public void attachView_registerEventBus() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);
        SettingsView view = mock(SettingsView.class);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.attachView(view);

        verify(eventBus, times(1)).register(presenter);
    }

    @Test
    public void detachView_unregisterEventBus() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.detachView();

        verify(eventBus, times(1)).unregister(presenter);
    }

    @Test
    public void onEventMainThread_successfulBackupEvent() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);
        SettingsView view = mock(SettingsView.class);

        Backup backup = mock(Backup.class);
        BackupSuccessfulEvent event = mock(BackupSuccessfulEvent.class);
        when(event.getBackup()).thenReturn(backup);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.attachView(view);
        presenter.onEventMainThread(event);

        verify(view, times(1)).setLatestBackup(backup);
    }

    @Test
    public void onEventMainThread_successfulBackupEventWithoutView() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);
        SettingsView view = mock(SettingsView.class);

        Backup backup = mock(Backup.class);
        BackupSuccessfulEvent event = mock(BackupSuccessfulEvent.class);
        when(event.getBackup()).thenReturn(backup);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.onEventMainThread(event);

        verify(view, never()).setLatestBackup(backup);
    }
}
