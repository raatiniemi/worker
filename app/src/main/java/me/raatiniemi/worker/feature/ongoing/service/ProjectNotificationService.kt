/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.GetProject
import me.raatiniemi.worker.domain.project.usecase.IsProjectActive
import me.raatiniemi.worker.domain.timeinterval.usecase.CalculateTimeToday
import me.raatiniemi.worker.feature.ongoing.model.OngoingUriCommunicator
import me.raatiniemi.worker.feature.ongoing.view.PauseNotification
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class ProjectNotificationService : OngoingService("ProjectNotificationService"), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private val getProject: GetProject by inject()
    private val isProjectActive: IsProjectActive by inject()
    private val calculateTimeToday: CalculateTimeToday by inject()

    override fun onHandleIntent(intent: Intent?) {
        launch {
            try {
                val projectId = getProjectId(intent)
                val project = getProject(projectId)

                val isProjectActive = isProjectActive(project.id.value)
                val calculatedTimeToday = calculateTimeToday(project)
                withContext(Dispatchers.Main) {
                    if (isProjectActive) {
                        sendOrDismissPauseNotification(project, calculatedTimeToday)
                        return@withContext
                    }
                    dismissNotification(project)
                }
            } catch (e: Exception) {
                Timber.e(e, "Unable to update notification for project")
            }
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

    companion object {
        fun startServiceWithContext(context: Context, project: Project) {
            val intent = Intent(context, ProjectNotificationService::class.java)
            intent.data = OngoingUriCommunicator.createWith(project.id.value)
            context.startService(intent)
        }
    }
}
