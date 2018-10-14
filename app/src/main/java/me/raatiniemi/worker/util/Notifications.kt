/*
 * Copyright (C) 2018 Worker Project
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

@file:JvmName("Notifications")

package me.raatiniemi.worker.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.support.v4.app.NotificationCompat
import me.raatiniemi.worker.R

class Notifications {
    companion object {
        private const val ongoingId = "ongoing"
        private const val ongoingTitle = R.string.notification_channel_ongoing_title
        private const val ongoingDescription = R.string.notification_channel_ongoing_description

        private const val backupId = "backup"
        private const val backupTitle = R.string.notification_channel_backup_title
        private const val backupDescription = R.string.notification_channel_backup_description

        fun createChannel(notificationManager: NotificationManager, channel: NotificationChannel) {
            notificationManager.createNotificationChannel(channel)
        }

        fun isOngoingChannelDisabled(notificationManager: NotificationManager): Boolean {
            val channel = notificationManager.getNotificationChannel(ongoingId)

            return NotificationManager.IMPORTANCE_NONE == channel.importance
        }

        fun ongoingChannel(resources: Resources): NotificationChannel {
            val channel = NotificationChannel(
                    ongoingId,
                    resources.getString(ongoingTitle),
                    NotificationManager.IMPORTANCE_LOW
            )
            channel.description = resources.getString(ongoingDescription)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            return channel
        }

        fun ongoingBuilder(context: Context): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, ongoingId)
        }

        fun isBackupChannelDisabled(notificationManager: NotificationManager): Boolean {
            val channel = notificationManager.getNotificationChannel(backupId)

            return NotificationManager.IMPORTANCE_NONE == channel.importance
        }

        fun backupChannel(resources: Resources): NotificationChannel {
            val channel = NotificationChannel(
                    backupId,
                    resources.getString(backupTitle),
                    NotificationManager.IMPORTANCE_LOW
            )
            channel.description = resources.getString(backupDescription)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            return channel
        }

        fun backupBuilder(context: Context): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, backupId)
        }
    }
}
