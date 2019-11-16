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

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.features.ongoing.service.ProjectNotificationService
import me.raatiniemi.worker.features.projects.all.adapter.AllProjectsAdapter
import me.raatiniemi.worker.features.projects.all.view.AllProjectsFragmentDirections
import me.raatiniemi.worker.features.projects.createproject.view.CreateProjectDialogFragment
import me.raatiniemi.worker.features.shared.datetime.view.DateTimePickerDialogFragment
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.ContextViewAction
import me.raatiniemi.worker.features.shared.model.FragmentViewAction
import me.raatiniemi.worker.features.shared.view.show
import timber.log.Timber
import java.util.*

internal sealed class AllProjectsViewActions {
    object CreateProject : AllProjectsViewActions() {
        fun action(fragment: Fragment, onCreateProject: () -> Unit) {
            val dialogFragment = CreateProjectDialogFragment.newInstance(onCreateProject)
            fragment.show(dialogFragment)
        }
    }

    object ProjectCreated : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.projects_all_project_created_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class RefreshProjects(private val positions: List<Int>) : AllProjectsViewActions() {
        fun action(adapter: AllProjectsAdapter) {
            Timber.d("Refreshing %d projects", positions.size)

            positions.forEach { adapter.notifyItemChanged(it) }
        }
    }

    data class OpenProject(private val project: Project) : AllProjectsViewActions(),
        FragmentViewAction {
        override fun action(fragment: Fragment) {
            val destinationAction =
                AllProjectsFragmentDirections.openTimeReport(project.id.value, project.name.value)

            fragment.findNavController()
                .navigate(destinationAction)
        }
    }

    data class ShowConfirmClockOutMessage(val item: ProjectsItem, val date: Date) :
        AllProjectsViewActions()

    data class ChooseDateAndTimeForClockIn(val item: ProjectsItem) : AllProjectsViewActions() {
        fun action(fragment: Fragment, onChooseTime: (Project, Date) -> Unit) {
            val dialogFragment = DateTimePickerDialogFragment.newInstance { configuration ->
                configuration.choose = { date ->
                    onChooseTime(item.asProject(), date)
                }
            }
            fragment.show(dialogFragment)
        }
    }

    data class ChooseDateAndTimeForClockOut(val item: ProjectsItem) : AllProjectsViewActions() {
        fun action(fragment: Fragment, onChooseTime: (Project, Date) -> Unit) {
            val dialogFragment = DateTimePickerDialogFragment.newInstance { configuration ->
                configuration.minDate = Date(item.clockedInSinceInMilliseconds)
                configuration.choose = { date ->
                    onChooseTime(item.asProject(), date)
                }
            }
            fragment.show(dialogFragment)
        }
    }

    object ShowUnableToClockInErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.projects_all_unable_to_clock_in_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    object ShowUnableToClockOutErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.projects_all_unable_to_clock_out_message,
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
                R.string.projects_all_unable_to_delete_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class UpdateNotification(val project: Project) : AllProjectsViewActions(),
        ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            ProjectNotificationService.startServiceWithContext(activity, project)
        }
    }

    data class DismissNotification(val project: Project) : AllProjectsViewActions(),
        ContextViewAction {
        override fun action(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(
                project.id.value.toString(),
                WorkerApplication.NOTIFICATION_ON_GOING_ID
            )
        }
    }
}
