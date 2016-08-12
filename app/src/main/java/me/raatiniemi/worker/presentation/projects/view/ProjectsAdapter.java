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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.comparator.ProjectComparator;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.base.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.presentation.projects.model.ProjectsModel;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;

/**
 * Adapter for listing available projects.
 */
public class ProjectsAdapter extends SimpleListAdapter<ProjectsModel, ProjectsAdapter.ItemViewHolder> {
    /**
     * Listener for project actions.
     */
    private final OnProjectActionListener onProjectActionListener;
    private final Resources resources;

    /**
     * Listener for hinting images.
     */
    private HintedImageButtonListener hintedImageButtonListener;

    /**
     * Construct the ProjectsAdapter.
     *
     * @param context               Context to use.
     * @param projectActionListener Listener for project actions.
     */
    public ProjectsAdapter(
            Context context,
            OnProjectActionListener projectActionListener
    ) {
        super(context);

        onProjectActionListener = projectActionListener;
        resources = getContext().getResources();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.fragment_projects_item;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder vh, int index) {
        final ProjectsModel item = get(index);
        final Project project = item.asProject();

        vh.name.setText(item.getTitle());
        vh.time.setText(item.getTimeSummary());

        vh.clockedInSince.setText(item.getClockedInSince(resources));
        item.setVisibilityForClockedInSinceView(vh.clockedInSince);

        vh.itemView.setOnClickListener(getOnClickListener());

        vh.clockActivityToggle.setContentDescription(
                item.getHelpTextForClockActivityToggle(resources)
        );
        vh.clockActivityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProjectActionListener.onClockActivityToggle(item);
            }
        });
        vh.clockActivityToggle.setOnLongClickListener(hintedImageButtonListener);
        vh.clockActivityToggle.setActivated(project.isActive());

        vh.clockActivityAt.setContentDescription(
                item.getHelpTextForClockActivityAt(resources)
        );
        vh.clockActivityAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProjectActionListener.onClockActivityAt(item);
            }
        });
        vh.clockActivityAt.setOnLongClickListener(hintedImageButtonListener);

        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProjectActionListener.onDelete(item);
            }
        });
        vh.delete.setOnLongClickListener(hintedImageButtonListener);
    }

    public int findProject(final ProjectsModel project) {
        // TODO: Clean up the comparator.
        final ProjectComparator comparator = new ProjectComparator();

        return Collections.binarySearch(getItems(), project, new Comparator<ProjectsModel>() {
            @Override
            public int compare(ProjectsModel lhs, ProjectsModel rhs) {
                return comparator.compare(
                        lhs.asProject(),
                        rhs.asProject()
                );
            }
        });
    }

    /**
     * Set the listener for hinting images.
     *
     * @param hintedImageButtonListener Listener for hinting images.
     */
    public void setHintedImageButtonListener(HintedImageButtonListener hintedImageButtonListener) {
        this.hintedImageButtonListener = hintedImageButtonListener;
    }

    /**
     * View holder for the project item view.
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        /**
         * Field for the project name.
         */
        private final TextView name;

        /**
         * Field for the registered project time.
         */
        private final TextView time;

        /**
         * Icon for toggling the project activity.
         */
        private final ImageButton clockActivityToggle;

        /**
         * Icon for toggling the project activity, with date and time.
         */
        private final ImageButton clockActivityAt;

        /**
         * Icon for deleting project.
         */
        private final ImageButton delete;

        /**
         * Field for the time when the project was clocked in.
         */
        private final TextView clockedInSince;

        private ItemViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.fragment_projects_item_name);
            time = (TextView) view.findViewById(R.id.fragment_projects_item_time);
            clockActivityToggle = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_clock_activity_toggle);
            clockActivityAt = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_clock_activity_at);
            delete = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_delete);
            clockedInSince = (TextView) view.findViewById(R.id.fragment_projects_item_clocked_in_since);
        }
    }

    /**
     * Interface for project actions.
     */
    public interface OnProjectActionListener {
        /**
         * Toggle the clock activity change.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityToggle(@NonNull ProjectsModel project);

        /**
         * Toggle the clock activity change, with date and time.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityAt(@NonNull ProjectsModel project);

        /**
         * Handle project delete action from user.
         *
         * @param project Project to delete.
         */
        void onDelete(@NonNull ProjectsModel project);
    }
}
