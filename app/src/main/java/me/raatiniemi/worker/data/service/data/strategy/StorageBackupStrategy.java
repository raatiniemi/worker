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

package me.raatiniemi.worker.data.service.data.strategy;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.util.ExternalStorage;
import me.raatiniemi.worker.data.util.FileUtils;
import me.raatiniemi.worker.domain.interactor.BackupStrategy;
import me.raatiniemi.worker.presentation.settings.model.Backup;
import me.raatiniemi.worker.presentation.settings.model.BackupSuccessfulEvent;

import static me.raatiniemi.util.NullUtil.isNull;

/**
 * Backup strategy for storage device.
 */
public class StorageBackupStrategy implements BackupStrategy {
    /**
     * Application context.
     */
    private final Context context;

    /**
     * Event bus used for notification.
     */
    private final EventBus eventBus;

    /**
     * Constructor.
     *
     * @param context  Application context.
     * @param eventBus Event bus used for notification.
     */
    public StorageBackupStrategy(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void execute() {
        try {
            // Check that the external storage is writable.
            if (!ExternalStorage.isWritable()) {
                throw new IOException("External storage is not writable");
            }

            // Check that the backup directory is available.
            File directory = ExternalStorage.getBackupDirectory();
            if (isNull(directory)) {
                throw new FileNotFoundException("Directory for backup is not available");
            }

            // Retrieve the source and destination file locations.
            File from = context.getDatabasePath(Worker.DATABASE_NAME);
            File to = new File(directory, Worker.DATABASE_NAME);

            // Perform the file copy.
            FileUtils.copy(from, to);

            // Assemble and post the successful backup event.
            Backup backup = new Backup(directory);
            eventBus.post(new BackupSuccessfulEvent(backup));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
