/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.ongoing.service

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.feature.ongoing.model.OngoingUriCommunicator
import me.raatiniemi.worker.feature.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.feature.shared.view.isOngoingChannelDisabled
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import timber.log.Timber

abstract class OngoingService internal constructor(name: String) : IntentService(name) {
    private val eventBus: EventBus = EventBus.getDefault()
    private val keyValueStore: KeyValueStore by inject()

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(applicationContext)
    }

    protected val isOngoingNotificationEnabled: Boolean by lazy {
        keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_ENABLED, true)
    }

    protected val isOngoingNotificationChronometerEnabled: Boolean by lazy {
        keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, true)
    }

    private fun buildNotificationTag(project: Project): String = "${project.id.value}"

    protected fun getProjectId(intent: Intent?): Long {
        val projectId = OngoingUriCommunicator.parseFrom(intent?.data)
        if (0L == projectId) {
            throw IllegalArgumentException("Unable to extract project id from URI")
        }

        return projectId
    }

    protected fun sendOrDismissOngoingNotification(project: Project, producer: () -> Notification) {
        if (isOngoingNotificationEnabled) {
            if (!isOngoingChannelDisabled(applicationContext)) {
                sendNotification(project, producer())
                return
            }

            Timber.d("Ongoing notification channel is disabled, ignoring notification")
        }

        dismissNotification(project)
    }

    private fun sendNotification(project: Project, notification: Notification) {
        notificationManager.notify(
            buildNotificationTag(project),
            WorkerApplication.NOTIFICATION_ON_GOING_ID,
            notification
        )
    }

    protected fun dismissNotification(project: Project) {
        notificationManager.cancel(
            buildNotificationTag(project),
            WorkerApplication.NOTIFICATION_ON_GOING_ID
        )
    }

    protected fun updateUserInterface(project: Project) {
        eventBus.post(OngoingNotificationActionEvent(project))
    }
}
