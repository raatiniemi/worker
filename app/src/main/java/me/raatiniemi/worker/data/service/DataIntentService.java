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

package me.raatiniemi.worker.data.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.backup.StorageRestoreStrategy;
import me.raatiniemi.worker.domain.interactor.RestoreBackup;
import me.raatiniemi.worker.domain.interactor.RestoreStrategy;
import me.raatiniemi.worker.presentation.view.notification.ErrorNotification;
import me.raatiniemi.worker.presentation.view.notification.RestoreNotification;

/**
 * Service coordinating data related operations.
 */
public class DataIntentService extends IntentService {
    /**
     * Intent action for running the restore operation.
     */
    private static final String INTENT_ACTION_RESTORE = "restore";

    /**
     * Tag for logging.
     */
    private static final String TAG = "DataIntentService";

    /**
     * Type of running data operation.
     */
    private static boolean running = false;

    /**
     * Constructor.
     */
    public DataIntentService() {
        super(TAG);
    }

    public static void startRestore(Context context) {
        Intent intent = new Intent(context, DataIntentService.class);
        intent.setAction(INTENT_ACTION_RESTORE);

        context.startService(intent);
    }

    /**
     * Check whether a data operation is running.
     *
     * @return True if data operation is running, otherwise false.
     */
    public static boolean isRunning() {
        return running;
    }

    @Override
    protected synchronized void onHandleIntent(Intent intent) {
        try {
            // If an operation is already running, we should not allow another to
            // start. We wouldn't want backup and restore running simultaneously.
            if (DataIntentService.isRunning()) {
                throw new IllegalStateException("Data operation is already running");
            }

            Context context = getApplicationContext();

            // Check which data operation we should execute.
            String action = intent.getAction();
            switch (action) {
                case INTENT_ACTION_RESTORE:
                    running = true;
                    runRestore(context);
                    running = false;
                    break;
                default:
                    throw new IllegalStateException("Received unknown action: " + action);
            }
        } catch (IllegalStateException e) {
            // TODO: Post event `DataOperationFailure`.
            Log.w(TAG, "Failed to perform data operation", e);
        } catch (Exception e) {
            // TODO: Post event `DataOperationFailure`.
            Log.w(TAG, "Failed to perform data operation", e);

            // In case the data operation throw an exception we need to reset
            // the running flag, otherwise we might prevent actions to run.
            running = false;
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

            notification = RestoreNotification.build(context);
            // TODO: Post event for `RestoreSuccessful`.
        } catch (ClassCastException e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to cast the NotificationManager", e);
        } catch (Exception e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to restore backup: ", e);

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
