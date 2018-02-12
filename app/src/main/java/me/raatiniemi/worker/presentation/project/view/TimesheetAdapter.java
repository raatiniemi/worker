/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.presentation.project.view;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.domain.util.HoursMinutesFormat;
import me.raatiniemi.worker.presentation.project.model.TimesheetAdapterResult;
import me.raatiniemi.worker.presentation.project.model.TimesheetGroup;
import me.raatiniemi.worker.presentation.util.SelectionListener;
import me.raatiniemi.worker.presentation.util.SelectionManager;
import me.raatiniemi.worker.presentation.util.SelectionManagerAdapterDecorator;
import me.raatiniemi.worker.presentation.view.adapter.ExpandableListAdapter;
import me.raatiniemi.worker.presentation.view.widget.LetterDrawable;

class TimesheetAdapter extends ExpandableListAdapter<
        TimesheetItem,
        TimesheetGroup,
        GroupItemViewHolder,
        ChildItemViewHolder
        > {
    private final HoursMinutesFormat formatter;
    private final SelectionManager<TimesheetAdapterResult> selectionManager;

    TimesheetAdapter(
            HoursMinutesFormat formatter,
            SelectionListener selectionListener
    ) {
        this.formatter = formatter;
        selectionManager = new SelectionManagerAdapterDecorator<>(this, selectionListener);

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
        View view = inflater.inflate(R.layout.fragment_timesheet_group_item, viewGroup, false);

        return new GroupItemViewHolder(view);
    }

    @Override
    public ChildItemViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.fragment_timesheet_child_item, viewGroup, false);

        return new ChildItemViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(GroupItemViewHolder vh, int group, int viewType) {
        TimesheetGroup groupItem = get(group);

        vh.title.setText(groupItem.getTitle());
        vh.summarize.setText(groupItem.getTimeSummaryWithDifference(formatter));

        vh.letter.setImageDrawable(
                LetterDrawable.build(groupItem.getFirstLetterFromTitle())
        );

        final List<TimesheetAdapterResult> results = groupItem.buildItemResultsWithGroupIndex(group);

        vh.letter.setOnLongClickListener(view -> {
            if (selectionManager.isSelectionActivated()) {
                return false;
            }

            selectionManager.selectItems(results);
            return true;
        });

        vh.letter.setOnClickListener(view -> {
            if (!selectionManager.isSelectionActivated()) {
                return;
            }

            if (selectionManager.isSelected(results)) {
                selectionManager.deselectItems(results);
                return;
            }
            selectionManager.selectItems(results);
        });

        vh.itemView.setSelected(selectionManager.isSelected(results));

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.setActivated(false);
        if (!vh.itemView.isSelected()) {
            vh.itemView.setActivated(groupItem.isRegistered());
        }
    }

    @Override
    public void onBindChildViewHolder(ChildItemViewHolder vh, final int group, final int child, int viewType) {
        final TimesheetItem item = get(group, child);

        final TimesheetAdapterResult result = new TimesheetAdapterResult(group, child, item);

        // Register the long click listener on the time item.
        vh.itemView.setOnLongClickListener(view -> {
            if (selectionManager.isSelectionActivated()) {
                return false;
            }

            if (selectionManager.isSelected(result)) {
                return false;
            }

            selectionManager.selectItem(result);
            return true;
        });
        vh.itemView.setOnClickListener(view -> {
            if (!selectionManager.isSelectionActivated()) {
                return;
            }

            if (selectionManager.isSelected(result)) {
                selectionManager.deselectItem(result);
                return;
            }

            selectionManager.selectItem(result);
        });

        vh.itemView.setSelected(selectionManager.isSelected(result));

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.setActivated(false);
        if (!vh.itemView.isSelected()) {
            vh.itemView.setActivated(item.isRegistered());
        }

        vh.title.setText(item.getTitle());
        vh.summarize.setText(item.getTimeSummaryWithFormatter(formatter));
    }

    @Override
    public int getGroupItemViewType(int group) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int group, int child) {
        return 0;
    }

    @Override
    public long getGroupId(int group) {
        TimesheetGroup groupItem = get(group);
        return groupItem.getId();
    }

    @Override
    public long getChildId(int group, int child) {
        TimesheetItem item = get(group, child);
        return item.getId();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupItemViewHolder vh, int group, int x, int y, boolean expand) {
        return !selectionManager.isSelectionActivated() || !isPointInView(new Point(x, y), vh.letter);
    }

    public void remove(List<TimesheetAdapterResult> results) {
        Collections.sort(results);
        Collections.reverse(results);

        //noinspection Convert2streamapi
        for (TimesheetAdapterResult result : results) {
            remove(result);
        }
    }

    public void remove(TimesheetAdapterResult result) {
        remove(result.getGroup(), result.getChild());
    }

    public void set(List<TimesheetAdapterResult> results) {
        Collections.sort(results);

        //noinspection Convert2streamapi
        for (TimesheetAdapterResult result : results) {
            set(result);
        }
    }

    public void set(TimesheetAdapterResult result) {
        set(result.getGroup(), result.getChild(), TimesheetItem.with(result.getTime()));
    }

    boolean haveSelectedItems() {
        return selectionManager.isSelectionActivated();
    }

    List<TimesheetAdapterResult> getSelectedItems() {
        return selectionManager.getSelectedItems();
    }

    void deselectItems() {
        selectionManager.deselectItems();
    }
}
