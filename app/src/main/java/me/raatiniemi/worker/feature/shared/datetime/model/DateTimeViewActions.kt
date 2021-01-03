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

package me.raatiniemi.worker.feature.shared.datetime.model

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import me.raatiniemi.worker.R
import me.raatiniemi.worker.feature.shared.model.ContextViewAction
import me.raatiniemi.worker.feature.shared.view.hide
import me.raatiniemi.worker.feature.shared.view.show
import me.raatiniemi.worker.feature.shared.view.yearMonthDayHourMinute
import timber.log.Timber
import java.util.*

internal sealed class DateTimeViewActions {
    object ChooseDate : DateTimeViewActions() {
        operator fun invoke(view: View?) {
            try {
                requireNotNull(view) { "No view available for choose date" }

                show(findDatePicker(view))
                hide(findTimePicker(view))
            } catch (e: IllegalArgumentException) {
                Timber.d(e, "Unable to find view for choose date")
            }
        }
    }

    object ChooseTime : DateTimeViewActions() {
        operator fun invoke(view: View?) {
            try {
                requireNotNull(view) { "No view available for choose date" }

                hide(findDatePicker(view))
                show(findTimePicker(view))
            } catch (e: IllegalArgumentException) {
                Timber.d(e, "Unable to find view for choose date")
            }
        }
    }

    abstract class DateTimeIsOutsideOfAllowedInterval : DateTimeViewActions(), ContextViewAction

    data class DateTimeIsIsBeforeAllowedInterval(private val minimumAllowedDate: Date) :
        DateTimeIsOutsideOfAllowedInterval() {
        override fun accept(t: Context) {
            AlertDialog.Builder(t)
                .setTitle(R.string.date_time_picker_time_is_before_allowed_title)
                .setMessage(
                    t.resources.getString(
                        R.string.date_time_picker_time_is_before_allowed_message,
                        yearMonthDayHourMinute(minimumAllowedDate)
                    )
                )
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }

    data class DateTimeIsIsAfterAllowedInterval(private val maximumAllowedDate: Date) :
        DateTimeIsOutsideOfAllowedInterval() {
        override fun accept(t: Context) {
            AlertDialog.Builder(t)
                .setTitle(R.string.date_time_picker_time_is_after_allowed_title)
                .setMessage(
                    t.resources.getString(
                        R.string.date_time_picker_time_is_after_allowed_message,
                        yearMonthDayHourMinute(maximumAllowedDate)
                    )
                )
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }

    data class Choose(private val date: Date) : DateTimeViewActions() {
        operator fun invoke(dialogFragment: DialogFragment, choose: (Date) -> Unit) {
            choose(date)

            dialogFragment.dismiss()
        }
    }
}

private fun findDatePicker(view: View): DatePicker {
    val datePicker = view.findViewById<DatePicker>(R.id.dpDate)

    return requireNotNull(datePicker) { "Unable to find date picker" }
}

private fun findTimePicker(view: View): TimePicker {
    val timePicker = view.findViewById<TimePicker>(R.id.tpTime)

    return requireNotNull(timePicker) { "Unable to find time picker" }
}