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
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimeChild;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimeGroup;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetAdapter extends ExpandableListAdapter<
    TimeGroup,
    TimeChild,
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
        TimeGroup group = item.getGroup();
        holder.mTitle.setText(mDateFormat.format(group.getDate()));

        long interval = 0;

        for (TimeChild child : item) {
            Time time = child.getTime();
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
        TimeChild timeChild = item.get(childPosition);
        final Time time = timeChild.getTime();

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
        TimesheetItem item = getItems().get(groupPosition);
        return item.getGroup().getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        TimeChild item = getItems().get(groupPosition).get(childPosition);
        return item.getId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    public interface OnTimesheetListener {
        boolean onTimeLongClick(View view, TimeInAdapterResult result);
    }

    public static class TimesheetItem extends ExpandableListAdapter.ExpandableItem<TimeGroup, TimeChild> {
        public TimesheetItem(TimeGroup group) {
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

    public static abstract class Data<T> {
        private long mId;

        private T mData;

        public Data(long id, T data) {
            mId = id;
            mData = data;
        }

        public long getId() {
            return mId;
        }

        public T getData() {
            return mData;
        }
    }

    public static class TimeGroup extends Data<Date> {
        public TimeGroup(long id, Date date) {
            super(id, date);
        }

        public long getGroupId() {
            return getId();
        }

        public Date getDate() {
            return getData();
        }
    }

    public static class TimeChild extends Data<Time> {
        public TimeChild(long id, Time time) {
            super(id, time);
        }

        public long getChildId() {
            return getId();
        }

        public Time getTime() {
            return getData();
        }
    }
}
