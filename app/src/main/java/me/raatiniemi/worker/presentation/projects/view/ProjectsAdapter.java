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

package me.raatiniemi.worker.presentation.projects.view;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.projects.model.ClockActivityAtEvent;
import me.raatiniemi.worker.presentation.projects.model.ClockActivityToggleEvent;
import me.raatiniemi.worker.presentation.projects.model.DeleteProjectEvent;
import me.raatiniemi.worker.presentation.projects.model.ProjectActionEvent;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItemAdapterResult;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;
import me.raatiniemi.worker.presentation.util.RxBus;
import me.raatiniemi.worker.presentation.view.adapter.SimpleListAdapter;

/**
 * Adapter for listing available projects.
 */
class ProjectsAdapter extends SimpleListAdapter<ProjectsItem, ProjectsItemViewHolder> {
    private final OnProjectActionListener onProjectActionListener;
    private final HintedImageButtonListener hintedImageButtonListener;

    private final Resources resources;
    private final RxBus<ProjectActionEvent> rxBus = new RxBus<>();

    /**
     * Construct the ProjectsAdapter.
     *
     * @param resources                 Resources available.
     * @param onProjectActionListener   Listener for project actions.
     * @param hintedImageButtonListener Listener for hinting images.
     */
    ProjectsAdapter(
            Resources resources,
            OnProjectActionListener onProjectActionListener,
            HintedImageButtonListener hintedImageButtonListener
    ) {
        this.onProjectActionListener = onProjectActionListener;
        this.hintedImageButtonListener = hintedImageButtonListener;

        this.resources = resources;
        rxBus.toObservable()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(this::propagateActionWithEvent);
    }

    private void propagateActionWithEvent(ProjectActionEvent event) {
        if (event instanceof ClockActivityToggleEvent) {
            this.onProjectActionListener.onClockActivityToggle(event.getResult());
            return;
        }

        if (event instanceof ClockActivityAtEvent) {
            this.onProjectActionListener.onClockActivityAt(event.getResult());
            return;
        }

        this.onProjectActionListener.onDelete(event.getResult());
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
        final ProjectsItem item = get(index);
        final ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(index, item);

        vh.name.setText(item.getTitle());
        vh.time.setText(item.getTimeSummary());

        vh.clockedInSince.setText(item.getClockedInSince(resources));
        item.setVisibilityForClockedInSinceView(vh.clockedInSince);

        vh.itemView.setOnClickListener(getOnClickListener());

        vh.clockActivityToggle.setContentDescription(
                item.getHelpTextForClockActivityToggle(resources)
        );
        vh.clockActivityToggle.setOnClickListener(view ->
                rxBus.send(ClockActivityToggleEvent.withAdapterResult(result)));
        vh.clockActivityToggle.setOnLongClickListener(hintedImageButtonListener);
        vh.clockActivityToggle.setActivated(item.isActive());

        vh.clockActivityAt.setContentDescription(
                item.getHelpTextForClockActivityAt(resources)
        );
        vh.clockActivityAt.setOnClickListener(view ->
                rxBus.send(ClockActivityAtEvent.withAdapterResult(result)));
        vh.clockActivityAt.setOnLongClickListener(hintedImageButtonListener);

        vh.delete.setOnClickListener(view ->
                rxBus.send(DeleteProjectEvent.withAdapterResult(result)));
        vh.delete.setOnLongClickListener(hintedImageButtonListener);
    }
}
