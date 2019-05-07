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

import android.content.Context
import android.content.Intent
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.interactor.CalculateTimeToday
import me.raatiniemi.worker.domain.interactor.FindActiveProjects
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.shared.view.notification.PauseNotification
import org.koin.android.ext.android.inject
import timber.log.Timber

class ReloadNotificationService : OngoingService("ReloadNotificationService") {
    private val findActiveProjects: FindActiveProjects by inject()
    private val calculateTimeToday: CalculateTimeToday by inject()

    private val isOngoingNotificationDisabled: Boolean
        get() = !isOngoingNotificationEnabled

    override fun onHandleIntent(intent: Intent) {
        if (isOngoingNotificationDisabled) {
            return
        }

        try {
            findActiveProjects().forEach {
                sendOrDismissPauseNotification(it)
            }
        } catch (e: DomainException) {
            Timber.e(e, "Unable to reload notifications")
        }
    }

    private fun sendOrDismissPauseNotification(project: Project) {
        sendOrDismissOngoingNotification(project) {
            PauseNotification.build(
                this,
                project,
                calculateTimeToday(project),
                isOngoingNotificationChronometerEnabled
            )
        }
    }

    companion object {
        fun startServiceWithContext(context: Context) {
            val intent = Intent(context, ReloadNotificationService::class.java)

            context.startService(intent)
        }
    }
}
