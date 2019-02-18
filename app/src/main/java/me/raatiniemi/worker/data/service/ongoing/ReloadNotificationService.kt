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
import me.raatiniemi.worker.domain.interactor.FindActiveProjects
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.shared.view.notification.PauseNotification
import timber.log.Timber

class ReloadNotificationService : OngoingService("ReloadNotificationService") {
    private val isOngoingNotificationDisabled: Boolean
        get() = !isOngoingNotificationEnabled

    override fun onHandleIntent(intent: Intent) {
        if (isOngoingNotificationDisabled) {
            return
        }

        try {
            val findActiveProjects = buildFindActiveProjectsUseCase()
            findActiveProjects().forEach {
                sendPauseNotification(it)
            }
        } catch (e: DomainException) {
            Timber.e(e, "Unable to reload notifications")
        }
    }

    private fun buildFindActiveProjectsUseCase(): FindActiveProjects {
        return FindActiveProjects(projectRepository, timeIntervalRepository)
    }

    private fun sendPauseNotification(project: Project) {
        sendNotification(
                project.id,
                PauseNotification.build(
                        this,
                        project,
                        isOngoingNotificationChronometerEnabled
                )
        )
    }

    companion object {
        fun startServiceWithContext(context: Context) {
            val intent = Intent(context, ReloadNotificationService::class.java)

            context.startService(intent)
        }
    }
}
