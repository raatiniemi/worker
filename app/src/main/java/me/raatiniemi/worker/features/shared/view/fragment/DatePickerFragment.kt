/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.shared.view.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import timber.log.Timber
import java.util.*

internal class DatePickerFragment : BaseDialogFragment() {
    private var onDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var minDate: Calendar? = null

    override val isStateValid: Boolean
        get() {
            return if (onDateSetListener == null) {
                Timber.w("No `OnDateSetListener` have been supplied")
                false
            } else {
                true
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = Calendar.getInstance()
        .run {
            DatePickerDialog(
                requireContext(),
                onDateSetListener,
                get(Calendar.YEAR),
                get(Calendar.MONTH),
                get(Calendar.DAY_OF_MONTH)
            )
        }
        .also { dialog ->
            minDate?.let { dialog.datePicker.minDate = it.timeInMillis }
        }

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    fun setMinDate(minDate: Calendar) {
        this.minDate = minDate
    }

    companion object {
        @JvmStatic
        fun newInstance(onDateSetListener: DatePickerDialog.OnDateSetListener) =
            DatePickerFragment()
                .also { it.onDateSetListener = onDateSetListener }
    }
}
