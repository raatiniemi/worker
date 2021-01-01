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

package me.raatiniemi.worker.feature.shared.view

import android.view.KeyEvent
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import me.raatiniemi.worker.BuildConfig

internal fun doOnTextChange(tv: TextView, onTextChanged: (String) -> Unit) {
    tv.doOnTextChanged { text, _, _, _ ->
        onTextChanged(
            if (text.isNullOrBlank()) {
                ""
            } else {
                text as String
            }
        )
    }
}

internal fun done(tv: TextView, cb: () -> Unit) {
    tv.setOnEditorActionListener { _, actionId, event ->
        val configuration = EditorAction.from(actionId)
        if (configuration == EditorAction.DONE || (BuildConfig.DEBUG && event.isEnterKey)) {
            cb()
            return@setOnEditorActionListener true
        }

        return@setOnEditorActionListener false
    }
}

private val KeyEvent?.isEnterKey: Boolean
    get() {
        this ?: return false
        return keyCode == KeyEvent.KEYCODE_ENTER
    }
