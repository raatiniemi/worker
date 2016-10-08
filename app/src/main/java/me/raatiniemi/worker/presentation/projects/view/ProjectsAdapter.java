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

import butterknife.BindView;
import butterknife.ButterKnife;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.comparator.ProjectComparator;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.model.ProjectsModel;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;
import me.raatiniemi.worker.presentation.view.adapter.SimpleListAdapter;

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
        vh.clockActivityToggle.setOnClickListener(view -> onProjectActionListener.onClockActivityToggle(item));
        vh.clockActivityToggle.setOnLongClickListener(hintedImageButtonListener);
        vh.clockActivityToggle.setActivated(project.isActive());

        vh.clockActivityAt.setContentDescription(
                item.getHelpTextForClockActivityAt(resources)
        );
        vh.clockActivityAt.setOnClickListener(view -> onProjectActionListener.onClockActivityAt(item));
        vh.clockActivityAt.setOnLongClickListener(hintedImageButtonListener);

        vh.delete.setOnClickListener(view -> onProjectActionListener.onDelete(item));
        vh.delete.setOnLongClickListener(hintedImageButtonListener);
    }

    public int findProject(final ProjectsModel project) {
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

    /**
     * Set the listener for hinting images.
     *
     * @param hintedImageButtonListener Listener for hinting images.
     */
    public void setHintedImageButtonListener(HintedImageButtonListener hintedImageButtonListener) {
        this.hintedImageButtonListener = hintedImageButtonListener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fragment_projects_item_name)
        TextView name;

        @BindView(R.id.fragment_projects_item_time)
        TextView time;

        @BindView(R.id.fragment_projects_item_action_clock_activity_toggle)
        ImageButton clockActivityToggle;

        @BindView(R.id.fragment_projects_item_action_clock_activity_at)
        ImageButton clockActivityAt;

        @BindView(R.id.fragment_projects_item_action_delete)
        ImageButton delete;

        @BindView(R.id.fragment_projects_item_clocked_in_since)
        TextView clockedInSince;

        private ItemViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
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
