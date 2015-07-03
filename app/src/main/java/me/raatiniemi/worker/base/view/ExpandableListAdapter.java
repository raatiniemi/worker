package me.raatiniemi.worker.base.view;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

abstract public class ExpandableListAdapter<
    G,
    C,
    T extends ExpandableListAdapter.ExpandableItem<C>,
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

    public static class ExpandableItem<C> extends ArrayList<C> {
    }
}
