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

package me.raatiniemi.worker.presentation.view.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.presentation.projects.view.ProjectsActivity;

public class RestoreNotification {
    private static final int TITLE = R.string.notification_restore_title;
    private static final int MESSAGE = R.string.notification_restore_message;

    private static final int SMALL_ICON = R.drawable.ic_restore_notification;

    private RestoreNotification() {
    }

    public static Notification build(Context context) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(TITLE))
                .setContentText(context.getString(MESSAGE))
                .setSmallIcon(SMALL_ICON)
                .setContentIntent(buildContentAction(context))
                .setAutoCancel(true)
                .build();
    }

    private static PendingIntent buildContentAction(Context context) {
        Intent intent = new Intent(context, ProjectsActivity.class);
        intent.setAction(Worker.INTENT_ACTION_RESTART);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

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
