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

package me.raatiniemi.worker.project.timesheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.ExpandableListAdapter;
import me.raatiniemi.worker.model.domain.time.Time;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetAdapter extends ExpandableListAdapter<
        Date,
        Time,
        TimesheetAdapter.TimesheetItem,
        TimesheetAdapter.GroupViewHolder,
        TimesheetAdapter.ChildViewHolder
        > {
    private static final String TAG = "TimesheetAdapter";

    private SimpleDateFormat mDateFormat;

    private SimpleDateFormat mTimeFormat;

    private OnTimesheetListener mOnTimesheetListener;

    public TimesheetAdapter(OnTimesheetListener listener) {
        mOnTimesheetListener = listener;

        mDateFormat = new SimpleDateFormat("EEEE (MMMM d)", Locale.getDefault());
        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        setHasStableIds(true);
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new GroupViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ChildViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder vh, int group, int viewType) {
        vh.itemView.setClickable(true);

        Date date = getGroup(group);
        vh.mTitle.setText(mDateFormat.format(date));

        long interval = 0;
        boolean registered = true;

        for (Time time : get(group)) {
            // If a single child is not registered, the group should not be
            // considered registered.
            if (!time.isRegistered()) {
                registered = false;
            }

            interval += time.getInterval();
        }

        vh.itemView.setActivated(registered);

        String summarize = DateIntervalFormat.format(
                interval,
                DateIntervalFormat.Type.FRACTION_HOURS
        );

        Float difference = Float.valueOf(summarize) - 8;
        if (difference != 0) {
            String format = difference > 0 ? " (+%.2f)" : " (%.2f)";
            summarize += String.format(format, difference);
        }

        vh.mSummarize.setText(summarize);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder vh, final int group, final int child, int viewType) {
        final Time time = get(group, child);

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
            vh.itemView.setActivated(time.isRegistered());
        }

        String title = mTimeFormat.format(new Date(time.getStart()));
        if (!time.isActive()) {
            title += " - " + mTimeFormat.format(new Date(time.getStop()));
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
        Date date = getGroup(group);
        return date.getTime();
    }

    @Override
    public long getChildId(int group, int child) {
        Time time = get(group, child);
        return time.getId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder vh, int group, int x, int y, boolean expand) {
        return true;
    }

    public interface OnTimesheetListener {
        boolean onTimeLongClick(View view, TimeInAdapterResult result);
    }

    public static class TimesheetItem extends ExpandableListAdapter.ExpandableItem<Date, Time> {
        public TimesheetItem(Date group) {
            super(group);
        }
    }

    public static class BaseViewHolder extends AbstractExpandableItemViewHolder {
        public RelativeLayout mContainer;

        public TextView mTitle;

        public TextView mSummarize;

        public BaseViewHolder(View view) {
            super(view);
        }
    }

    public static class GroupViewHolder extends BaseViewHolder {
        public GroupViewHolder(View view) {
            super(view);

            mContainer = (RelativeLayout) view.findViewById(R.id.fragment_timesheet_group_item);
            mTitle = (TextView) view.findViewById(R.id.fragment_timesheet_group_item_title);
            mSummarize = (TextView) view.findViewById(R.id.fragment_timesheet_group_item_summarize);
        }
    }

    public static class ChildViewHolder extends BaseViewHolder {
        public ChildViewHolder(View view) {
            super(view);

            mContainer = (RelativeLayout) view.findViewById(R.id.fragment_timesheet_child_item);
            mTitle = (TextView) view.findViewById(R.id.fragment_timesheet_child_item_title);
            mSummarize = (TextView) view.findViewById(R.id.fragment_timesheet_child_item_summarize);
        }
    }

    public final static class TimeInAdapterResult {
        private int mGroup;

        private int mChild;

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
