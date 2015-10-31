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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base adapter for working with the expandable RecyclerView.
 *
 * @param <G>   Reference type for the group item.
 * @param <C>   Reference type for the child item.
 * @param <T>   Reference type for the combined group/child item.
 * @param <GVH> Reference type for the group item view holder.
 * @param <CVH> Reference type for the child item view holder.
 */
abstract public class ExpandableListAdapter<
        G,
        C,
        T extends ExpandableListAdapter.ExpandableItem<G, C>,
        GVH extends ViewHolder,
        CVH extends ViewHolder
        >
        extends AbstractExpandableItemAdapter<GVH, CVH>
        implements ListAdapter<T> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "ExpandableListAdapter";

    /**
     * Data items for the adapter to display.
     */
    private List<T> mItems;

    /**
     * Constructor.
     *
     * @param items Items for the adapter.
     */
    public ExpandableListAdapter(@NonNull List<T> items) {
        mItems = items;
    }

    /**
     * Retrieve the number of group items.
     *
     * @return Number of group items.
     */
    @Override
    public int getGroupCount() {
        return getItems().size();
    }

    /**
     * Retrieve the number of child items within a group.
     *
     * @param group Index for the group.
     * @return Number of child items within the group.
     */
    @Override
    public int getChildCount(int group) {
        return has(group) ? get(group).size() : 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<T> getItems() {
        return mItems;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    /**
     * Check whether an index exists.
     *
     * @param index Index to check.
     * @return True if index exists, otherwise false.
     */
    protected boolean has(int index) {
        // Check if index is within bounds.
        return 0 <= index && getGroupCount() > index;
    }

    /**
     * Check whether a combined index exists.
     *
     * @param index Index to check.
     * @param child Child index to check.
     * @return True if index exists, otherwise false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean has(int index, int child) {
        // Check if index is within bounds.
        return has(index) && 0 <= child && getChildCount(index) > child;
    }

    /**
     * @inheritDoc
     */
    @Override
    public T get(int index) {
        if (!has(index)) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + ", size is " + getGroupCount()
            );
        }

        return getItems().get(index);
    }

    /**
     * Get item with combined index.
     *
     * @param group Index of the group.
     * @param child Index of the child.
     * @return Item at the combined index.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    public C get(int group, int child) {
        if (!has(group, child)) {
            throw new IndexOutOfBoundsException(
                    "Invalid index [" + group + "][" + child + "] size is "
                            + "[" + getGroupCount() + "]"
                            + "[" + getChildCount(group) + "]"
            );
        }

        return get(group).get(child);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(int index, T item) {
        if (!has(index)) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + ", size is " + getGroupCount()
            );
        }

        // Update the item and notify the adapter.
        getItems().set(index, item);
        notifyDataSetChanged();
    }

    /**
     * Update child item in the adapter.
     *
     * @param group Index for group containing the child.
     * @param child Index for child to be updated.
     * @param item  Item to update the adapter.
     * @throws IndexOutOfBoundsException if index do not exists.
     */
    public void set(int group, int child, C item) {
        // Check that the group/child index exists.
        if (!has(group, child)) {
            throw new IndexOutOfBoundsException(
                    "Invalid index [" + group + "][" + child + "] size is "
                            + "[" + getGroupCount() + "]"
                            + "[" + getChildCount(group) + "]"
            );
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
    public int add(T item) {
        // Retrieve the index of the new item by retrieving the number of
        // items within the adapter before adding the new item.
        int index = getItems().size();
        getItems().add(item);

        // Notify the adapter, a new item have been added.
        notifyItemInserted(index);

        // Return the index for the new item.
        return index;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(List<T> items) {
        // Retrieve the current count to have a reference point
        // at which location the new items will be inserted.
        int index = getItemCount();
        getItems().addAll(items);

        // Notify the adapter of the new items.
        notifyDataSetChanged();

        // Return the reference point for the location of the new items.
        return index;
    }

    /**
     * @inheritDoc
     */
    @Override
    public T remove(int index) {
        if (!has(index)) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + ", size is " + getGroupCount()
            );
        }

        // Remove the group and notify the change.
        T item = getItems().remove(index);
        notifyDataSetChanged();

        return item;
    }

    /**
     * Remove child item from group in the adapter.
     *
     * @param group Index for group containing the child.
     * @param child Index for child to be removed.
     */
    public void remove(int group, int child) {
        // Check that the child index exists.
        if (!has(group, child)) {
            Log.w(TAG, "Unable to remove item, it do not exists");
            return;
        }

        // Remove the child item from the group.
        get(group).remove(child);

        // If there are no more child items within the
        // group, remove the group aswell.
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
        getItems().clear();
        notifyDataSetChanged();
    }

    /**
     * Get the group item at given index.
     *
     * @param group Index for the group.
     * @return Group item.
     */
    public G getGroup(int group) {
        return get(group).getGroup();
    }

    /**
     * Base type for the combined group and child item.
     *
     * @param <G> Reference type for the group item.
     * @param <C> Reference type for the child item.
     */
    public static class ExpandableItem<G, C> extends ArrayList<C> {
        /**
         * Group item.
         */
        private final G mGroup;

        /**
         * Constructor, initialize with the group item.
         *
         * @param group Group item.
         */
        public ExpandableItem(G group) {
            mGroup = group;
        }

        /**
         * Get the group item.
         *
         * @return Group item.
         */
        public G getGroup() {
            return mGroup;
        }
    }
}
