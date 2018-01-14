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

package me.raatiniemi.worker.presentation.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import me.raatiniemi.worker.R

class Notifications {
    companion object {
        private val ongoingId = "ongoing"
        private val ongoingTitle = R.string.notification_channel_ongoing_title
        private val ongoingDescription = R.string.notification_channel_ongoing_description

        private val backupId = "backup"
        private val backupTitle = R.string.notification_channel_backup_title
        private val backupDescription = R.string.notification_channel_backup_description

        val isChannelsAvailable: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        @RequiresApi(Build.VERSION_CODES.O)
        fun createChannel(notificationManager: NotificationManager, channel: NotificationChannel) {
            notificationManager.createNotificationChannel(channel)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun isOngoingChannelDisabled(notificationManager: NotificationManager): Boolean {
            val channel = notificationManager.getNotificationChannel(ongoingId)

            return NotificationManager.IMPORTANCE_NONE == channel.importance
        }

        @RequiresApi(Build.VERSION_CODES.O)
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
            if (isChannelsAvailable) {
                return NotificationCompat.Builder(context, ongoingId)
            }

            return NotificationCompat.Builder(context)
        }

        @RequiresApi(Build.VERSION_CODES.O)
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
            if (isChannelsAvailable) {
                return NotificationCompat.Builder(context, backupId)
            }

            return NotificationCompat.Builder(context)
        }
    }
}
