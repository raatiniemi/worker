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
import me.raatiniemi.worker.domain.exception.ActiveProjectException
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.usecase.CalculateTimeToday
import me.raatiniemi.worker.domain.usecase.ClockIn
import me.raatiniemi.worker.domain.usecase.GetProject
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
        try {
            val projectId = getProjectId(intent)
            val project = getProject(projectId)

            clockIn(project)

            updateUserInterface(project.id.value)
            sendOrDismissPauseNotification(project)
        } catch (e: Exception) {
            Timber.e(e, "Unable to resume project")
        }
    }

    private fun clockIn(project: Project) {
        try {
            clockIn(project.id.value, Date())

            usageAnalytics.log(Event.NotificationClockIn)
        } catch (e: ActiveProjectException) {
            Timber.w(e, "Resume service called with active project")
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
}
