/*
 * Copyright (C) 2016 Worker Project
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

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.backup.StorageBackupStrategy;
import me.raatiniemi.worker.domain.interactor.BackupStrategy;
import me.raatiniemi.worker.domain.interactor.CreateBackup;
import me.raatiniemi.worker.presentation.view.notification.BackupNotification;
import me.raatiniemi.worker.presentation.view.notification.ErrorNotification;

public class BackupService extends IntentService {
    private static final String TAG = "BackupService";

    private EventBus eventBus;

    public static void startBackup(Context context) {
        Intent intent = new Intent(context, BackupService.class);
        context.startService(intent);
    }

    public BackupService() {
        super(TAG);

        this.eventBus = EventBus.getDefault();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager manager = null;
        Notification notification = null;

        try {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Create the backup.
            BackupStrategy backupStrategy = new StorageBackupStrategy(this, eventBus);
            CreateBackup createBackup = new CreateBackup(backupStrategy);
            createBackup.execute();

            notification = BackupNotification.build(this);
        } catch (ClassCastException e) {
            // TODO: Post event for `BackupFailure`.
            Log.w(TAG, "Unable to cast the NotificationManager", e);
        } catch (Exception e) {
            // TODO: Post event for `BackupFailure`.
            Log.w(TAG, "Unable to backup", e);

            // TODO: Display what was the cause of the backup failure.
            notification = ErrorNotification.build(
                    this,
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
}
