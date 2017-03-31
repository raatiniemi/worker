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

package me.raatiniemi.worker.data.service.data.strategy;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.service.data.strategy.exception.RestoreException;
import me.raatiniemi.worker.data.util.ExternalStorage;
import me.raatiniemi.worker.data.util.FileUtils;
import me.raatiniemi.worker.domain.interactor.RestoreStrategy;

import static me.raatiniemi.util.NullUtil.isNull;

/**
 * Restoration strategy for storage device.
 */
public class StorageRestoreStrategy implements RestoreStrategy {
    /**
     * Application context.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param context Application context.
     */
    public StorageRestoreStrategy(Context context) {
        this.context = context;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void execute() {
        try {
            // Check that the external storage is readable.
            if (!ExternalStorage.isReadable()) {
                throw new IOException("External storage is not readable");
            }

            // Check that we have backup to restore from.
            File directory = ExternalStorage.getLatestBackupDirectory();
            if (isNull(directory)) {
                throw new FileNotFoundException("Unable to find backup from which to restore");
            }

            // Retrieve the source and destination file locations.
            File from = new File(directory, Worker.DATABASE_NAME);
            File to = context.getDatabasePath(Worker.DATABASE_NAME);

            // Perform the file copy.
            FileUtils.copy(from, to);
        } catch (Exception e) {
            throw new RestoreException(e);
        }
    }
}
