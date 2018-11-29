/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * Utilities for working with files.
 */
public class FileUtils {
    private FileUtils() {
    }

    /**
     * Copy one file from one location to another. If the destination
     * file do not exists, it will be created.
     *
     * @param from The source file.
     * @param to   The destination file.
     * @return Number of bytes copied between the locations.
     * @throws IOException If file copy is unsuccessful.
     */
    public static long copy(@NonNull File from, @NonNull File to) throws IOException {
        Timber.d("Copy file from %s to %s", from.getPath(), to.getParent());

        try (
                FileInputStream sourceStream = new FileInputStream(from);
                FileOutputStream destinationStream = new FileOutputStream(to)
        ) {
            // Open the read and write file channels for the source and
            // destination locations.
            FileChannel source = sourceStream.getChannel();
            FileChannel destination = destinationStream.getChannel();

            // Begin copying the source file to the destination file.
            long size = source.size();
            long bytes = destination.transferFrom(source, 0, size);

            // If the number of bytes copied and the size of the source file is
            // different, something is wrong.
            if (size != bytes) {
                throw new IOException(
                        "Number of bytes copied (" + bytes + ") do not match " +
                                "the source file (" + size + ")"
                );
            }

            Timber.d("%d bytes have been successfully copied", bytes);
            return bytes;
        }
    }
}
