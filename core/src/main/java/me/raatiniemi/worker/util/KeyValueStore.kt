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

package me.raatiniemi.worker.util

interface KeyValueStore {
    fun set(key: AppKeys, value: Boolean)
    fun set(key: AppKeys, value: Int)

    fun bool(key: AppKeys, defaultValue: Boolean): Boolean
    fun int(key: AppKeys, defaultValue: Int = 0): Int

    // TODO: Move configurations to extensions when calling code is in kotlin.

    fun setHideRegisteredTime(value: Boolean) {
        set(AppKeys.HIDE_REGISTERED_TIME, value)
    }

    fun hideRegisteredTime(): Boolean {
        return bool(AppKeys.HIDE_REGISTERED_TIME, false)
    }
}
