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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.base.view.adapter.ExpandableListAdapter;
import me.raatiniemi.worker.presentation.model.timesheet.TimeInAdapterResult;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetChildModel;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetGroupModel;

public class TimesheetAdapter extends ExpandableListAdapter<
        Date,
        TimesheetChildModel,
        TimesheetGroupModel,
        TimesheetAdapter.ItemViewHolder,
        TimesheetAdapter.ItemViewHolder
        > {
    private static final String TAG = "TimesheetAdapter";

    private final TimesheetSelectionListener mSelectionListener;
    private Set<TimeInAdapterResult> mSelectedItems = new HashSet<>();

    public TimesheetAdapter(TimesheetSelectionListener selectionListener) {
        mSelectionListener = selectionListener;

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

        final TimeInAdapterResult result = new TimeInAdapterResult(group, child, time);

        // Register the long click listener on the time item.
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isSelected(result)) {
                    return false;
                }

                selectItem(result);
                return true;
            }
        });

        vh.itemView.setSelected(isSelected(result));

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.setActivated(false);
        if (!vh.itemView.isSelected()) {
            vh.itemView.setActivated(item.isRegistered());
        }

        vh.mTitle.setText(item.getTitle());
        vh.mSummarize.setText(item.getTimeSummary());
    }

    private boolean isSelected(TimeInAdapterResult result) {
        return mSelectedItems.contains(result);
    }

    private void selectItem(TimeInAdapterResult result) {
        mSelectedItems.clear();
        mSelectedItems.add(result);

        notifyDataSetChanged();

        mSelectionListener.onSelect();
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
        TimesheetChildModel item = get(group, child);
        return item.getId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(ItemViewHolder vh, int group, int x, int y, boolean expand) {
        return true;
    }

    public List<TimeInAdapterResult> getSelectedItems() {
        return new ArrayList<>(mSelectedItems);
    }

    public void deselectItems() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public interface TimesheetSelectionListener {
        void onSelect();
    }

    class ItemViewHolder extends AbstractExpandableItemViewHolder {
        private TextView mTitle;

        private TextView mSummarize;

        private ItemViewHolder(View view) {
            super(view);
        }
    }
}
