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

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
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
import me.raatiniemi.worker.presentation.view.widget.LetterDrawable;

public class TimesheetAdapter extends ExpandableListAdapter<
        Date,
        TimesheetChildModel,
        TimesheetGroupModel,
        TimesheetAdapter.GroupItemViewHolder,
        TimesheetAdapter.ItemViewHolder
        > {
    private final TimesheetSelectionListener mSelectionListener;
    private Set<TimeInAdapterResult> mSelectedItems = new HashSet<>();

    public TimesheetAdapter(TimesheetSelectionListener selectionListener) {
        mSelectionListener = selectionListener;

        setHasStableIds(true);
    }

    private static boolean isPointInView(Point point, View view) {
        float x = view.getX();
        float y = view.getY();
        float width = x + view.getWidth();
        float height = y + view.getHeight();

        return !(point.x < x || point.y < y)
                && point.x <= width
                && point.y <= height;
    }

    @Override
    public GroupItemViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        GroupItemViewHolder viewHolder = new GroupItemViewHolder(view);
        viewHolder.mLetter = (ImageView) view.findViewById(R.id.fragment_timesheet_group_item_letter);
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
    public void onBindGroupViewHolder(GroupItemViewHolder vh, int group, int viewType) {
        TimesheetGroupModel item = get(group);

        vh.mTitle.setText(item.getTitle());
        vh.mSummarize.setText(item.getTimeSummaryWithDifference());

        vh.mLetter.setImageDrawable(
                LetterDrawable.build(item.getFirstLetterFromTitle())
        );

        final List<TimeInAdapterResult> results = item.buildItemResultsWithGroupIndex(group);

        vh.mLetter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isSelectionActivated()) {
                    return false;
                }

                selectItems(results);
                return true;
            }
        });

        vh.mLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectionActivated()) {
                    return;
                }

                if (isSelected(results)) {
                    deselectItems(results);
                    return;
                }
                selectItems(results);
            }
        });

        vh.itemView.setSelected(isSelected(results));

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.setActivated(false);
        if (!vh.itemView.isSelected()) {
            vh.itemView.setActivated(item.isRegistered());
        }
    }

    private boolean isSelected(List<TimeInAdapterResult> results) {
        boolean isSelected = true;

        for (TimeInAdapterResult result : results) {
            if (!isSelected(result)) {
                isSelected = false;
                break;
            }
        }

        return isSelected;
    }

    private void selectItems(List<TimeInAdapterResult> results) {
        for (TimeInAdapterResult result : results) {
            selectItem(result);
        }
    }

    private void deselectItems(List<TimeInAdapterResult> results) {
        for (TimeInAdapterResult result : results) {
            deselectItem(result);
        }
    }

    @Override
    public void onBindChildViewHolder(ItemViewHolder vh, final int group, final int child, int viewType) {
        final TimesheetChildModel item = get(group, child);
        final Time time = item.asTime();

        final TimeInAdapterResult result = TimeInAdapterResult.build(group, child, time);

        // Register the long click listener on the time item.
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isSelectionActivated()) {
                    return false;
                }

                if (isSelected(result)) {
                    return false;
                }

                selectItem(result);
                return true;
            }
        });
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectionActivated()) {
                    return;
                }

                if (isSelected(result)) {
                    deselectItem(result);
                    return;
                }

                selectItem(result);
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

    private boolean isSelectionActivated() {
        return !mSelectedItems.isEmpty();
    }

    private boolean isSelected(TimeInAdapterResult result) {
        return mSelectedItems.contains(result);
    }

    private void selectItem(TimeInAdapterResult result) {
        mSelectedItems.add(result);

        notifyDataSetChanged();

        mSelectionListener.onSelect();
    }

    private void deselectItem(TimeInAdapterResult result) {
        mSelectedItems.remove(result);

        notifyDataSetChanged();

        mSelectionListener.onDeselect();
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
    public boolean onCheckCanExpandOrCollapseGroup(GroupItemViewHolder vh, int group, int x, int y, boolean expand) {
        return !isSelectionActivated() || !isPointInView(new Point(x, y), vh.mLetter);
    }

    public void remove(List<TimeInAdapterResult> results) {
        Collections.sort(results);
        Collections.reverse(results);

        for (TimeInAdapterResult result : results) {
            remove(result.getGroup(), result.getChild());
        }
    }

    public void set(List<TimeInAdapterResult> results) {
        Collections.sort(results);

        for (TimeInAdapterResult result: results) {
            set(
                    result.getGroup(),
                    result.getChild(),
                    new TimesheetChildModel(result.getTime())
            );
        }
    }

    public boolean haveSelectedItems() {
        return isSelectionActivated();
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
        void onDeselect();
    }

    class ItemViewHolder extends AbstractExpandableItemViewHolder {
        protected TextView mTitle;

        protected TextView mSummarize;

        private ItemViewHolder(View view) {
            super(view);
        }
    }

    class GroupItemViewHolder extends ItemViewHolder {
        private ImageView mLetter;

        private GroupItemViewHolder(View view) {
            super(view);
        }
    }
}
