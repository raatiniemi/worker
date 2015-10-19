/*
 * Copyright (C) 2015 Worker Project
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
     * @param to   The destination file.
     * @return Number of bytes copied between the locations.
     * @throws IOException
     */
    public static long copy(@NonNull File from, @NonNull File to) throws IOException {
        Log.d(TAG, "Copy file from " + from.getPath() + " to " + to.getParent());

        // Number of bytes copied between the locations.
        long bytes;

        try (
                // Open the read and write file channels for the source and
                // destination locations.
                FileChannel source = new FileInputStream(from).getChannel();
                FileChannel destination = new FileOutputStream(to).getChannel()
        ) {
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
        }

        return bytes;
    }
}
