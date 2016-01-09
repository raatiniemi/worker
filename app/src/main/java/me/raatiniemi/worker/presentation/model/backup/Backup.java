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

package me.raatiniemi.worker.presentation.model.backup;

import android.support.annotation.Nullable;

import java.io.File;
import java.util.Date;

import me.raatiniemi.worker.util.Worker;

public class Backup {
    /**
     * Backup directory.
     */
    private File mBackup;

    /**
     * Constructor.
     *
     * @param backup Backup directory.
     */
    public Backup(@Nullable File backup) {
        mBackup = backup;
    }

    /**
     * Constructor, without arguments.
     */
    public Backup() {
    }

    /**
     * Get the backup directory.
     *
     * @return Backup directory, or null if none have been supplied.
     */
    @Nullable
    public File getBackup() {
        return mBackup;
    }

    /**
     * Get the timestamp of the backup.
     *
     * @return Timestamp of the backup, or null if backup is not available.
     */
    @Nullable
    public Long getTimestamp() {
        if (null == getBackup()) {
            return null;
        }

        String timestamp = getBackup().getName().replaceFirst(
                Worker.STORAGE_BACKUP_DIRECTORY_PATTERN,
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
        if (null == timestamp) {
            return null;
        }

        return new Date(timestamp);
    }
}
