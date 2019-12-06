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
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.FindActiveProjects
import me.raatiniemi.worker.domain.timeinterval.usecase.CalculateTimeToday
import me.raatiniemi.worker.feature.ongoing.view.PauseNotification
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class ReloadNotificationService : OngoingService("ReloadNotificationService"), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private val findActiveProjects: FindActiveProjects by inject()
    private val calculateTimeToday: CalculateTimeToday by inject()

    private val isOngoingNotificationDisabled: Boolean
        get() = !isOngoingNotificationEnabled

    override fun onHandleIntent(intent: Intent?) {
        if (isOngoingNotificationDisabled) {
            return
        }

        launch {
            try {
                val projects = findActiveProjects()
                withContext(Dispatchers.Main) {
                    projects.forEach {
                        sendOrDismissPauseNotification(it)
                    }
                }
            } catch (e: DomainException) {
                Timber.e(e, "Unable to reload notifications")
            }
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
