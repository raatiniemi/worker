/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.service.ongoing

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.Notifications
import me.raatiniemi.worker.util.OngoingUriCommunicator
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import timber.log.Timber

abstract class OngoingService internal constructor(name: String) : IntentService(name) {
    private val eventBus: EventBus = EventBus.getDefault()
    private val keyValueStore: KeyValueStore by inject()

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    protected val isOngoingNotificationEnabled: Boolean by lazy {
        keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_ENABLED, true)
    }

    protected val isOngoingNotificationChronometerEnabled: Boolean by lazy {
        keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, true)
    }

    private fun buildNotificationTag(projectId: Long): String = projectId.toString()

    protected fun getProjectId(intent: Intent?): Long {
        val projectId = OngoingUriCommunicator.parseFrom(intent?.data)
        if (0L == projectId) {
            throw IllegalArgumentException("Unable to extract project id from URI")
        }

        return projectId
    }

    protected fun sendOrDismissOngoingNotification(project: Project, producer: () -> Notification) {
        if (isOngoingNotificationEnabled) {
            if (!Notifications.isOngoingChannelDisabled(notificationManager)) {
                sendNotification(project.id.value, producer())
                return
            }

            Timber.d("Ongoing notification channel is disabled, ignoring notification")
        }

        dismissNotification(project.id.value)
    }

    private fun sendNotification(projectId: Long, notification: Notification) {
        notificationManager.notify(
            buildNotificationTag(projectId),
            WorkerApplication.NOTIFICATION_ON_GOING_ID,
            notification
        )
    }

    protected fun dismissNotification(projectId: Long) {
        notificationManager.cancel(
            buildNotificationTag(projectId),
            WorkerApplication.NOTIFICATION_ON_GOING_ID
        )
    }

    protected fun updateUserInterface(projectId: Long) {
        eventBus.post(
            OngoingNotificationActionEvent(projectId)
        )
    }
}
