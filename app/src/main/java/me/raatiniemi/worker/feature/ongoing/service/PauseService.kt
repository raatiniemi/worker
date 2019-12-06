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

package me.raatiniemi.worker.feature.ongoing.service

import android.content.Intent
import kotlinx.coroutines.*
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.GetProject
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.InactiveProjectException
import me.raatiniemi.worker.feature.ongoing.view.ResumeNotification
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

internal class PauseService : OngoingService("PauseService"), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private val usageAnalytics: UsageAnalytics by inject()
    private val getProject: GetProject by inject()
    private val clockOut: ClockOut by inject()

    override fun onHandleIntent(intent: Intent?) {
        launch {
            try {
                val projectId = getProjectId(intent)
                val project = getProject(projectId)

                clockOut(project)

                withContext(Dispatchers.Main) {
                    updateUserInterface(project)
                    sendOrDismissResumeNotification(project)
                }
            } catch (e: InactiveProjectException) {
                Timber.w(e, "Pause service called with inactive project")
                // We should never resend the resume notification since that would cause
                // the timer to reset giving an incorrect time elapsed for the pause.
            } catch (e: Exception) {
                Timber.e(e, "Unable to pause project")
            }
        }
    }

    private fun clockOut(project: Project) {
        clockOut(project, Milliseconds.now)

        usageAnalytics.log(Event.NotificationClockOut)
    }

    private fun sendOrDismissResumeNotification(project: Project) {
        sendOrDismissOngoingNotification(project) {
            ResumeNotification.build(
                this,
                project,
                isOngoingNotificationChronometerEnabled
            )
        }
    }
}
