package me.raatiniemi.worker.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Utilities for working with files.
 */
public class FileUtils {
    /**
     * Tag for logging.
     */
    private static final String TAG = "FileUtils";

    /**
     * Copy one file from one location to another. If the destination
     * file do not exists, it will be created.
     *
     * @param from The source file.
     * @param to The destination file.
     * @return Number of bytes copied between the locations.
     * @throws IOException
     */
    public static long copy(@NonNull File from, @NonNull File to) throws IOException {
        // Number of bytes copied between the locations.
        long bytes = -1;

        Log.d(TAG, "Copy file from " + from.getPath() + " to " + to.getParent());

        // Source and destination file channels, needs to be defined before the
        // try-block otherwise we won't be able to close them in finally.
        FileChannel source = null;
        FileChannel destination = null;

        try {
            // Open the read and write file channels for the source and
            // destination locations.
            source = new FileInputStream(from).getChannel();
            destination = new FileOutputStream(to).getChannel();

            // Begin copying the source file to the destination file.
            long size = source.size();
            bytes = destination.transferFrom(source, 0, size);

            // If the number of bytes copied and the size of the source file is
            // different, something is wrong.
            if (size != bytes) {
                Log.w(
                    TAG,
                    "Number of bytes copied (" + bytes + ") do not match " +
                        "the source file (" + size + ")"
                );
            } else {
                Log.d(TAG, bytes + " have been successfully copied");
            }
        } finally {
            // If the source file channel is open, it needs to be closed.
            if (null != source && source.isOpen()) {
                Log.d(TAG, "Close the source file channel");
                source.close();
            }

            // If the destination file channel is open, it needs to be closed.
            if (null != destination && destination.isOpen()) {
                Log.d(TAG, "Close the destination file channel");
                destination.close();
            }
        }

        return bytes;
    }
}
