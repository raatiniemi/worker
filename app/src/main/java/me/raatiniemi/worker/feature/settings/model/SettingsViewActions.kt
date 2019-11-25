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

package me.raatiniemi.worker.feature.settings.model

import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.feature.shared.model.ActivityViewAction
import org.greenrobot.eventbus.EventBus

internal sealed class SettingsViewActions : ActivityViewAction {
    object ShowTimeSummaryStartingPointChangedToWeek : SettingsViewActions() {
        override fun action(activity: FragmentActivity) {
            with(EventBus.getDefault()) {
                post(TimeSummaryStartingPointChangeEvent())
            }

            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.settings_time_summary_changed_starting_point_to_week_message,
                Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }

    object ShowTimeSummaryStartingPointChangedToMonth : SettingsViewActions() {
        override fun action(activity: FragmentActivity) {
            with(EventBus.getDefault()) {
                post(TimeSummaryStartingPointChangeEvent())
            }

            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.settings_time_summary_changed_starting_point_to_month_message,
                Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }

    object ShowUnableToChangeTimeSummaryStartingPointErrorMessage : SettingsViewActions() {
        override fun action(activity: FragmentActivity) {
            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.settings_time_summary_unable_to_change_starting_point_message,
                Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }
}
