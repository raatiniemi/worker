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

package me.raatiniemi.worker.feature.shared.view

import android.app.AlertDialog
import android.content.Context
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class ConfirmAction { YES, NO }

object CoroutineConfirmAlertDialog {
    suspend fun build(context: Context, title: Int, message: Int) =
        suspendCoroutine<ConfirmAction> {
            val alert = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    it.resume(ConfirmAction.YES)
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    it.resume(ConfirmAction.NO)
                }
                .create()

            alert.show()
        }
}
