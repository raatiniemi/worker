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

import android.view.KeyEvent
import android.widget.EditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.raatiniemi.worker.BuildConfig

fun EditText.onChange(onTextChanged: (String) -> Unit) {
    addTextChangedListener(onChangeTextWatcher { onTextChanged(it) })
}

fun EditText.on(action: EditorAction, cb: suspend () -> Unit) {
    setOnEditorActionListener { _, actionId, event ->
        if (BuildConfig.DEBUG && event.isEnterKey) {
            GlobalScope.launch(Dispatchers.Default) { cb() }
            return@setOnEditorActionListener true
        }

        val configuration = EditorAction.from(actionId)
        if (configuration == action) {
            GlobalScope.launch(Dispatchers.Default) { cb() }
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
