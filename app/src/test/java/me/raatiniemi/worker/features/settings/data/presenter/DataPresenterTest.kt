/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.settings.data.presenter

import me.raatiniemi.worker.features.settings.data.model.Backup
import me.raatiniemi.worker.features.settings.data.model.BackupSuccessfulEvent
import me.raatiniemi.worker.features.settings.data.view.DataView
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.io.File

@RunWith(JUnit4::class)
class DataPresenterTest {
    private lateinit var eventBus: EventBus
    private lateinit var view: DataView
    private lateinit var presenter: DataPresenter

    @Before
    fun setUp() {
        eventBus = mock(EventBus::class.java)
        view = mock(DataView::class.java)
        presenter = DataPresenter(eventBus)
    }

    @Test
    fun attachView_registerEventBus() {
        presenter.attachView(view)

        verify<EventBus>(eventBus).register(presenter)
    }

    @Test
    fun detachView_unregisterEventBus() {
        presenter.detachView()

        verify<EventBus>(eventBus).unregister(presenter)
    }

    @Test
    fun onEventMainThread_successfulBackupEvent() {
        val backup = Backup(File("backup-file"))
        val event = BackupSuccessfulEvent(backup)
        presenter.attachView(view)

        presenter.onEventMainThread(event)

        verify<DataView>(view).setLatestBackup(backup)
    }

    @Test
    fun onEventMainThread_successfulBackupEventWithoutView() {
        val backup = Backup(File("backup-file"))
        val event = BackupSuccessfulEvent(backup)

        presenter.onEventMainThread(event)

        verify<DataView>(view, never()).setLatestBackup(backup)
    }
}
