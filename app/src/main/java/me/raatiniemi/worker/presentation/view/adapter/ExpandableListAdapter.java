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
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.presentation.model.ExpandableItem;

/**
 * Base adapter for working with the expandable RecyclerView.
 *
 * @param <G>   Reference type for the group item.
 * @param <C>   Reference type for the child item.
 * @param <T>   Reference type for the combined group/child item.
 * @param <GVH> Reference type for the group item view holder.
 * @param <CVH> Reference type for the child item view holder.
 */
public abstract class ExpandableListAdapter<
        G,
        C,
        T extends ExpandableItem<G, C>,
        GVH extends ViewHolder,
        CVH extends ViewHolder
        >
        extends AbstractExpandableItemAdapter<GVH, CVH>
        implements ListAdapter<T> {
    /**
     * Available items.
     */
    private final List<T> items = new ArrayList<>();

    /**
     * Get number of groups.
     *
     * @return Number of group items.
     */
    @Override
    public int getGroupCount() {
        return items.size();
    }

    /**
     * Get number of children within a group.
     *
     * @param group Index for group.
     * @return Number of children within a group.
     */
    @Override
    public int getChildCount(int group) {
        return has(group) ? get(group).size() : 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public List<T> getItems() {
        return items;
    }

    /**
     * Check whether an index exists.
     *
     * @param index Group index to check.
     * @return True if index exists, otherwise false.
     */
    private boolean has(int index) {
        // Check if index is within bounds.
        return 0 <= index && getGroupCount() > index;
    }

    /**
     * Check whether a combined index exists.
     *
     * @param index Group index to check.
     * @param child Child index to check.
     * @return True if index exists, otherwise false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean has(int index, int child) {
        // Check if index is within bounds.
        return has(index) && 0 <= child && getChildCount(index) > child;
    }

    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public T get(int index) {
        if (!has(index)) {
            throwGroupIndexOutOfBounds(index);
        }

        return items.get(index);
    }

    private void throwGroupIndexOutOfBounds(int index) {
        throw new IndexOutOfBoundsException(
                "Invalid index " + index + ", size is " + getGroupCount()
        );
    }

    /**
     * Get item at combined index.
     *
     * @param group Index of the group.
     * @param child Index of the child.
     * @return Item at the combined index.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    @NonNull
    protected C get(int group, int child) {
        if (!has(group, child)) {
            throwChildIndexOutOfBounds(group, child);
        }

        return get(group).get(child);
    }

    private void throwChildIndexOutOfBounds(int group, int child) {
        throw new IndexOutOfBoundsException(
                "Invalid index [" + group + "][" + child + "] size is "
                        + "[" + getGroupCount() + "]"
                        + "[" + getChildCount(group) + "]"
        );
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(int index, @NonNull T item) {
        if (!has(index)) {
            throwGroupIndexOutOfBounds(index);
        }

        // Update the item and notify the adapter.
        items.set(index, item);
        notifyDataSetChanged();
    }

    /**
     * Update item at combined index.
     *
     * @param group Index of the group.
     * @param child Index of the child.
     * @param item  Item to update.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    protected void set(int group, int child, @NonNull C item) {
        // Check that the group/child index exists.
        if (!has(group, child)) {
            throwChildIndexOutOfBounds(group, child);
        }

        // Update the child item within the group item.
        T groupItem = get(group);
        groupItem.set(child, item);

        // Trigger the adapter update on the group item.
        set(group, groupItem);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull T item) {
        // Retrieve the index of the new item by retrieving the number of
        // items within the adapter before adding the new item.
        int index = items.size();
        items.add(item);

        // Notify the adapter, a new item have been added.
        notifyItemInserted(index);

        // Return the index for the new item.
        return index;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(final int index, @NonNull final T item) {
        // TODO: Implement adding at specific index for ExpandableListAdapter.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull List<T> items) {
        // Retrieve the current count to have a reference point
        // at which location the new items will be inserted.
        int index = getItemCount();
        this.items.addAll(items);

        // Notify the adapter of the new items.
        notifyDataSetChanged();

        // Return the reference point for the location of the new items.
        return index;
    }

    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public T remove(int index) {
        if (!has(index)) {
            throwGroupIndexOutOfBounds(index);
        }

        // Remove the group and notify the change.
        T item = items.remove(index);
        notifyDataSetChanged();

        return item;
    }

    /**
     * Remove item at combined index.
     *
     * @param group Index of the group.
     * @param child Index of the child.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    protected void remove(int group, int child) {
        if (!has(group, child)) {
            throwChildIndexOutOfBounds(group, child);
        }

        // Remove the child item from the group.
        get(group).remove(child);

        // If there are no more child items within the
        // group, remove the group as well.
        if (0 == getChildCount(group)) {
            remove(group);
        }

        // Notify the adapter.
        notifyDataSetChanged();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}
