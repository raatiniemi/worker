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

package me.raatiniemi.worker.features.projects.all.model

import android.app.NotificationManager
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.data.service.ongoing.ProjectNotificationService
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.projects.all.adapter.AllProjectsAdapter
import me.raatiniemi.worker.features.projects.all.view.AllProjectsFragmentDirections
import me.raatiniemi.worker.features.projects.all.view.ClockActivityAtFragment
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.ContextViewAction
import me.raatiniemi.worker.features.shared.model.FragmentViewAction
import timber.log.Timber
import java.util.*

internal sealed class AllProjectsViewActions {
    data class RefreshProjects(private val positions: List<Int>) : AllProjectsViewActions() {
        fun action(adapter: AllProjectsAdapter) {
            Timber.d("Refreshing %d projects", positions.size)

            positions.forEach { adapter.notifyItemChanged(it) }
        }
    }

    data class OpenProject(private val project: Project) : AllProjectsViewActions(), FragmentViewAction {
        override fun action(fragment: Fragment) {
            val destinationAction = AllProjectsFragmentDirections.openTimeReport(project.id, project.name)

            fragment.findNavController()
                    .navigate(destinationAction)
        }
    }

    data class ShowConfirmClockOutMessage(val item: ProjectsItem, val date: Date) : AllProjectsViewActions()

    data class ShowChooseTimeForClockActivity(val item: ProjectsItem) : AllProjectsViewActions() {
        fun action(fragmentManager: FragmentManager, onChooseTime: (ProjectsItem, Date) -> Unit) {
            val fragment = ClockActivityAtFragment.newInstance(item) {
                onChooseTime(item, it.time)
            }

            fragmentManager.beginTransaction()
                    .add(fragment, "clock activity at")
                    .commit()
        }
    }

    object ShowUnableToClockInErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_clock_in,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    object ShowUnableToClockOutErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_clock_out,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class ShowConfirmRemoveProjectMessage(val item: ProjectsItem) : AllProjectsViewActions()

    object ShowUnableToDeleteProjectErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_project_deleted,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class UpdateNotification(val project: Project) : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            ProjectNotificationService.startServiceWithContext(activity, project)
        }
    }

    data class DismissNotification(val project: Project) : AllProjectsViewActions(), ContextViewAction {
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
}
