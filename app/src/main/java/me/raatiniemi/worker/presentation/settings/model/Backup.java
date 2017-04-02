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

package me.raatiniemi.worker.presentation.settings.model;

import android.support.annotation.Nullable;

import java.io.File;
import java.util.Date;

import me.raatiniemi.worker.WorkerApplication;

import static me.raatiniemi.worker.util.NullUtil.isNull;

public class Backup {
    /**
     * Backup directory.
     */
    private final File backup;

    /**
     * Constructor.
     *
     * @param backup Backup directory.
     */
    public Backup(@Nullable File backup) {
        this.backup = backup;
    }

    /**
     * Get the backup directory.
     *
     * @return Backup directory, or null if none have been supplied.
     */
    @Nullable
    private File getBackup() {
        return backup;
    }

    /**
     * Get the timestamp of the backup.
     *
     * @return Timestamp of the backup, or null if backup is not available.
     */
    @Nullable
    private Long getTimestamp() {
        if (isNull(getBackup())) {
            return null;
        }

        String timestamp = getBackup().getName().replaceFirst(
                WorkerApplication.STORAGE_BACKUP_DIRECTORY_PATTERN,
                "$1"
        );

        return Long.valueOf(timestamp);
    }

    /**
     * Get the date for the backup.
     *
     * @return Date for the backup, or null if backup is not available.
     */
    @Nullable
    public Date getDate() {
        Long timestamp = getTimestamp();
        if (isNull(timestamp)) {
            return null;
        }

        return new Date(timestamp);
    }
}
