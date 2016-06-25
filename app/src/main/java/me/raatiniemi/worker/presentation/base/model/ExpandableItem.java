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

package me.raatiniemi.worker.presentation.base.model;

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
    private final List<C> mItems = new ArrayList<>();

    /**
     * Group item.
     */
    private final G mGroup;

    /**
     * Constructor, initialize with the group item.
     *
     * @param group Group item.
     */
    protected ExpandableItem(@NonNull G group) {
        mGroup = group;
    }

    /**
     * Get the group item.
     *
     * @return Group item.
     */
    @NonNull
    protected G getGroup() {
        return mGroup;
    }

    public boolean add(C item) {
        return mItems.add(item);
    }

    public C get(int index) {
        return mItems.get(index);
    }

    public void set(int index, C item) {
        mItems.set(index, item);
    }

    public C remove(int index) {
        return mItems.remove(index);
    }

    public int size() {
        return mItems.size();
    }

    protected List<C> getItems() {
        return Collections.unmodifiableList(mItems);
    }
}
