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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.view.activity.MainActivity;
import me.raatiniemi.worker.Worker;

public class RestoreNotification {
    private static final int sTitle = R.string.notification_restore_title;
    private static final int sMessage = R.string.notification_restore_message;

    private static final int sSmallIcon = R.drawable.ic_restore_white_24dp;

    private RestoreNotification() {
    }

    public static Notification build(Context context) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(sTitle))
                .setContentText(context.getString(sMessage))
                .setSmallIcon(sSmallIcon)
                .setContentIntent(buildContentAction(context))
                .setAutoCancel(true)
                .build();
    }

    private static PendingIntent buildContentAction(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Worker.INTENT_ACTION_RESTART);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return buildPendingIntentWithActivity(context, intent);
    }

    public static PendingIntent buildPendingIntentWithActivity(
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
