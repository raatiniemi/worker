/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.raatiniemi.worker.R
import timber.log.Timber

class Notifications {
    companion object {
        private const val ongoingId = "ongoing"
        private const val ongoingTitle = R.string.ongoing_notification_channel_title
        private const val ongoingDescription = R.string.ongoing_notification_channel_description

        fun createChannel(
            notificationManager: NotificationManagerCompat,
            channel: NotificationChannel
        ) {
            notificationManager.createNotificationChannel(channel)
        }

        fun isOngoingChannelDisabled(notificationManager: NotificationManagerCompat): Boolean {
            val channel = notificationManager.getNotificationChannel(ongoingId)
            if (channel == null) {
                Timber.d("Notification channels are not available for device")
                return false
            }

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
            return NotificationCompat.Builder(
                context,
                ongoingId
            )
        }
    }
}
