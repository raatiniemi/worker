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

import android.content.Intent
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.exception.ActiveProjectException
import me.raatiniemi.worker.domain.interactor.CalculateTimeToday
import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.GetProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.shared.view.notification.ErrorNotification
import me.raatiniemi.worker.features.shared.view.notification.PauseNotification
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

internal class ResumeService : OngoingService("ResumeService") {
    private val usageAnalytics: UsageAnalytics by inject()
    private val clockIn: ClockIn by inject()
    private val getProject: GetProject by inject()
    private val calculateTimeToday: CalculateTimeToday by inject()

    override fun onHandleIntent(intent: Intent?) {
        val projectId = getProjectId(intent)

        try {
            clockIn(projectId, Date())

            usageAnalytics.log(Event.NotificationClockIn)
            updateUserInterface(projectId)

            if (isOngoingNotificationEnabled) {
                val project = getProject(projectId)
                sendPauseNotification(project)
                return
            }
            dismissNotification(projectId)
        } catch (e: ActiveProjectException) {
            Timber.e(e, "Resume service called with active project")
        } catch (e: Exception) {
            Timber.w(e, "Unable to resume project")
            sendErrorNotification(projectId)
        }
    }

    private fun sendPauseNotification(project: Project) {
        val notification = PauseNotification.build(
                this,
                project,
                calculateTimeToday(project),
                isOngoingNotificationChronometerEnabled
        )
        sendNotification(project.id, notification)
    }

    private fun sendErrorNotification(projectId: Long) {
        sendNotification(
                projectId,
                ErrorNotification.buildOngoing(
                        this,
                        getString(R.string.ongoing_notification_unable_to_resume_title),
                        getString(R.string.ongoing_notification_unable_to_resume_message)
                )
        )
    }
}
