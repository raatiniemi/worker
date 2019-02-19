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

package me.raatiniemi.worker.features.settings.project.model

import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import org.greenrobot.eventbus.EventBus

sealed class ProjectViewActions : ActivityViewAction {
    object ShowTimeSummaryStartingPointChangedToWeek : ProjectViewActions() {
        override fun action(activity: FragmentActivity) {
            with(EventBus.getDefault()) {
                post(TimeSummaryStartingPointChangeEvent())
            }

            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.message_change_time_summary_starting_point_week,
                    Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }

    object ShowTimeSummaryStartingPointChangedToMonth : ProjectViewActions() {
        override fun action(activity: FragmentActivity) {
            with(EventBus.getDefault()) {
                post(TimeSummaryStartingPointChangeEvent())
            }

            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.message_change_time_summary_starting_point_month,
                    Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }

    object ShowUnableToChangeTimeSummaryStartingPointErrorMessage : ProjectViewActions() {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_change_time_summary_starting_point,
                    Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }
}
