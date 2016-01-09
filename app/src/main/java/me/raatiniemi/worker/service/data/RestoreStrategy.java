/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.service.data;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.greenrobot.event.EventBus;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.view.activity.MainActivity;
import me.raatiniemi.worker.util.ExternalStorage;
import me.raatiniemi.worker.util.FileUtils;
import me.raatiniemi.worker.util.Worker;

/**
 * Strategy for restoring data backups.
 * <p>
 * Copies the latest backup of the SQLite database from the external storage to
 * the application database location.
 */
class RestoreStrategy extends DataStrategy {
    private static final String TAG = "RestoreStrategy";

    /**
     * @inheritDoc
     */
    RestoreStrategy(Context context, EventBus eventBus) {
        super(context, eventBus);
    }

    /**
     * @inheritDoc
     */
    @Override
    synchronized void execute() {
        NotificationManager manager = null;
        NotificationCompat.Builder notification = null;

        try {
            manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            // Check that the external storage is readable.
            if (!ExternalStorage.isReadable()) {
                throw new IOException("External storage is not readable");
            }

            // Check that we have backup to restore from.
            File directory = ExternalStorage.getLatestBackupDirectory();
            if (null == directory) {
                throw new FileNotFoundException("Unable to find backup from which to restore");
            }

            // Retrieve the source and destination file locations.
            File from = new File(directory, Worker.DATABASE_NAME);
            File to = getContext().getDatabasePath(Worker.DATABASE_NAME);

            // Perform the file copy.
            FileUtils.copy(from, to);

            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setAction(Worker.INTENT_ACTION_RESTART);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            // Send the "Restore complete" notification to the user.
            notification = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.ic_restore_white_24dp)
                    .setContentTitle(getContext().getString(R.string.notification_restore_title))
                    .setContentText(getContext().getString(R.string.notification_restore_message))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            // TODO: Post event for `RestoreSuccessful`.
        } catch (IOException e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to restore backup: " + e.getMessage());

            // TODO: Display what was the cause of the restore failure.
            // Send the "Restore failed" notification to the user.
            notification = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.ic_error_outline_white_24dp)
                    .setContentTitle(getContext().getString(R.string.error_notification_restore_title))
                    .setContentText(getContext().getString(R.string.error_notification_restore_message));
        } catch (ClassCastException e) {
            // TODO: Post event for `RestoreFailure`.
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
}
