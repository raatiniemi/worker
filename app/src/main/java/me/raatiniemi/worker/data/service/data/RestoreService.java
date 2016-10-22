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

package me.raatiniemi.worker.data.service.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.service.data.strategy.StorageRestoreStrategy;
import me.raatiniemi.worker.domain.interactor.RestoreBackup;
import me.raatiniemi.worker.domain.interactor.RestoreStrategy;
import me.raatiniemi.worker.presentation.view.notification.ErrorNotification;
import me.raatiniemi.worker.presentation.view.notification.RestoreNotification;

import static me.raatiniemi.util.NullUtil.nonNull;

public class RestoreService extends IntentService {
    private static final String TAG = "RestoreService";

    public RestoreService() {
        super(TAG);
    }

    public static void startRestore(Context context) {
        Intent intent = new Intent(context, RestoreService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager manager = null;
        Notification notification = null;

        try {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Restore the backup.
            RestoreStrategy restoreStrategy = new StorageRestoreStrategy(this);
            RestoreBackup restoreBackup = new RestoreBackup(restoreStrategy);
            restoreBackup.execute();

            notification = RestoreNotification.build(this);
            // TODO: Post event for `RestoreSuccessful`.
        } catch (ClassCastException e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to cast the NotificationManager", e);
        } catch (Exception e) {
            // TODO: Post event for `RestoreFailure`.
            Log.w(TAG, "Unable to restore backup: ", e);

            // TODO: Display what was the cause of the restore failure.
            notification = ErrorNotification.build(
                    this,
                    getString(R.string.error_notification_restore_title),
                    getString(R.string.error_notification_restore_message)
            );
        } finally {
            // Both the notification and notification manager must be
            // available, otherwise we can't display the notification.
            //
            // The notification manager won't be available if a
            // ClassCastException have been thrown.
            if (nonNull(manager) && nonNull(notification)) {
                manager.notify(
                        Worker.NOTIFICATION_RESTORE_SERVICE_ID,
                        notification
                );
            }
        }
    }
}
