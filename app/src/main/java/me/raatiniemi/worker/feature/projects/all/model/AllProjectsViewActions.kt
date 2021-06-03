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

package me.raatiniemi.worker.feature.projects.all.model

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.constrainedMilliseconds
import me.raatiniemi.worker.domain.time.days
import me.raatiniemi.worker.feature.ongoing.service.ProjectNotificationService
import me.raatiniemi.worker.feature.projects.all.adapter.AllProjectsAdapter
import me.raatiniemi.worker.feature.projects.createproject.view.CreateProjectDialogFragment
import me.raatiniemi.worker.feature.shared.datetime.view.DateTimePickerDialogFragment
import me.raatiniemi.worker.feature.shared.model.ActivityViewAction
import me.raatiniemi.worker.feature.shared.model.ContextViewAction
import me.raatiniemi.worker.feature.shared.model.FragmentViewAction
import me.raatiniemi.worker.feature.shared.view.show
import timber.log.Timber
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal sealed class AllProjectsViewActions {
    object ReloadProjects : AllProjectsViewActions()

    object CreateProject : AllProjectsViewActions() {
        suspend fun apply(fm: FragmentManager): Project? {
            return suspendCoroutine { continuation ->
                show(fm) {
                    CreateProjectDialogFragment.newInstance {
                        continuation.resume(it)
                    }
                }
            }
        }
    }

    object ProjectCreated : AllProjectsViewActions(), ActivityViewAction {
        override fun accept(t: FragmentActivity) {
            val snackBar = Snackbar.make(
                t.findViewById(android.R.id.content),
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

    data class OpenProject(
        private val project: Project
    ) : AllProjectsViewActions(), FragmentViewAction {
        override fun accept(t: Fragment) {
            val arguments = Bundle().apply {
                putLong("projectId", project.id.value)
                putString("projectName", project.name.value)
            }

            t.findNavController()
                .navigate(R.id.navTimeReport, arguments)
        }
    }

    data class ShowConfirmClockOutMessage(val item: ProjectsItem, val date: Date) :
        AllProjectsViewActions()

    data class ChooseDateAndTimeForClockIn(val item: ProjectsItem) : AllProjectsViewActions() {
        suspend fun apply(fm: FragmentManager): Pair<Project, Date>? {
            return suspendCoroutine {
                show(fm) {
                    DateTimePickerDialogFragment.newInstance { configuration ->
                        configuration.choose = { date ->
                            if (date != null) {
                                it.resume(item.asProject() to date)
                            } else {
                                it.resume(null)
                            }
                        }
                    }
                }
            }
        }
    }

    data class ChooseDateAndTimeForClockOut(val item: ProjectsItem) : AllProjectsViewActions() {
        suspend fun apply(fm: FragmentManager): Pair<Project, Date>? {
            return suspendCoroutine {
                show(fm) {
                    DateTimePickerDialogFragment.newInstance { configuration ->
                        val minDate = Milliseconds(item.clockedInSinceInMilliseconds)
                        val maxDate = minDate + 1.days

                        val now = Milliseconds.now
                        val milliseconds = constrainedMilliseconds(now, minDate, maxDate)
                        configuration.date = Date(milliseconds.value)
                        configuration.minDate = Date(minDate.value)
                        configuration.maxDate = Date(maxDate.value)
                        configuration.choose = { date ->
                            if (date != null) {
                                it.resume(item.asProject() to date)
                            } else {
                                it.resume(null)
                            }
                        }
                    }
                }
            }
        }
    }

    object ShowUnableToClockInErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun accept(t: FragmentActivity) {
            val snackBar = Snackbar.make(
                t.findViewById(android.R.id.content),
                R.string.projects_all_unable_to_clock_in_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    object ShowElapsedTimePastAllowedErrorMessage : AllProjectsViewActions(), ContextViewAction {
        override fun accept(t: Context) {
            AlertDialog.Builder(t)
                .setTitle(R.string.projects_all_elapsed_time_past_allowed_title)
                .setMessage(R.string.projects_all_elapsed_time_past_allowed_message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }

    object ShowUnableToClockOutErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun accept(t: FragmentActivity) {
            val snackBar = Snackbar.make(
                t.findViewById(android.R.id.content),
                R.string.projects_all_unable_to_clock_out_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class ShowConfirmRemoveProjectMessage(val item: ProjectsItem) : AllProjectsViewActions()

    object ShowUnableToDeleteProjectErrorMessage : AllProjectsViewActions(), ActivityViewAction {
        override fun accept(t: FragmentActivity) {
            val snackBar = Snackbar.make(
                t.findViewById(android.R.id.content),
                R.string.projects_all_unable_to_delete_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    data class UpdateNotification(val project: Project) : AllProjectsViewActions(),
        ActivityViewAction {
        override fun accept(t: FragmentActivity) {
            ProjectNotificationService.startServiceWithContext(t, project)
        }
    }

    data class DismissNotification(val project: Project) : AllProjectsViewActions(),
        ContextViewAction {
        override fun accept(t: Context) {
            val notificationManager = NotificationManagerCompat.from(t)
            notificationManager.cancel(
                project.id.value.toString(),
                WorkerApplication.NOTIFICATION_ON_GOING_ID
            )
        }
    }
}
