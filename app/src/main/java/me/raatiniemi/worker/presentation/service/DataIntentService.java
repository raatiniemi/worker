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

package me.raatiniemi.worker.presentation.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.backup.StorageBackupStrategy;
import me.raatiniemi.worker.data.backup.StorageRestoreStrategy;
import me.raatiniemi.worker.domain.interactor.BackupStrategy;
import me.raatiniemi.worker.domain.interactor.CreateBackup;
import me.raatiniemi.worker.domain.interactor.RestoreBackup;
import me.raatiniemi.worker.domain.interactor.RestoreStrategy;
import me.raatiniemi.worker.presentation.notification.BackupNotification;
import me.raatiniemi.worker.presentation.notification.ErrorNotification;
import me.raatiniemi.worker.presentation.view.activity.MainActivity;
import me.raatiniemi.worker.util.Worker;

/**
 * Service coordinating data related operations.
 */
public class DataIntentService extends IntentService {
    /**
     * Intent action for running the backup operation.
     */
    public static final String INTENT_ACTION_BACKUP = "backup";

    /**
     * Intent action for running the restore operation.
     */
    public static final String INTENT_ACTION_RESTORE = "restore";

    /**
     * Tag for logging.
     */
    private static final String TAG = "DataIntentService";

    /**
     * Type of running data operation.
     */
    private static boolean sRunning = false;

    /**
     * Constructor.
     */
    public DataIntentService() {
        super(TAG);
    }

    /**
     * Check whether a data operation is running.
     *
     * @return True if data operation is running, otherwise false.
     */
    public static boolean isRunning() {
        return sRunning;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // If an operation is already running, we should not allow another to
            // start. We wouldn't want backup and restore running simultaneously.
            if (DataIntentService.isRunning()) {
                throw new IllegalStateException("Data operation is already running");
            }

            Context context = getApplicationContext();
            EventBus eventBus = EventBus.getDefault();

            // Check which data operation we should execute.
            String action = intent.getAction();
            switch (action) {
                case INTENT_ACTION_BACKUP:
                    sRunning = true;
                    runBackup(context, eventBus);
                    sRunning = false;
                    break;
                case INTENT_ACTION_RESTORE:
                    sRunning = true;
                    runRestore(context);
                    sRunning = false;
                    break;
                default:
                    throw new IllegalStateException("Received unknown action: " + action);
            }
        } catch (IllegalStateException e) {
            // TODO: Post event `DataOperationFailure`.
            Log.w(TAG, e.getMessage());
        } catch (Exception e) {
            // TODO: Post event `DataOperationFailure`.
            Log.w(TAG, e.getMessage());

            // In case the data operation throw an exception we need to reset
            // the running flag, otherwise we might prevent actions to run.
            sRunning = false;
        }
    }

    /**
     * Execute the backup process.
     *
     * @param context  Application context.
     * @param eventBus Event bus used for notification.
     */
    private void runBackup(Context context, EventBus eventBus) {
        NotificationManager manager = null;
        Notification notification = null;

        try {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create the backup.
            BackupStrategy backupStrategy = new StorageBackupStrategy(context, eventBus);
            CreateBackup createBackup = new CreateBackup(backupStrategy);
            createBackup.execute();

            notification = BackupNotification.build(context);
        } catch (ClassCastException e) {
            // TODO: Post event for `BackupFailure`.
            Log.w(TAG, "Unable to cast the NotificationManager: " + e.getMessage());
        } catch (Exception e) {
            // TODO: Post event for `BackupFailure`.
            Log.w(TAG, "Unable to backup: " + e.getMessage());

            // TODO: Display what was the cause of the backup failure.
            notification = ErrorNotification.build(
                    context,
                    getString(R.string.error_notification_backup_title),
                    getString(R.string.error_notification_backup_message)
            );
        } finally {
            // Both the notification and notification manager must be
            // available, otherwise we can't display the notification.
            //
            // The notification manager won't be available if a
            // ClassCastException have been thrown.
            if (null != manager && null != notification) {
                manager.notify(
                        Worker.NOTIFICATION_DATA_INTENT_SERVICE_ID,
                        notification
                );
            }
        }
    }

    /**
     * Execute the restore process.
     *
     * @param context Application context.
     */
    private void runRestore(Context context) {
        NotificationManager manager = null;
        Notification notification = null;

        try {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Restore the backup.
            RestoreStrategy restoreStrategy = new StorageRestoreStrategy(context);
            RestoreBackup restoreBackup = new RestoreBackup(restoreStrategy);
            restoreBackup.execute();

            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(Worker.INTENT_ACTION_RESTART);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            // Send the "Restore complete" notification to the user.
            notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_restore_white_24dp)
                    .setContentTitle(context.getString(R.string.notification_restore_title))
                    .setContentText(context.getString(R.string.notification_restore_message))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            // TODO: Post event for `RestoreSuccessful`.
        } catch (ClassCastException e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to cast the NotificationManager: " + e.getMessage());
        } catch (Exception e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to restore backup: " + e.getMessage());

            // TODO: Display what was the cause of the restore failure.
            notification = ErrorNotification.build(
                    context,
                    getString(R.string.error_notification_restore_title),
                    getString(R.string.error_notification_restore_message)
            );
        } finally {
            // Both the notification and notification manager must be
            // available, otherwise we can't display the notification.
            //
            // The notification manager won't be available if a
            // ClassCastException have been thrown.
            if (null != manager && null != notification) {
                manager.notify(
                        Worker.NOTIFICATION_DATA_INTENT_SERVICE_ID,
                        notification
                );
            }
        }
    }
}
