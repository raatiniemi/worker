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

package me.raatiniemi.worker.presentation.view.adapter;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Generic interface for working with list adapters.
 *
 * @param <T> Type of item stored within the list adapter.
 */
interface ListAdapter<T> {
    /**
     * Get items.
     *
     * @return Available items.
     */
    @NonNull
    List<T> getItems();

    /**
     * Get item at index.
     *
     * @param index Index of item.
     * @return Item at index.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    @NonNull
    T get(int index);

    /**
     * Update item at index.
     *
     * @param index Index of item.
     * @param item  Item to update.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    void set(int index, @NonNull T item);

    /**
     * Add item.
     *
     * @param item Item to add.
     * @return Index of new item.
     */
    int add(@NonNull T item);

    /**
     * Add item at index.
     *
     * @param index Index at which to insert the item.
     * @param item  Item to add.
     */
    void add(int index, @NonNull T item);

    /**
     * Add items.
     *
     * @param items Items to add.
     * @return Index of the first new item.
     */
    int add(@NonNull List<T> items);

    /**
     * Remove item at index.
     *
     * @param index Index of the item.
     * @return Removed item.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    @NonNull
    T remove(int index);

    /**
     * Clear items from the adapter.
     */
    void clear();
}
