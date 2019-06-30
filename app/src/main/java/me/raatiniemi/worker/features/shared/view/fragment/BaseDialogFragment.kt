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

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R

internal abstract class BaseDialogFragment : DialogFragment() {
    private var onCancelListener: DialogInterface.OnCancelListener? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null

    internal open val isStateValid: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isStateValid) {
            dismissDialogWithInvalidState()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        onCancelListener?.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onDismissListener?.onDismiss(dialog)
    }

    private fun dismissDialogWithInvalidState() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.projects_create_unknown_error_message,
            Snackbar.LENGTH_SHORT
        ).show()

        dismiss()
    }

    internal fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener) {
        this.onCancelListener = onCancelListener
    }

    internal fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        this.onDismissListener = onDismissListener
    }
}
