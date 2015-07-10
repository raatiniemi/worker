package me.raatiniemi.worker.util;

import android.os.Environment;

/**
 * Methods for working with the device external storage.
 */
public class ExternalStorage {
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
}
