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

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import timber.log.Timber
import java.util.*

internal class TimePickerFragment : BaseDialogFragment() {
    private var onTimeSetListener: TimePickerDialog.OnTimeSetListener? = null

    override fun isStateValid(): Boolean {
        if (onTimeSetListener == null) {
            Timber.w("No `OnTimeSetListener` have been supplied")
            return false
        }

        return true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = Calendar.getInstance()
        .run {
            TimePickerDialog(
                requireContext(),
                onTimeSetListener,
                get(Calendar.HOUR_OF_DAY),
                get(Calendar.MINUTE),
                DateFormat.is24HourFormat(requireContext())
            )
        }

    companion object {
        @JvmStatic
        fun newInstance(onTimeSetListener: TimePickerDialog.OnTimeSetListener) =
            TimePickerFragment()
                .also { it.onTimeSetListener = onTimeSetListener }
    }
}
