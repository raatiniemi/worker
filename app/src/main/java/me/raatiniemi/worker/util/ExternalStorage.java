package me.raatiniemi.worker.util;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

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
        File directory = new File(Environment.getExternalStorageDirectory(), "worker");
        if (!directory.exists() && !directory.mkdir()) {
            Log.w(TAG, "Unable to create non existing directory");
            return null;
        }
        return directory;
    }

    /**
     * Get the backup directory on the external storage. If it do not exists,
     * it will be created.
     *
     * @param backupName Name of the backup directory.
     * @return Backup directory, or null if it can't be created.
     */
    @Nullable
    public static File getBackupDirectory(@NonNull String backupName) {
        File directory = getDirectory();
        if (null == directory) {
            Log.w(TAG, "Unable to retrieve the application directory");
            return null;
        }

        // Build the backup directory within the application directory.
        directory = new File(directory, backupName);
        if (!directory.exists() && !directory.mkdir()) {
            Log.w(TAG, "Unable to create backup directory");
            return null;
        }
        return directory;
    }
}
