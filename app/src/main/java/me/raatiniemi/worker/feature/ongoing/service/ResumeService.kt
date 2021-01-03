/*
 * Copyright (C) 2021 Tobias Raatiniemi
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
import me.raatiniemi.worker.domain.timeinterval.usecase.ActiveProjectException
import me.raatiniemi.worker.domain.timeinterval.usecase.CalculateTimeToday
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.feature.ongoing.view.PauseNotification
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

internal class ResumeService : OngoingService("ResumeService"), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private val usageAnalytics: UsageAnalytics by inject()
    private val clockIn: ClockIn by inject()
    private val getProject: GetProject by inject()
    private val calculateTimeToday: CalculateTimeToday by inject()

    override fun onHandleIntent(intent: Intent?) {
        launch {
            try {
                val projectId = getProjectId(intent)
                val project = getProject(projectId)

                clockIn(project)

                val calculatedTimeToday = calculateTimeToday(project)
                withContext(Dispatchers.Main) {
                    updateUserInterface(project)
                    sendOrDismissPauseNotification(project, calculatedTimeToday)
                }
            } catch (e: Exception) {
                Timber.e(e, "Unable to resume project")
            }
        }
    }

    private suspend fun clockIn(project: Project) {
        try {
            clockIn(project, Milliseconds.now)

            usageAnalytics.log(Event.NotificationClockIn)
        } catch (e: ActiveProjectException) {
            Timber.w(e, "Resume service called with active project")
        }
    }

    private fun sendOrDismissPauseNotification(project: Project, calculatedTimeToday: Long) {
        sendOrDismissOngoingNotification(project) {
            PauseNotification.build(
                this,
                project,
                calculatedTimeToday,
                isOngoingNotificationChronometerEnabled
            )
        }
    }
}
