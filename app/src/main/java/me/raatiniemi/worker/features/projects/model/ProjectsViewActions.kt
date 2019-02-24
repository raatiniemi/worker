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

package me.raatiniemi.worker.features.projects.model

import android.app.NotificationManager
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.data.service.ongoing.ProjectNotificationService
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.projects.adapter.ProjectsAdapter
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.ContextViewAction
import timber.log.Timber

internal sealed class ProjectsViewActions {
    data class UpdateNotification(val project: Project) : ProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            ProjectNotificationService.startServiceWithContext(activity, project)
        }
    }

    data class DismissNotification(val project: Project) : ProjectsViewActions(), ContextViewAction {
        override fun action(context: Context) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager == null) {
                Timber.w("Unable to get notification manager from context")
                return
            }

            notificationManager.cancel(
                    project.id.toString(),
                    WorkerApplication.NOTIFICATION_ON_GOING_ID
            )
        }
    }

    object ShowUnableToClockInErrorMessage : ProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_clock_in,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    object ShowUnableToClockOutErrorMessage : ProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_clock_out,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    object ShowUnableToDeleteProjectErrorMessage : ProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_project_deleted,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class RefreshProjects(private val positions: List<Int>) : ProjectsViewActions() {
        fun action(adapter: ProjectsAdapter) {
            Timber.d("Refreshing %d projects", positions.size)

            positions.forEach { adapter.notifyItemChanged(it) }
        }
    }
}
