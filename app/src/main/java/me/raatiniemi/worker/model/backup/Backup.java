package me.raatiniemi.worker.model.backup;

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
