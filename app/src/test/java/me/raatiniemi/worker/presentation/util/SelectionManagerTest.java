/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.util;

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
    private SelectionManager<String> mSelectionManager;

    @Before
    public void setUp() throws Exception {
        mSelectionManager = new SelectionManager<>();
    }

    @Test
    public void isSelectionActivated_withoutItems() {
        assertFalse(mSelectionManager.isSelectionActivated());
    }

    @Test
    public void isSelectionActivated_withItems() {
        mSelectionManager.selectItem("isSelectionActivated_withItems");
        assertTrue(mSelectionManager.isSelectionActivated());
    }

    @Test
    public void getSelectedItems_withoutItems() {
        assertTrue(mSelectionManager.getSelectedItems().isEmpty());
    }

    @Test
    public void getSelectedItems_withItems() {
        List<String> selection = new ArrayList<>();
        selection.add("getSelectedItems_withItems");

        mSelectionManager.selectItems(selection);
        assertEquals(selection, mSelectionManager.getSelectedItems());
    }

    @Test
    public void isSelected_withoutItems() {
        List<String> selection = new ArrayList<>();

        assertFalse(mSelectionManager.isSelected(selection));
    }

    @Test
    public void isSelected_withPartialSelection() {
        mSelectionManager.selectItem("isSelected_withPartialSelection");

        List<String> selection = new ArrayList<>();
        selection.add("isSelected_withPartialSelection");
        selection.add("isSelected_withFullSelection");

        assertFalse(mSelectionManager.isSelected(selection));
    }

    @Test
    public void isSelected_withFullSelection() {
        mSelectionManager.selectItem("isSelected_withFullSelection");

        List<String> selection = new ArrayList<>();
        selection.add("isSelected_withFullSelection");

        assertTrue(mSelectionManager.isSelected(selection));
    }

    @Test
    public void isSelected_withoutItem() {
        assertFalse(mSelectionManager.isSelected("isSelected_withoutItem"));
    }

    @Test
    public void isSelected_withItem() {
        mSelectionManager.selectItem("isSelected_withItem");

        assertTrue(mSelectionManager.isSelected("isSelected_withItem"));
    }

    @Test
    public void selectItems() {
        List<String> selection = new ArrayList<>();
        selection.add("selectItems");
        selection.add("selectItem");

        mSelectionManager.selectItems(selection);

        assertTrue(mSelectionManager.isSelected(selection));
    }

    @Test
    public void selectItem() {
        mSelectionManager.selectItem("selectItem");

        assertTrue(mSelectionManager.isSelected("selectItem"));
    }

    @Test
    public void deselectItems_withItems() {
        List<String> selection = new ArrayList<>();
        selection.add("deselectItems");
        selection.add("deselectItem");

        mSelectionManager.selectItems(selection);
        mSelectionManager.deselectItems(selection);

        assertFalse(mSelectionManager.isSelected(selection));
    }

    @Test
    public void deselectItem_withItem() {
        mSelectionManager.selectItem("deselectItem");
        mSelectionManager.deselectItem("deselectItem");

        assertFalse(mSelectionManager.isSelected("deselectItem"));
    }

    @Test
    public void deselectItems() {
        mSelectionManager.selectItem("deselectItems");
        mSelectionManager.deselectItems();

        assertFalse(mSelectionManager.isSelectionActivated());
    }
}
