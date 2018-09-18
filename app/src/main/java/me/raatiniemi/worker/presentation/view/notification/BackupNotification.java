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

package me.raatiniemi.worker.presentation.view.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.features.projects.view.ProjectsActivity;
import me.raatiniemi.worker.presentation.util.Notifications;

public class BackupNotification {
    private static final int TITLE = R.string.notification_backup_title;
    private static final int MESSAGE = R.string.notification_backup_message;

    private static final int SMALL_ICON = R.drawable.ic_backup_notification;

    private BackupNotification() {
    }

    public static Notification build(Context context) {
        return Notifications.Companion.backupBuilder(context)
                .setContentTitle(context.getString(TITLE))
                .setContentText(context.getString(MESSAGE))
                .setSmallIcon(SMALL_ICON)
                .setContentIntent(buildContentAction(context))
                .setAutoCancel(true)
                .build();
    }

    private static PendingIntent buildContentAction(Context context) {
        Intent intent = new Intent(context, ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return buildPendingIntentWithActivity(context, intent);
    }

    private static PendingIntent buildPendingIntentWithActivity(
            Context context,
            Intent intent
    ) {
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
