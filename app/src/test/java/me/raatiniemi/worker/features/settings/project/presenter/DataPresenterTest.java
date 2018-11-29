/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.settings.project.presenter;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import me.raatiniemi.worker.features.settings.data.model.Backup;
import me.raatiniemi.worker.features.settings.data.model.BackupSuccessfulEvent;
import me.raatiniemi.worker.features.settings.data.presenter.DataPresenter;
import me.raatiniemi.worker.features.settings.data.view.DataView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class DataPresenterTest {
    private DataPresenter presenter;
    private DataView view;
    private EventBus eventBus;

    @Before
    public void setUp() {
        eventBus = mock(EventBus.class);
        presenter = new DataPresenter(eventBus);
        view = mock(DataView.class);
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
}
