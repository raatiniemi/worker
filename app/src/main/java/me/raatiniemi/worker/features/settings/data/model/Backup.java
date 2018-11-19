/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.features.settings.data.model;

import java.io.File;
import java.util.Date;

import androidx.annotation.Nullable;
import me.raatiniemi.worker.WorkerApplication;

import static me.raatiniemi.worker.util.NullUtil.isNull;

public class Backup {
    /**
     * Backup directory.
     */
    private final File directory;

    /**
     * Constructor.
     *
     * @param directory Backup directory.
     */
    public Backup(@Nullable File directory) {
        this.directory = directory;
    }

    /**
     * Get the timestamp of the backup.
     *
     * @return Timestamp of the backup, or null if backup is not available.
     */
    @Nullable
    private Long getTimestamp() {
        if (isNull(directory)) {
            return null;
        }

        String timestamp = directory.getName().replaceFirst(
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
