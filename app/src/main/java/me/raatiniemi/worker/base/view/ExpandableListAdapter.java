package me.raatiniemi.worker.base.view;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

abstract public class ExpandableListAdapter<
    G,
    C,
    T extends ExpandableListAdapter.ExpandableItem<G, C>,
    GVH extends ViewHolder, // View holder for the group item
    CVH extends ViewHolder // View holder for the child item
    >
    extends AbstractExpandableItemAdapter<GVH, CVH> {
    private static final String TAG = "ExpandableListAdapter";

    private List<T> mItems;

    public List<T> getItems() {
        if (null == mItems) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return null != getItems() ? getItems().size() : 0;
    }

    @Override
    public int getChildCount(int index) {
        // Check that the group item actually exists
        // before attempting to use it.
        boolean exists = -1 < index && getGroupCount() > index;

        return exists ? getItems().get(index).size() : 0;
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
    public boolean has(int group, int child) {
        return has(group) && 0 <= child && getChildCount(group) > child;
    }

    /**
     * Get the expandable item.
     *
     * @param group Index for the group.
     * @return The expandable item.
     */
    public T get(int group) {
        // Check that the group index exists before
        // attempting to retrieve it.
        if (!has(group)) {
            throw new IndexOutOfBoundsException();
        }

        return getItems().get(group);
    }

    public void add(T item) {
        // Check that the items have been initialized.
        if (null == getItems()) {
            Log.w(TAG, "Unable to add item, items have not been initialized");
            return;
        }

        // Add the item and notify the adapter.
        getItems().add(item);
        notifyDataSetChanged();
    }

    public void add(List<T> items) {
        // Check that the items have been initialized.
        if (null == getItems()) {
            Log.w(TAG, "Unable to add items, items have not been initialized");
            return;
        }

        // Add the items and notify the adapter.
        getItems().addAll(items);
        notifyDataSetChanged();
    }

    public void remove(int group, int child) {
        // Check that the items have been initialized.
        if (null == getItems()) {
            Log.w(TAG, "Unable to remove item, items have not been initialized");
            return;
        }

        // Check that the child index exists.
        if (0 > child || getChildCount(group) < child) {
            Log.w(TAG, "Unable to remove item, it do not exists");
            return;
        }

        // Remove the child item from the group.
        getItems().get(group).remove(child);

        // If there are no more child items within the
        // group, remove the group aswell.
        if (0 == getChildCount(group)) {
            getItems().remove(group);
        }

        // Notify the adapter.
        notifyDataSetChanged();
    }

    public static class ExpandableItem<G, C> extends ArrayList<C> {
        private G mGroup;

        public ExpandableItem(G group) {
            mGroup = group;
        }

        public G getGroup() {
            return mGroup;
        }
    }
}
