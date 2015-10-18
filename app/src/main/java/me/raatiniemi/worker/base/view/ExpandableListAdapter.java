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
        extends AbstractExpandableItemAdapter<GVH, CVH> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "ExpandableListAdapter";

    /**
     * Data items for the adapter to display.
     */
    private List<T> mItems;

    /**
     * Get the items from the adapater.
     *
     * @return Items from the adapter.
     */
    public List<T> getItems() {
        if (null == mItems) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    /**
     * Set items for the adapter.
     *
     * @param items Items for the adapter.
     */
    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
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
     * Check whether a group index exists.
     *
     * @param group Group index to check.
     * @return True if group index exists, otherwise false.
     */
    public boolean has(int group) {
        return 0 <= group && getGroupCount() > group;
    }

    /**
     * Check whether a child index within a group exists.
     *
     * @param group Index for the group.
     * @param child Child index to check.
     * @return True if child index exists within the group, otherwise false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean has(int group, int child) {
        return has(group) && 0 <= child && getChildCount(group) > child;
    }

    /**
     * Get the combined group and child item at given index.
     *
     * @param group Index for the group.
     * @return Combined group and child item.
     */
    public T get(int group) {
        // Check that the group index exists before
        // attempting to retrieve it.
        if (!has(group)) {
            throw new IndexOutOfBoundsException();
        }

        return getItems().get(group);
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
     * Get the child item at given index.
     *
     * @param group Index for the group.
     * @param child Index for the child.
     * @return Child item.
     */
    public C get(int group, int child) {
        // Check that the group and child indexes exists
        // before attempting to use them.
        if (!has(group, child)) {
            throw new IndexOutOfBoundsException();
        }

        return get(group).get(child);
    }

    /**
     * Add single item to the adapter.
     *
     * @param item Item to add to the adapter.
     */
    public void add(T item) {
        // Add the item and notify the adapter.
        getItems().add(item);
        notifyDataSetChanged();
    }

    /**
     * Add collection of items to the adapter.
     *
     * @param items Collection of items to add to the adapter.
     */
    public void add(List<T> items) {
        // Add the items and notify the adapter.
        getItems().addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Remove group item in the adapter.
     *
     * @param group Index for group to be removed.
     */
    public void remove(int group) {
        // Check that the group index exists.
        if (!has(group)) {
            Log.w(TAG, "Unable to remove group, it do not exists");
            return;
        }

        // Remove the group and notify the change.
        getItems().remove(group);
        notifyDataSetChanged();
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
     * Update group item in the adapter.
     *
     * @param group Index for group to be updated.
     * @param item  Item to update the adapter.
     */
    public void update(int group, T item) {
        // Check that the group index exists.
        if (!has(group)) {
            Log.w(TAG, "Unable to update group, it do not exists");
            return;
        }

        // Update the group item and notify the adapter.
        getItems().set(group, item);
        notifyDataSetChanged();
    }

    /**
     * Update child item in the adapter.
     *
     * @param group Index for group containing the child.
     * @param child Index for child to be updated.
     * @param item  Item to update the adapter.
     */
    public void update(int group, int child, C item) {
        // Check that the group/child index exists.
        if (!has(group, child)) {
            Log.w(TAG, "Unable to update child, it do not exists");
            return;
        }

        // Update the child item within the group item.
        T groupItem = get(group);
        groupItem.set(child, item);

        // Trigger the adapter update on the group item.
        update(group, groupItem);
    }

    /**
     * Clear the items from the adapter.
     */
    public void clear() {
        getItems().clear();
        notifyDataSetChanged();
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
