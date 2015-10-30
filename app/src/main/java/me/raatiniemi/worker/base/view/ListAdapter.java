/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.base.view;

import java.util.List;

/**
 * Generic interface for working with list adapters.
 *
 * @param <T> Type of item stored within the list adapter.
 */
public interface ListAdapter<T> {
    /**
     * Get items from the adapter.
     *
     * @return Items from the adapter.
     */
    List<T> getItems();

    /**
     * Set items for the adapter.
     *
     * @param items Items for the adapter.
     */
    void setItems(List<T> items);

    /**
     * Get item from the adapter.
     *
     * @param index Index of the item to get.
     * @return Item at the index.
     */
    T get(int index);

    /**
     * Update item within the adapter.
     *
     * @param index Index of the item to update.
     * @param item  Item to update at the index.
     */
    void set(int index, T item);

    /**
     * Add item to the adapter.
     *
     * @param item Item to add.
     * @return Index of the new item.
     */
    int add(T item);

    /**
     * Add items to the adapter.
     *
     * @param items Items to add.
     * @return Index of the first new item.
     */
    int add(List<T> items);
}
