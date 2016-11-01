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

package me.raatiniemi.worker.presentation.projects.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.comparator.ProjectComparator;
import me.raatiniemi.worker.presentation.projects.model.ProjectsModel;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;
import me.raatiniemi.worker.presentation.view.adapter.SimpleListAdapter;

/**
 * Adapter for listing available projects.
 */
class ProjectsAdapter extends SimpleListAdapter<ProjectsModel, ProjectsItemViewHolder> {
    /**
     * Listener for project actions.
     */
    private final OnProjectActionListener onProjectActionListener;
    private final Resources resources;

    /**
     * Listener for hinting images.
     */
    private final HintedImageButtonListener hintedImageButtonListener;

    /**
     * Construct the ProjectsAdapter.
     *
     * @param context                   Context to use.
     * @param onProjectActionListener   Listener for project actions.
     * @param hintedImageButtonListener Listener for hinting images.
     */
    ProjectsAdapter(
            Context context,
            OnProjectActionListener onProjectActionListener,
            HintedImageButtonListener hintedImageButtonListener
    ) {
        super(context);

        this.onProjectActionListener = onProjectActionListener;
        this.hintedImageButtonListener = hintedImageButtonListener;
        resources = getContext().getResources();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.fragment_projects_item;
    }

    @Override
    public ProjectsItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ProjectsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProjectsItemViewHolder vh, int index) {
        final ProjectsModel item = get(index);

        vh.name.setText(item.getTitle());
        vh.time.setText(item.getTimeSummary());

        vh.clockedInSince.setText(item.getClockedInSince(resources));
        item.setVisibilityForClockedInSinceView(vh.clockedInSince);

        vh.itemView.setOnClickListener(getOnClickListener());

        vh.clockActivityToggle.setContentDescription(
                item.getHelpTextForClockActivityToggle(resources)
        );
        vh.clockActivityToggle.setOnClickListener(view -> onProjectActionListener.onClockActivityToggle(item));
        vh.clockActivityToggle.setOnLongClickListener(hintedImageButtonListener);
        vh.clockActivityToggle.setActivated(item.isActive());

        vh.clockActivityAt.setContentDescription(
                item.getHelpTextForClockActivityAt(resources)
        );
        vh.clockActivityAt.setOnClickListener(view -> onProjectActionListener.onClockActivityAt(item));
        vh.clockActivityAt.setOnLongClickListener(hintedImageButtonListener);

        vh.delete.setOnClickListener(view -> onProjectActionListener.onDelete(item));
        vh.delete.setOnLongClickListener(hintedImageButtonListener);
    }

    int findProject(final ProjectsModel project) {
        // TODO: Clean up the comparator.
        final ProjectComparator comparator = new ProjectComparator();
        int position = Collections.binarySearch(
                getItems(),
                project,
                (lhs, rhs) -> comparator.compare(lhs.asProject(), rhs.asProject())
        );
        if (0 > position) {
            return RecyclerView.NO_POSITION;
        }

        return position;
    }
}
