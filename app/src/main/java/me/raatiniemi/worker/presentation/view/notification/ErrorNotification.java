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
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.R;

public class ErrorNotification {
    private static final int SMALL_ICON = R.drawable.ic_error_notification;

    private ErrorNotification() {
    }

    public static Notification build(
            Context context,
            String title,
            String text
    ) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(SMALL_ICON)
                .build();
    }
}
