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

package me.raatiniemi.worker.features.shared.datetime.model

import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.view.hide
import me.raatiniemi.worker.features.shared.view.show
import timber.log.Timber

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
}

private fun findDatePicker(view: View): DatePicker {
    val datePicker = view.findViewById<DatePicker>(R.id.dpDate)

    return requireNotNull(datePicker) { "Unable to find date picker" }
}

private fun findTimePicker(view: View): TimePicker {
    val timePicker = view.findViewById<TimePicker>(R.id.tpTime)

    return requireNotNull(timePicker) { "Unable to find time picker" }
}