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

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SelectionManagerTest {
    private lateinit var selectionManager: SelectionManager<String>

    @Before
    fun setUp() {
        selectionManager = SelectionManager()
    }

    @Test
    fun isSelectionActivated_withoutItems() {
        val actual = selectionManager.isSelectionActivated

        assertFalse(actual)
    }

    @Test
    fun isSelectionActivated_withItems() {
        selectionManager.selectItem("isSelectionActivated_withItems")

        val actual = selectionManager.isSelectionActivated

        assertTrue(actual)
    }

    @Test
    fun getSelectedItems_withoutItems() {
        val expected = emptyList<String>()

        val actual = selectionManager.selectedItems

        assertEquals(expected, actual)
    }

    @Test
    fun getSelectedItems_withItems() {
        val expected = listOf("getSelectedItems_withItems")

        selectionManager.selectItems(expected)

        val actual = selectionManager.selectedItems
        assertEquals(expected, actual)
    }

    @Test
    fun isSelected_withoutItems() {
        val selection = emptyList<String>()

        val actual = selectionManager.isSelected(selection)

        assertFalse(actual)
    }

    @Test
    fun isSelected_withPartialSelection() {
        selectionManager.selectItem("isSelected_withPartialSelection")

        val actual = selectionManager.isSelected(
                listOf(
                        "isSelected_withPartialSelection",
                        "isSelected_withFullSelection"
                )
        )

        assertFalse(actual)
    }

    @Test
    fun isSelected_withFullSelection() {
        selectionManager.selectItem("isSelected_withFullSelection")

        val actual = selectionManager.isSelected(
                listOf("isSelected_withFullSelection")
        )

        assertTrue(actual)
    }

    @Test
    fun isSelected_withoutItem() {
        val actual = selectionManager.isSelected("isSelected_withoutItem")

        assertFalse(actual)
    }

    @Test
    fun isSelected_withItem() {
        selectionManager.selectItem("isSelected_withItem")

        val actual = selectionManager.isSelected("isSelected_withItem")

        assertTrue(actual)
    }

    @Test
    fun selectItems() {
        val selection = listOf("selectItems", "selectItem")

        selectionManager.selectItems(selection)

        val actual = selectionManager.isSelected(selection)
        assertTrue(actual)
    }

    @Test
    fun selectItem() {
        selectionManager.selectItem("selectItem")

        val actual = selectionManager.isSelected("selectItem")

        assertTrue(actual)
    }

    @Test
    fun deselectItems_withItems() {
        val selection = listOf("deselectItems", "deselectItem")
        selectionManager.selectItems(selection)

        selectionManager.deselectItems(selection)

        val actual = selectionManager.isSelected(selection)
        assertFalse(actual)
    }

    @Test
    fun deselectItem_withItem() {
        selectionManager.selectItem("deselectItem")

        selectionManager.deselectItem("deselectItem")

        val actual = selectionManager.isSelected("deselectItem")
        assertFalse(actual)
    }

    @Test
    fun deselectItems() {
        selectionManager.selectItem("deselectItems")

        selectionManager.deselectItems()

        val actual = selectionManager.isSelectionActivated
        assertFalse(actual)
    }
}
