package me.raatiniemi.worker.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import me.raatiniemi.worker.R;

public class ExpandableTimeListAdapter
    extends AbstractExpandableItemAdapter<ExpandableTimeListAdapter.GroupViewHolder, ExpandableTimeListAdapter.ChildViewHolder>
{
    public static class BaseViewHolder extends AbstractExpandableItemViewHolder
    {
        public RelativeLayout mContainer;

        public TextView mTitle;

        public TextView mSummarize;

        public BaseViewHolder(View view)
        {
            super(view);
        }
    }

    public static class GroupViewHolder extends BaseViewHolder
    {
        public GroupViewHolder(View view)
        {
            super(view);

            mContainer = (RelativeLayout) view.findViewById(R.id.fragment_time_list_group_item);
            mTitle = (TextView) view.findViewById(R.id.fragment_time_list_group_item_title);
            mSummarize = (TextView) view.findViewById(R.id.fragment_time_list_group_item_summarize);
        }
    }

    public static class ChildViewHolder extends BaseViewHolder
    {
        public ChildViewHolder(View view)
        {
            super(view);

            mContainer = (RelativeLayout) view.findViewById(R.id.fragment_time_list_child_item);
            mTitle = (TextView) view.findViewById(R.id.fragment_time_list_child_item_title);
            mSummarize = (TextView) view.findViewById(R.id.fragment_time_list_child_item_summarize);
        }
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int i)
    {
        return null;
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder groupViewHolder, int i, int i2)
    {
    }

    @Override
    public int getGroupCount()
    {
        return 0;
    }

    @Override
    public int getGroupItemViewType(int i)
    {
        return 0;
    }

    @Override
    public long getGroupId(int i)
    {
        return 0;
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i)
    {
        return null;
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int i, int i2, int i3)
    {
    }

    @Override
    public int getChildCount(int i)
    {
        return 0;
    }

    @Override
    public int getChildItemViewType(int i, int i2)
    {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2)
    {
        return 0;
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder groupViewHolder, int i, int i2, int i3, boolean b)
    {
        return false;
    }
}
