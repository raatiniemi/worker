package me.raatiniemi.worker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.provider.ExpandableDataProvider.*;
import me.raatiniemi.worker.provider.ExpandableTimeDataProvider;
import me.raatiniemi.worker.provider.ExpandableTimeDataProvider.*;
import me.raatiniemi.worker.util.DateIntervalFormatter;

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

    private ExpandableTimeDataProvider mProvider;

    private SimpleDateFormat mDateFormat;

    private SimpleDateFormat mTimeFormat;

    private DateIntervalFormatter mIntervalFormat;

    public ExpandableTimeListAdapter(ExpandableTimeDataProvider provider)
    {
        mProvider = provider;

        mDateFormat = new SimpleDateFormat("EEEE (d MMMM)", Locale.getDefault());
        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        mIntervalFormat = new DateIntervalFormatter();

        setHasStableIds(true);
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new GroupViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int position, int viewType)
    {
        holder.itemView.setClickable(true);

        TimeGroup group = (TimeGroup) mProvider.getGroupItem(position);
        holder.mTitle.setText(mDateFormat.format(group.getDate()));

        long interval = 0;

        List<Child> childItems = mProvider.getChildItems(position);
        for (Child child: childItems) {
            Time time = ((TimeChild) child).getTime();
            interval += time.getInterval();
        }

        holder.mSummarize.setText(
            mIntervalFormat.format(
                interval,
                DateIntervalFormatter.Type.FRACTION_HOURS
            )
        );
    }

    @Override
    public int getGroupCount()
    {
        return mProvider.getGroupCount();
    }

    @Override
    public int getGroupItemViewType(int position)
    {
        return R.layout.fragment_time_list_group_item;
    }

    @Override
    public long getGroupId(int position)
    {
        return mProvider.getGroupItem(position).getGroupId();
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int position, int viewType)
    {
        TimeChild timeChild = (TimeChild) mProvider.getChildItem(groupPosition, position);
        Time time = timeChild.getTime();

        Date start = new Date(time.getStart());
        Date stop = new Date(time.getStop());

        holder.mTitle.setText(mTimeFormat.format(start) + " - " + mTimeFormat.format(stop));

        holder.mSummarize.setText(
            mIntervalFormat.format(
                time.getInterval(),
                DateIntervalFormatter.Type.FRACTION_HOURS
            )
        );

    }

    @Override
    public int getChildCount(int groupPosition)
    {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public int getChildItemViewType(int groupPosition, int position)
    {
        return R.layout.fragment_time_list_child_item;
    }

    @Override
    public long getChildId(int groupPosition, int position)
    {
        return mProvider.getChildItem(groupPosition, position).getChildId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y, boolean expand)
    {
        return true;
    }
}
