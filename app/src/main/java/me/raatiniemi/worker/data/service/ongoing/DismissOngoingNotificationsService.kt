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

package me.raatiniemi.worker.data.service.ongoing

import android.content.Intent
import me.raatiniemi.worker.domain.interactor.FindAllProjects
import me.raatiniemi.worker.domain.interactor.findAllProjects
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.koin.android.ext.android.inject

class DismissOngoingNotificationsService : OngoingService("DismissOngoingNotificationsService") {
    private val repository: ProjectRepository by inject()
    private val findAllProjects: FindAllProjects by lazy {
        findAllProjects(repository)
    }

    override fun onHandleIntent(intent: Intent?) {
        findAllProjects()
            .forEach { dismissNotification(it.id) }
    }
}
