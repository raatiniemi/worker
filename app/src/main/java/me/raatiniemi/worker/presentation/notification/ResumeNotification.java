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

/**
 * Notification for resuming an inactive project.
 */
public class ResumeNotification {
    private static final int sSmallIcon = R.drawable.ic_timer_off_black_24dp;

    private static final int sResumeIcon = 0;

    public static Notification build(Context context) {
        return new NotificationCompat.Builder(context)
                .setContentTitle("Project name")
                .setSmallIcon(sSmallIcon)
                .addAction(buildResumeAction(context))
                .build();
    }

    private static NotificationCompat.Action buildResumeAction(Context context) {
        return new NotificationCompat.Action(
                sResumeIcon,
                context.getString(R.string.notification_pause_action_resume),
                null
        );
    }
}
