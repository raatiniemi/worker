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

package me.raatiniemi.worker.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectionManager<T> {
    private final Set<T> selectedItems = new HashSet<>();

    public boolean isSelectionActivated() {
        return !selectedItems.isEmpty();
    }

    public List<T> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public boolean isSelected(List<T> results) {
        if (results.isEmpty()) {
            return false;
        }

        boolean isSelected = true;

        for (T result : results) {
            if (!isSelected(result)) {
                isSelected = false;
                break;
            }
        }

        return isSelected;
    }

    public boolean isSelected(T result) {
        return selectedItems.contains(result);
    }

    public void selectItems(List<T> results) {
        // noinspection Convert2streamapi
        for (T result : results) {
            selectItem(result);
        }
    }

    public void selectItem(T result) {
        selectedItems.add(result);
    }

    public void deselectItems(List<T> results) {
        // noinspection Convert2streamapi
        for (T result : results) {
            deselectItem(result);
        }
    }

    public void deselectItem(T result) {
        selectedItems.remove(result);
    }

    public void deselectItems() {
        selectedItems.clear();
    }
}
