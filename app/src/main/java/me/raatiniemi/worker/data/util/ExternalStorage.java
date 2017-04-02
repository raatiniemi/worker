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

package me.raatiniemi.worker.data.util;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.WorkerApplication;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

/**
 * Methods for working with the device external storage.
 */
public class ExternalStorage {
    private ExternalStorage() {
    }

    /**
     * Checks if the external storage is writable.
     *
     * @return True if external storage is writable, otherwise false.
     */
    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if the external storage is at least readable.
     *
     * @return True if external storage is readable, otherwise false.
     */
    public static boolean isReadable() {
        String state = Environment.getExternalStorageState();
        return isWritable() || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Get the application directory on the external storage. If it do not
     * exists, it will be created.
     *
     * @return Application directory, or null if it can't be created.
     */
    @Nullable
    private static File getDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), WorkerApplication.PACKAGE);
        if (!directory.exists() && !directory.mkdir()) {
            // TODO: Throw exception, avoiding checks for null everywhere.
            Timber.w("Unable to create non existing directory");
            return null;
        }
        return directory;
    }

    /**
     * Get directory within the application directory on the external storage.
     * If it do not exists, it will be created.
     *
     * @param name Name of the directory.
     * @return Directory within the application directory, or null if it can't be created.
     */
    @Nullable
    private static File getDirectory(@NonNull String name) {
        File directory = getDirectory();
        if (isNull(directory)) {
            // TODO: Throw exception, avoiding checks for null everywhere.
            Timber.w("Unable to retrieve the application directory");
            return null;
        }

        // Build the directory within the application directory.
        directory = new File(directory, name);
        if (!directory.exists() && !directory.mkdir()) {
            // TODO: Throw exception, avoiding checks for null everywhere.
            Timber.w("Unable to create directory");
            return null;
        }
        return directory;
    }

    /**
     * Get the backup directory on the external storage. If it do not exists,
     * it will be created.
     *
     * @return Backup directory, or null if it can't be created.
     */
    @Nullable
    public static File getBackupDirectory() {
        // Build the backup folder name with the current timestamp to prevent
        // running multiple backups against the same directory, which would
        // effectively override any previous backups.
        String name = WorkerApplication.STORAGE_BACKUP_DIRECTORY_PREFIX + (new Date()).getTime();
        return getDirectory(name);
    }

    /**
     * Get a list of backup directories from the application directory on the external storage.
     *
     * @return List of backup directories within the application directory.
     */
    @NonNull
    private static List<File> getBackupDirectories() {
        File[] directories = {};

        File directory = getDirectory();
        if (nonNull(directory)) {
            directories = directory.listFiles(file -> file.isDirectory() &&
                    file.getName().matches(WorkerApplication.STORAGE_BACKUP_DIRECTORY_PATTERN));
        }

        return new ArrayList<>(Arrays.asList(directories));
    }

    /**
     * Get the last created backup directory.
     *
     * @return Last created backup directory, or null if none can be found.
     */
    @Nullable
    public static File getLatestBackupDirectory() {
        List<File> directories = ExternalStorage.getBackupDirectories();
        if (directories.isEmpty()) {
            return null;
        }

        // Sort the directories and reverse the order, i.e. the last created
        // first since we are looking for the latest directory.
        Collections.sort(directories);
        Collections.reverse(directories);

        // Return the first directory entry.
        return directories.get(0);
    }
}
