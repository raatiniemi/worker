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

package me.raatiniemi.worker.features.projects.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import me.raatiniemi.worker.R;

class ProjectsItemViewHolder extends RecyclerView.ViewHolder {
    final TextView name;
    final TextView time;
    final ImageButton clockActivityToggle;
    final ImageButton clockActivityAt;
    final ImageButton delete;
    final TextView clockedInSince;

    ProjectsItemViewHolder(View view) {
        super(view);

        name = view.findViewById(R.id.fragment_projects_item_name);
        time = view.findViewById(R.id.fragment_projects_item_time);
        clockActivityToggle = view.findViewById(R.id.fragment_projects_item_action_clock_activity_toggle);
        clockActivityAt = view.findViewById(R.id.fragment_projects_item_action_clock_activity_at);
        delete = view.findViewById(R.id.fragment_projects_item_action_delete);
        clockedInSince = view.findViewById(R.id.fragment_projects_item_clocked_in_since);
    }
}
