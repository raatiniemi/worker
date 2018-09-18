/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SelectionManagerTest {
    private SelectionManager<String> selectionManager;

    @Before
    public void setUp() throws Exception {
        selectionManager = new SelectionManager<>();
    }

    @Test
    public void isSelectionActivated_withoutItems() {
        assertFalse(selectionManager.isSelectionActivated());
    }

    @Test
    public void isSelectionActivated_withItems() {
        selectionManager.selectItem("isSelectionActivated_withItems");
        assertTrue(selectionManager.isSelectionActivated());
    }

    @Test
    public void getSelectedItems_withoutItems() {
        assertTrue(selectionManager.getSelectedItems().isEmpty());
    }

    @Test
    public void getSelectedItems_withItems() {
        List<String> selection = new ArrayList<>();
        selection.add("getSelectedItems_withItems");

        selectionManager.selectItems(selection);
        assertEquals(selection, selectionManager.getSelectedItems());
    }

    @Test
    public void isSelected_withoutItems() {
        List<String> selection = new ArrayList<>();

        assertFalse(selectionManager.isSelected(selection));
    }

    @Test
    public void isSelected_withPartialSelection() {
        selectionManager.selectItem("isSelected_withPartialSelection");

        List<String> selection = new ArrayList<>();
        selection.add("isSelected_withPartialSelection");
        selection.add("isSelected_withFullSelection");

        assertFalse(selectionManager.isSelected(selection));
    }

    @Test
    public void isSelected_withFullSelection() {
        selectionManager.selectItem("isSelected_withFullSelection");

        List<String> selection = new ArrayList<>();
        selection.add("isSelected_withFullSelection");

        assertTrue(selectionManager.isSelected(selection));
    }

    @Test
    public void isSelected_withoutItem() {
        assertFalse(selectionManager.isSelected("isSelected_withoutItem"));
    }

    @Test
    public void isSelected_withItem() {
        selectionManager.selectItem("isSelected_withItem");

        assertTrue(selectionManager.isSelected("isSelected_withItem"));
    }

    @Test
    public void selectItems() {
        List<String> selection = new ArrayList<>();
        selection.add("selectItems");
        selection.add("selectItem");

        selectionManager.selectItems(selection);

        assertTrue(selectionManager.isSelected(selection));
    }

    @Test
    public void selectItem() {
        selectionManager.selectItem("selectItem");

        assertTrue(selectionManager.isSelected("selectItem"));
    }

    @Test
    public void deselectItems_withItems() {
        List<String> selection = new ArrayList<>();
        selection.add("deselectItems");
        selection.add("deselectItem");

        selectionManager.selectItems(selection);
        selectionManager.deselectItems(selection);

        assertFalse(selectionManager.isSelected(selection));
    }

    @Test
    public void deselectItem_withItem() {
        selectionManager.selectItem("deselectItem");
        selectionManager.deselectItem("deselectItem");

        assertFalse(selectionManager.isSelected("deselectItem"));
    }

    @Test
    public void deselectItems() {
        selectionManager.selectItem("deselectItems");
        selectionManager.deselectItems();

        assertFalse(selectionManager.isSelectionActivated());
    }
}
