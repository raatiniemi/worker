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

import java.util.*

open class SelectionManager<T> {
    private val _selectedItems = HashSet<T>()

    val isSelectionActivated: Boolean
        get() = _selectedItems.isNotEmpty()

    val selectedItems: List<T>
        get() = _selectedItems.toList()

    fun isSelected(results: List<T>): Boolean {
        if (results.isEmpty()) {
            return false
        }

        return _selectedItems.containsAll(results)
    }

    fun isSelected(result: T): Boolean {
        return _selectedItems.contains(result)
    }

    fun selectItems(results: List<T>) {
        results.forEach { selectItem(it) }
    }

    open fun selectItem(result: T) {
        _selectedItems.add(result)
    }

    fun deselectItems(results: List<T>) {
        results.forEach { deselectItem(it) }
    }

    open fun deselectItem(result: T) {
        _selectedItems.remove(result)
    }

    open fun deselectItems() {
        _selectedItems.clear()
    }
}
