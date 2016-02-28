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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.base.view.adapter.ExpandableListAdapter;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetChildModel;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetGroupModel;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetAdapter extends ExpandableListAdapter<
        Date,
        TimesheetChildModel,
        TimesheetGroupModel,
        TimesheetAdapter.ItemViewHolder,
        TimesheetAdapter.ItemViewHolder
        > {
    private static final String TAG = "TimesheetAdapter";

    private final SimpleDateFormat mTimeFormat;

    private final OnTimesheetListener mOnTimesheetListener;

    public TimesheetAdapter(
            @NonNull List<TimesheetGroupModel> items,
            OnTimesheetListener listener
    ) {
        super(items);

        mOnTimesheetListener = listener;

        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        setHasStableIds(true);
    }

    @Override
    public ItemViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        ItemViewHolder viewHolder = new ItemViewHolder(view);
        viewHolder.mTitle = (TextView) view.findViewById(R.id.fragment_timesheet_group_item_title);
        viewHolder.mSummarize = (TextView) view.findViewById(R.id.fragment_timesheet_group_item_summarize);

        return viewHolder;
    }

    @Override
    public ItemViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        ItemViewHolder viewHolder = new ItemViewHolder(view);
        viewHolder.mTitle = (TextView) view.findViewById(R.id.fragment_timesheet_child_item_title);
        viewHolder.mSummarize = (TextView) view.findViewById(R.id.fragment_timesheet_child_item_summarize);

        return viewHolder;
    }

    @Override
    public void onBindGroupViewHolder(ItemViewHolder vh, int group, int viewType) {
        TimesheetGroupModel item = get(group);

        vh.mTitle.setText(item.getTitle());
        vh.mSummarize.setText(item.getTimeSummaryWithDifference());

        vh.itemView.setActivated(item.isRegistered());
        vh.itemView.setClickable(true);
    }

    @Override
    public void onBindChildViewHolder(ItemViewHolder vh, final int group, final int child, int viewType) {
        final TimesheetChildModel item = get(group, child);
        final Time time = item.asTime();

        // Register the long click listener on the time item.
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TimeInAdapterResult result = new TimeInAdapterResult(group, child, time);
                mOnTimesheetListener.onTimeLongClick(v, result);
                return true;
            }
        });

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.setActivated(false);
        if (!vh.itemView.isSelected()) {
            vh.itemView.setActivated(item.isRegistered());
        }

        String title = mTimeFormat.format(new Date(time.getStartInMilliseconds()));
        if (!time.isActive()) {
            title += " - " + mTimeFormat.format(new Date(time.getStopInMilliseconds()));
        }
        vh.mTitle.setText(title);

        vh.mSummarize.setText(
                DateIntervalFormat.format(
                        time.getInterval(),
                        DateIntervalFormat.Type.FRACTION_HOURS
                )
        );

    }

    @Override
    public int getGroupItemViewType(int group) {
        return R.layout.fragment_timesheet_group_item;
    }

    @Override
    public int getChildItemViewType(int group, int child) {
        return R.layout.fragment_timesheet_child_item;
    }

    @Override
    public long getGroupId(int group) {
        TimesheetGroupModel groupModel = get(group);
        return groupModel.getId();
    }

    @Override
    public long getChildId(int group, int child) {
        // TODO: Add delegate method to model for id?
        TimesheetChildModel item = get(group, child);
        Time time = item.asTime();

        return time.getId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(ItemViewHolder vh, int group, int x, int y, boolean expand) {
        return true;
    }

    public interface OnTimesheetListener {
        boolean onTimeLongClick(View view, TimeInAdapterResult result);
    }

    class ItemViewHolder extends AbstractExpandableItemViewHolder {
        private TextView mTitle;

        private TextView mSummarize;

        private ItemViewHolder(View view) {
            super(view);
        }
    }

    public final static class TimeInAdapterResult {
        private final int mGroup;

        private final int mChild;

        private Time mTime;

        public TimeInAdapterResult(int group, int child, Time time) {
            mGroup = group;
            mChild = child;
            mTime = time;
        }

        public int getGroup() {
            return mGroup;
        }

        public int getChild() {
            return mChild;
        }

        public Time getTime() {
            return mTime;
        }

        public void setTime(Time time) {
            mTime = time;
        }
    }
}
