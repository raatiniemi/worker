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

package me.raatiniemi.worker.presentation.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base type for the combined group and child item.
 *
 * @param <G> Reference type for the group item.
 * @param <C> Reference type for the child item.
 */
public class ExpandableItem<G, C> {
    private final List<C> items = new ArrayList<>();

    /**
     * Group item.
     */
    private final G group;

    /**
     * Constructor, initialize with the group item.
     *
     * @param group Group item.
     */
    protected ExpandableItem(@NonNull G group) {
        this.group = group;
    }

    /**
     * Get the group item.
     *
     * @return Group item.
     */
    @NonNull
    protected G getGroup() {
        return group;
    }

    public boolean add(C item) {
        return items.add(item);
    }

    public C get(int index) {
        return items.get(index);
    }

    public void set(int index, C item) {
        items.set(index, item);
    }

    public C remove(int index) {
        return items.remove(index);
    }

    public int size() {
        return items.size();
    }

    protected List<C> getItems() {
        return Collections.unmodifiableList(items);
    }
}
