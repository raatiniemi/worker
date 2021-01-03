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
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.project.usecase.FindAllProjects
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class DismissOngoingNotificationsService :
    OngoingService("DismissOngoingNotificationsService"), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private val repository: ProjectRepository by inject()
    private val findAllProjects: FindAllProjects by lazy {
        FindAllProjects(repository)
    }

    override fun onHandleIntent(intent: Intent?) {
        launch {
            val projects = findAllProjects()
            withContext(Dispatchers.Main) {
                projects.forEach {
                    dismissNotification(it)
                }
            }
        }
    }
}
