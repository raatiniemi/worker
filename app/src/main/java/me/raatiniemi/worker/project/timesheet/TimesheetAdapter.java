package me.raatiniemi.worker.project.timesheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.Groupable;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.TimeChild;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.TimeGroup;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetAdapter
    extends AbstractExpandableItemAdapter<TimesheetAdapter.GroupViewHolder, TimesheetAdapter.ChildViewHolder> {
    private static final String TAG = "TimesheetAdapter";

    private TimesheetExpandableDataProvider mProvider;

    private SimpleDateFormat mDateFormat;

    private SimpleDateFormat mTimeFormat;

    private OnTimesheetListener mOnTimesheetListener;

    public TimesheetAdapter(TimesheetExpandableDataProvider provider, OnTimesheetListener listener) {
        mProvider = provider;
        mOnTimesheetListener = listener;

        mDateFormat = new SimpleDateFormat("EEEE (MMMM d)", Locale.getDefault());
        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        setHasStableIds(true);
    }

    public void addGroup(Groupable groupable) {
        mProvider.addGroupItem(groupable);
        notifyDataSetChanged();
    }

    public void remove(int groupPosition, int childPosition) {
        mProvider.removeChildItem(groupPosition, childPosition);
        notifyDataSetChanged();
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new GroupViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition, int viewType) {
        holder.itemView.setClickable(true);

        TimeGroup group = mProvider.getGroupItem(groupPosition);
        holder.mTitle.setText(mDateFormat.format(group.getDate()));

        long interval = 0;

        List<TimeChild> childItems = mProvider.getChildItems(groupPosition);
        for (TimeChild child : childItems) {
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
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return R.layout.fragment_timesheet_group_item;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // Register the long click listener on the time item.
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnTimesheetListener.onTimeLongClick(v);
                return true;
            }
        });

        TimeChild timeChild = mProvider.getChildItem(groupPosition, childPosition);
        Time time = timeChild.getTime();

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
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return R.layout.fragment_timesheet_child_item;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    public interface OnTimesheetListener {
        boolean onTimeLongClick(View view);
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
}
