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

import me.raatiniemi.worker.RobolectricTestCase
import me.raatiniemi.worker.features.shared.view.adapter.EmptyRecyclerViewAdapter
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SelectionManagerAdapterDecoratorTest : RobolectricTestCase() {
    private val adapter = EmptyRecyclerViewAdapter()
    private val selectionListener: SelectionListener = mock(SelectionListener::class.java)

    private lateinit var selectionManager: SelectionManager<String>

    @Before
    fun setUp() {
        selectionManager = SelectionManagerAdapterDecorator(adapter, selectionListener)
    }

    @Test
    fun selectItem() {
        selectionManager.selectItem("selectItem")

        verify<SelectionListener>(selectionListener).onSelect()
    }

    @Test
    fun deselectItem() {
        selectionManager.deselectItem("deselectItem")

        verify<SelectionListener>(selectionListener).onDeselect()
    }

    @Test
    fun deselectItems() {
        selectionManager.deselectItems()

        verify<SelectionListener>(selectionListener, never()).onDeselect()
    }
}
