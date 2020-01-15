/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.timereport.model

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.feature.projects.timereport.view.TimeReportAdapter
import me.raatiniemi.worker.feature.shared.model.ActivityViewAction
import me.raatiniemi.worker.feature.shared.model.ContextViewAction

internal sealed class TimeReportViewActions {
    data class RefreshTimeReportWeek(private val position: Int) : TimeReportViewActions() {
        fun action(adapter: TimeReportAdapter) {
            adapter.notifyItemChanged(position)
        }
    }

    object ShowUnableToMarkActiveTimeIntervalsAsRegisteredErrorMessage : TimeReportViewActions(),
        ContextViewAction {
        override fun action(context: Context) = AlertDialog.Builder(context)
            .setTitle(R.string.projects_time_report_unable_to_mark_active_items_as_registered_title)
            .setMessage(R.string.projects_time_report_unable_to_mark_active_items_as_registered_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog?.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    object ShowUnableToRegisterErrorMessage : TimeReportViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.projects_time_report_unable_to_register_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    object ShowUnableToDeleteErrorMessage : TimeReportViewActions(), ActivityViewAction {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.projects_time_report_unable_to_delete_message,
                Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }
}
