package me.raatiniemi.worker.util;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Methods for working with the device external storage.
 */
public class ExternalStorage {
    /**
     * Tag for logging.
     */
    private static final String TAG = "ExternalStorage";

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
    public static File getDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), Worker.PACKAGE);
        if (!directory.exists() && !directory.mkdir()) {
            Log.w(TAG, "Unable to create non existing directory");
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
    public static File getDirectory(@NonNull String name) {
        File directory = getDirectory();
        if (null == directory) {
            Log.w(TAG, "Unable to retrieve the application directory");
            return null;
        }

        // Build the directory within the application directory.
        directory = new File(directory, name);
        if (!directory.exists() && !directory.mkdir()) {
            Log.w(TAG, "Unable to create directory");
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
        String name = Worker.STORAGE_BACKUP_DIRECTORY_PREFIX + (new Date()).getTime();
        return getDirectory(name);
    }
}
