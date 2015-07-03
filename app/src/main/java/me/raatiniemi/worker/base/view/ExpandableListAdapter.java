package me.raatiniemi.worker.base.view;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

abstract public class ExpandableListAdapter<
    T extends ExpandableListAdapter.ExpandableItem,
    GVH extends ViewHolder, // View holder for the group item
    CVH extends ViewHolder // View holder for the child item
    >
    extends AbstractExpandableItemAdapter<GVH, CVH> {
    private List<T> mItems;

    public List<T> getItems() {
        return mItems;
    }

    public void setItems(List<T> items) {
        mItems = items;
    }

    public static class ExpandableItem<C> extends ArrayList<C> {
    }
}
