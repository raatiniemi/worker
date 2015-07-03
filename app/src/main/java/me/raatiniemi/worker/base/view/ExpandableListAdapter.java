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
        return mItems;
    }

    public void setItems(List<T> items) {
        mItems = items;
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
    }
}
