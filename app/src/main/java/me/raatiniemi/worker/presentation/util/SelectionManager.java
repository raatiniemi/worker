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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectionManager<T> {
    private Set<T> mSelectedItems = new HashSet<>();

    public boolean isSelectionActivated() {
        return !mSelectedItems.isEmpty();
    }

    public List<T> getSelectedItems() {
        return new ArrayList<>(mSelectedItems);
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
        return mSelectedItems.contains(result);
    }

    public void selectItems(List<T> results) {
        for (T result : results) {
            selectItem(result);
        }
    }

    public void selectItem(T result) {
        mSelectedItems.add(result);
    }

    public void deselectItems(List<T> results) {
        for (T result : results) {
            deselectItem(result);
        }
    }

    public void deselectItem(T result) {
        mSelectedItems.remove(result);
    }

    public void deselectItems() {
        mSelectedItems.clear();
    }
}
