package me.raatiniemi.worker.base.view;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;

abstract public class ExpandableListAdapter<
    GVH extends ViewHolder, // View holder for the group item
    CVH extends ViewHolder // View holder for the child item
    >
    extends AbstractExpandableItemAdapter<GVH, CVH> {
    public static class ExpandableItem<C> extends ArrayList<C> {
    }
}
