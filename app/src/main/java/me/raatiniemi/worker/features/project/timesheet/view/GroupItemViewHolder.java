/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timesheet.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import me.raatiniemi.worker.R;

class GroupItemViewHolder extends AbstractExpandableItemViewHolder {
    final ImageView letter;
    final TextView title;
    final TextView summarize;

    GroupItemViewHolder(View view) {
        super(view);

        letter = view.findViewById(R.id.fragment_timesheet_group_item_letter);
        title = view.findViewById(R.id.fragment_timesheet_group_item_title);
        summarize = view.findViewById(R.id.fragment_timesheet_group_item_summarize);
    }
}
