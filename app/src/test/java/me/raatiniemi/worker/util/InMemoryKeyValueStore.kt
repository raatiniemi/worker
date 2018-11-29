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

package me.raatiniemi.worker.util

class InMemoryKeyValueStore : KeyValueStore {
    private var store = mutableMapOf<String, Any>()

    override fun set(key: String, value: Boolean) {
        store[key] = value
    }

    override fun set(key: String, value: Int) {
        store[key] = value
    }

    override fun bool(key: String, defaultValue: Boolean): Boolean {
        val value = store[key]
        if (value is Boolean) {
            return value
        }

        return defaultValue
    }

    override fun int(key: String, defaultValue: Int): Int {
        val value = store[key]
        if (value is Int) {
            return value
        }

        return defaultValue
    }
}
