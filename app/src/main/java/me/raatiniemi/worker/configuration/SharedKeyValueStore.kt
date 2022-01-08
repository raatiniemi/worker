/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.configuration

import android.content.SharedPreferences
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore

class SharedKeyValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore {
    override fun set(key: AppKeys, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(key.rawValue, value)
        }
    }

    override fun set(key: AppKeys, value: Int) {
        sharedPreferences.edit {
            putInt(key.rawValue, value)
        }
    }

    override fun bool(key: AppKeys, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key.rawValue, defaultValue)
    }

    override fun int(key: AppKeys, defaultValue: Int): Int {
        return sharedPreferences.getInt(key.rawValue, defaultValue)
    }
}

private inline fun SharedPreferences.edit(setter: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.setter()
    editor.apply()
}
