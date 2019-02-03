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

import androidx.recyclerview.widget.RecyclerView

class SelectionManagerAdapterDecorator<T>(
        private val adapter: RecyclerView.Adapter<*>,
        private val selectionListener: SelectionListener
) : SelectionManager<T>() {
    override fun selectItem(result: T) {
        super.selectItem(result)

        adapter.notifyDataSetChanged()
        selectionListener.onSelect()
    }

    override fun deselectItem(result: T) {
        super.deselectItem(result)

        adapter.notifyDataSetChanged()
        selectionListener.onDeselect()
    }

    override fun deselectItems() {
        super.deselectItems()

        adapter.notifyDataSetChanged()
    }
}
