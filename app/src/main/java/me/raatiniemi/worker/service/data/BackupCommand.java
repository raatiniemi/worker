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
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.util.Worker;

/**
 * Backup operation.
 */
class BackupCommand extends DataCommand {
    private static final String TAG = "BackupCommand";

    /**
     * Backup strategy.
     */
    private BackupStrategy mStrategy;

    /**
     * @inheritDoc
     */
    BackupCommand(Context context, BackupStrategy strategy) {
        super(context);

        mStrategy = strategy;
    }

    /**
     * Get the backup strategy.
     *
     * @return Backup strategy.
     */
    protected BackupStrategy getStrategy() {
        return mStrategy;
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

            // Execute the backup strategy.
            getStrategy().execute();

            // Send the "Backup complete" notification to the user.
            notification = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.ic_archive_white_24dp)
                    .setContentTitle(getContext().getString(R.string.notification_backup_title))
                    .setContentText(getContext().getString(R.string.notification_backup_message));
        } catch (ClassCastException e) {
            // TODO: Post event for `BackupFailure`.
            Log.w(TAG, "Unable to cast the NotificationManager: " + e.getMessage());
        } catch (Exception e) {
            // TODO: Post event for `BackupFailure`.
            Log.w(TAG, "Unable to backup: " + e.getMessage());

            // TODO: Display what was the cause of the backup failure.
            // Send the "Backup failed" notification to the user.
            notification = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.ic_error_outline_white_24dp)
                    .setContentTitle(getContext().getString(R.string.error_notification_backup_title))
                    .setContentText(getContext().getString(R.string.error_notification_backup_message));
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
