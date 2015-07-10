package me.raatiniemi.worker.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.util.ExternalStorage;
import me.raatiniemi.worker.util.FileUtils;
import me.raatiniemi.worker.util.Worker;

/**
 * Service for running data operations.
 */
public class DataIntentService extends IntentService {
    /**
     * Intent action for running the backup operation.
     */
    public static final String INTENT_ACTION_BACKUP = "backup";

    /**
     * Tag for logging.
     */
    private static final String TAG = "DataIntentService";

    /**
     * Type of running data operation.
     */
    private static RUNNING sRunning = RUNNING.NONE;

    /**
     * Constructor.
     */
    public DataIntentService() {
        super(TAG);
    }

    /**
     * Get the type of data operation that is running. If the RUNNING.NONE is
     * returned no data operation is currently running.
     *
     * @return Type of running data operation.
     */
    public static RUNNING getRunning() {
        return sRunning;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // If an operation is already running, we should not allow another to
        // start. We wouldn't want backup and restore running simultaneously.
        if (RUNNING.NONE != getRunning()) {
            Log.w(TAG, "Data operation is already running, exiting");
            return;
        }

        String action = intent.getAction();
        switch (action) {
            case INTENT_ACTION_BACKUP:
                sRunning = RUNNING.BACKUP;
                backup();
                break;
            default:
                Log.w(TAG, "Received unknown action: " + action);
                break;
        }
        sRunning = RUNNING.NONE;
    }

    /**
     * Run the backup operation. Copies the SQLite database to the backup
     * directory on the external storage.
     */
    private synchronized void backup() {
        NotificationManager manager = null;
        NotificationCompat.Builder notification = null;

        try {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Check that the external storage is writable.
            if (!ExternalStorage.isWritable()) {
                throw new IOException("External storage is not writable");
            }

            // Build the backup folder name with the current timestamp to prevent
            // running multiple backups against the same directory, which would
            // effectively override any previous backups.
            String backupName = "backup-" + (new Date()).getTime();
            File directory = ExternalStorage.getBackupDirectory(backupName);

            // Check that the backup directory is available.
            if (null == directory) {
                throw new FileNotFoundException("Directory for backup is not available");
            }

            // Retrieve the source and destination file locations.
            File from = getDatabasePath(Worker.DATABASE_NAME);
            File to = new File(directory, Worker.DATABASE_NAME);

            // Perform the file copy.
            FileUtils.copy(from, to);

            // Send the "Backup complete" notification to the user.
            notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_backup_white_24dp)
                .setContentTitle(getString(R.string.data_intent_service_backup_complete_title))
                .setContentText(getString(R.string.data_intent_service_backup_complete_message));
        } catch (IOException e) {
            Log.w(TAG, "Unable to backup: " + e.getMessage());

            // TODO: Display what was the cause of the backup failure.
            // Send the "Backup failed" notification to the user.
            notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_error_outline_white_24dp)
                .setContentTitle(getString(R.string.data_intent_service_backup_failed_title))
                .setContentText(getString(R.string.data_intent_service_backup_failed_message));
        } catch (ClassCastException e) {
            Log.w(TAG, "Unable to cast the NotificationManager: " + e.getMessage());
        } finally {
            // Both the notification and notification manager must be
            // available, otherwise we can't display the notification.
            //
            // The notification manager won't be available if a
            // ClassCastException have been thrown.
            if (null != manager && null != notification) {
                manager.notify(
                    Worker.NOTIFICATION_DATA_INTENT_SERVICE_ID,
                    notification.build()
                );
            }
        }
    }

    /**
     * Available types of runnable data operations.
     */
    public enum RUNNING {
        NONE,
        BACKUP
    }
}
