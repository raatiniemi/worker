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

package me.raatiniemi.worker.presentation.notification;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.R;

public class BackupNotification {
    private static final int sTitle = R.string.notification_backup_title;
    private static final int sText = R.string.notification_backup_message;

    private static final int sSmallIcon = R.drawable.ic_archive_white_24dp;

    public static Notification build(Context context) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(sTitle))
                .setContentText(context.getString(sText))
                .setSmallIcon(sSmallIcon)
                .build();
    }
}
