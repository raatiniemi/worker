/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.data.service.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.data.service.data.strategy.StorageBackupStrategy;
import me.raatiniemi.worker.domain.interactor.BackupStrategy;
import me.raatiniemi.worker.domain.interactor.CreateBackup;
import me.raatiniemi.worker.presentation.view.notification.BackupNotification;
import me.raatiniemi.worker.presentation.view.notification.ErrorNotification;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.nonNull;

public class BackupService extends IntentService {
    private final EventBus eventBus = EventBus.getDefault();

    public BackupService() {
        super("BackupService");
    }

    public static void startBackup(Context context) {
        Intent intent = new Intent(context, BackupService.class);
        context.startService(intent);
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
            Timber.w(e, "Unable to cast the NotificationManager");
        } catch (Exception e) {
            // TODO: Post event for `BackupFailure`.
            Timber.w(e, "Unable to backup");

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
            if (nonNull(manager) && nonNull(notification)) {
                manager.notify(
                        WorkerApplication.NOTIFICATION_BACKUP_SERVICE_ID,
                        notification
                );
            }
        }
    }
}
