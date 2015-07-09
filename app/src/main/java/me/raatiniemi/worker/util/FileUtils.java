package me.raatiniemi.worker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static long copy(File from, File to) throws IOException {
        long bytes = -1;

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(from).getChannel();
            destination = new FileOutputStream(to).getChannel();

            bytes = destination.transferFrom(source, 0, source.size());
        } finally {
            if (null != source && source.isOpen()) {
                source.close();
            }

            if (null != destination && destination.isOpen()) {
                destination.close();
            }
        }

        return bytes;
    }
}
