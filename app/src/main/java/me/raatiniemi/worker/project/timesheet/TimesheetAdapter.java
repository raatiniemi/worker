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
import me.raatiniemi.worker.model.time.Time;
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
    public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition, int viewType) {
        holder.itemView.setClickable(true);

        TimesheetItem item = getItems().get(groupPosition);
        Date date = item.getGroup();
        holder.mTitle.setText(mDateFormat.format(date));

        long interval = 0;

        for (Time time : item) {
            interval += time.getInterval();
        }

        String summarize = DateIntervalFormat.format(
            interval,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        Float difference = Float.valueOf(summarize) - 8;
        if (difference != 0) {
            String format = difference > 0 ? " (+%.2f)" : " (%.2f)";
            summarize += String.format(format, difference);
        }

        holder.mSummarize.setText(summarize);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, final int groupPosition, final int childPosition, int viewType) {
        TimesheetItem item = getItems().get(groupPosition);
        final Time time = item.get(childPosition);

        // Register the long click listener on the time item.
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TimeInAdapterResult result = new TimeInAdapterResult(groupPosition, childPosition, time);
                mOnTimesheetListener.onTimeLongClick(v, result);
                return true;
            }
        });

        String title = mTimeFormat.format(new Date(time.getStart()));
        if (!time.isActive()) {
            title += " - " + mTimeFormat.format(new Date(time.getStop()));
        }
        holder.mTitle.setText(title);

        holder.mSummarize.setText(
            DateIntervalFormat.format(
                time.getInterval(),
                DateIntervalFormat.Type.FRACTION_HOURS
            )
        );

    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return R.layout.fragment_timesheet_group_item;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return R.layout.fragment_timesheet_child_item;
    }

    @Override
    public long getGroupId(int groupPosition) {
        Date item = getItems().get(groupPosition).getGroup();
        return item.getTime();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Time item = getItems().get(groupPosition).get(childPosition);
        return item.getId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
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
    }
}
