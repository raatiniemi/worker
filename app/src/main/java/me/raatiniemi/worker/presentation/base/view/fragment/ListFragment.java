/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.presentation.base.view.fragment;

import android.support.annotation.NonNull;

import java.util.List;

import me.raatiniemi.worker.presentation.base.view.adapter.ListAdapter;

/**
 * Interface for fragments with a {@link ListAdapter}.
 *
 * @param <A> Type reference for the {@link ListAdapter}.
 * @param <T> Type reference for the item within the {@link ListAdapter}.
 */
public interface ListFragment<A extends ListAdapter, T> {
    /**
     * Get the adapter connected to the fragment.
     *
     * @return The adapter.
     */
    @NonNull
    A getAdapter();

    /**
     * Get the item from the {@link ListAdapter}.
     *
     * @param index Index of the item.
     * @return Item at the index.
     */
    @NonNull
    T get(int index);

    /**
     * Update item in the {@link ListAdapter}.
     *
     * @param index Index of the item.
     * @param item  Item to update.
     */
    void set(int index, @NonNull T item);

    /**
     * Add item to the {@link ListAdapter}.
     *
     * @param item Item to add.
     * @return Index of the item.
     */
    int add(@NonNull T item);

    /**
     * Add items to the {@link ListAdapter}.
     *
     * @param items Items to add.
     * @return Index of the first item.
     */
    int add(@NonNull List<T> items);

    /**
     * Remove item from the {@link ListAdapter}.
     *
     * @param index Index of the item to remove.
     * @return Removed item.
     */
    @NonNull
    T remove(int index);
}
